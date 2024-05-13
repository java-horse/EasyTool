package easy.action.api;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import easy.api.config.ApidocxConfig;
import easy.api.model.common.Api;
import easy.api.parse.ApiParser;
import easy.api.parse.model.ClassApiData;
import easy.api.parse.model.MethodApiData;
import easy.base.ApiDocConstants;
import easy.base.Constants;
import easy.util.NotifyUtil;
import easy.util.PropertiesUtil;
import easy.util.PsiElementUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;

public abstract class AbstractAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FileDocumentManager.getInstance().saveAllDocuments();
        EventData data = EventData.of(e);
        if (!data.shouldHandle()) {
            return;
        }
        StepResult<ApidocxConfig> configResult = resolveConfig();
        if (!configResult.isContinue()) {
            return;
        }
        if (!before(e)) {
            return;
        }
        StepResult<List<Api>> apisResult = parse(data, configResult.getData());
        if (!apisResult.isContinue()) {
            return;
        }
        handle(e, apisResult.getData());
    }

    /**
     * ApiDoc前置检查处理
     *
     * @param event 事件
     * @return boolean
     * @author mabin
     * @date 2024/05/11 14:55
     */
    public boolean before(AnActionEvent event) {
        return true;
    }


    /**
     * ApiDoc文档分发处理
     *
     * @param event 事件
     * @param apis  原料药
     * @author mabin
     * @date 2024/05/11 14:55
     */
    public abstract void handle(AnActionEvent event, List<Api> apis);

    /**
     * 解析文档模型数据
     */
    private StepResult<List<Api>> parse(EventData data, ApidocxConfig config) {
        ApiParser parser = new ApiParser(data.project, data.module, config);
        // 选中方法
        if (Objects.nonNull(data.selectedMethod)) {
            MethodApiData methodData = parser.parse(data.selectedMethod);
            if (!methodData.isValid()) {
                NotifyUtil.notify(String.format("The current method [%s] is not a valid api or ignored", data.selectedMethod.getName()), NotificationType.WARNING);
                return StepResult.stop();
            }
            if (config.isStrict() && StringUtils.isEmpty(methodData.getDeclaredApiSummary())) {
                NotifyUtil.notify(String.format("The current method [%s] must declare summary", data.selectedMethod.getName()), NotificationType.WARNING);
                return StepResult.stop();
            }
            return StepResult.ok(methodData.getApis());
        }
        // 选中类
        if (data.selectedClass != null) {
            ClassApiData controllerData = parser.parse(data.selectedClass);
            if (!controllerData.isValid()) {
                NotifyUtil.notify(String.format("The current class [%s] is not a valid controller or ignored", data.selectedClass.getName()), NotificationType.WARNING);
                return StepResult.stop();
            }
            if (config.isStrict() && StringUtils.isEmpty(controllerData.getDeclaredCategory())) {
                NotifyUtil.notify(String.format("The current class [%s] must declare category", data.selectedClass.getName()), NotificationType.WARNING);
                return StepResult.stop();
            }
            return StepResult.ok(controllerData.getApis());
        }
        // 选中Java文件
        List<PsiClass> controllers = PsiElementUtil.getPsiClassByFile(data.selectedJavaFiles);
        if (controllers.isEmpty()) {
            NotifyUtil.notify("Not found valid controller class", NotificationType.WARNING);
            return StepResult.stop();
        }
        List<Api> apis = Lists.newLinkedList();
        for (PsiClass controller : controllers) {
            ClassApiData controllerData = parser.parse(controller);
            if (!controllerData.isValid()) {
                continue;
            }
            if (config.isStrict() && StringUtils.isEmpty(controllerData.getDeclaredCategory())) {
                continue;
            }
            List<Api> controllerApis = controllerData.getApis();
            if (config.isStrict()) {
                controllerApis = controllerApis.stream().filter(o -> StringUtils.isNotEmpty(o.getSummary())).toList();
            }
            apis.addAll(controllerApis);
        }
        return StepResult.ok(apis);
    }

    /**
     * 获取内部配置
     *
     * @return {@link easy.action.api.AbstractAction.StepResult<easy.api.config.ApidocxConfig>}
     * @author mabin
     * @date 2024/05/11 16:00
     */
    private StepResult<ApidocxConfig> resolveConfig() {
        ApidocxConfig config = ApidocxConfig.fromProperties(PropertiesUtil.getProperties(ApiDocConstants.CONFIG_FILE.ROOT_CONFIG));
        return StepResult.ok(config);
    }

    /**
     * 异步上传模板方法
     *
     * @param project       项目
     * @param apis          待处理接口列表
     * @param apiHandle     单个接口数据消费者
     * @param afterCallback 所有接口列表处理完毕后的回调执行，用于关闭资源
     */
    protected void handleUploadAsync(Project project, List<Api> apis, String actionText, Function<Api, ApiUploadResult> apiHandle, Supplier<?> afterCallback) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, Constants.PLUGIN_NAME) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                int poolSize = apis.size() == 1 ? 1 : 4;
                // 进度和并发
                Semaphore semaphore = new Semaphore(poolSize);
                ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
                double step = 1.0 / apis.size();
                AtomicInteger count = new AtomicInteger();
                AtomicDouble fraction = new AtomicDouble();

                List<ApiUploadResult> urls = null;
                try {
                    List<Future<ApiUploadResult>> futures = Lists.newArrayListWithExpectedSize(apis.size());
                    for (int i = 0; i < apis.size() && !indicator.isCanceled(); i++) {
                        Api api = apis.get(i);
                        semaphore.acquire();
                        Future<ApiUploadResult> future = threadPool.submit(() -> {
                            try {
                                String text = format("[%d/%d] %s %s", count.incrementAndGet(), apis.size(), api.getMethod(), api.getPath());
                                indicator.setText(text);
                                return apiHandle.apply(api);
                            } catch (Exception e) {
                                NotifyUtil.notify(String.format("%s upload failed: [%s %s], reason: %s", actionText, api.getMethod(), api.getPath(), ExceptionUtil.getMessage(e)));
                            } finally {
                                indicator.setFraction(fraction.addAndGet(step));
                                semaphore.release();
                            }
                            return null;
                        });
                        futures.add(future);
                    }
                    urls = waitFuturesSilence(futures).stream().filter(Objects::nonNull).collect(Collectors.toList());
                } catch (InterruptedException e) {
                    // ignore
                } finally {
                    if (urls != null && !urls.isEmpty()) {
                        ApiUploadResult uploadResult = urls.get(0);
                        String url = urls.size() == 1 ? uploadResult.getApiUrl() : uploadResult.getCategoryUrl();
                        if (url != null && !url.isEmpty()) {
                            NotifyUtil.notify(String.format("%s upload successful, <a href=\"%s\">%s</a>", actionText, url, url));
                        } else {
                            NotifyUtil.notify(String.format("%s upload successful", actionText));
                        }
                    }
                    threadPool.shutdown();
                    if (afterCallback != null) {
                        afterCallback.get();
                    }
                }
            }
        });
    }

    /**
     * 等待异步任务
     *
     * @param futures 期货
     * @return {@link java.util.List<T>}
     * @author mabin
     * @date 2024/05/11 14:54
     */
    public static <T> List<T> waitFuturesSilence(List<Future<T>> futures) {
        List<T> values = Lists.newArrayListWithExpectedSize(futures.size());
        for (Future<T> future : futures) {
            try {
                T value = future.get();
                if (value != null) {
                    values.add(value);
                }
            } catch (InterruptedException | ExecutionException e) {
                // ignore
            }
        }
        return values;
    }


    /**
     * api上载结果
     *
     * @author mabin
     * @project EasyTool
     * @package easy.action.api.AbstractAction
     * @date 2024/05/11 18:02
     */
    public static class ApiUploadResult {

        private String categoryUrl;
        private String apiUrl;

        public String getCategoryUrl() {
            return categoryUrl;
        }

        public void setCategoryUrl(String categoryUrl) {
            this.categoryUrl = categoryUrl;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }
    }

    /**
     * 组装事件数据实例
     *
     * @author mabin
     * @project EasyTool
     * @package easy.action.api.AbstractAction
     * @date 2024/05/11 14:10
     */
    protected static class EventData {

        AnActionEvent event;
        Project project;
        Module module;
        VirtualFile[] selectedFiles;
        List<PsiJavaFile> selectedJavaFiles;
        PsiClass selectedClass;
        PsiMethod selectedMethod;

        public boolean shouldHandle() {
            return project != null && module != null && (selectedJavaFiles != null || selectedClass != null);
        }


        /**
         * 解析&组装需要的通用数据
         *
         * @param event 事件
         * @return {@link easy.action.api.AbstractAction.EventData}
         * @author mabin
         * @date 2024/05/11 14:10
         */
        public static EventData of(AnActionEvent event) {
            EventData data = new EventData();
            data.event = event;
            data.project = event.getData(CommonDataKeys.PROJECT);
            data.module = event.getData(LangDataKeys.MODULE);
            data.selectedFiles = event.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
            if (data.project != null && data.selectedFiles != null) {
                data.selectedJavaFiles = PsiElementUtil.getPsiJavaFiles(data.project, data.selectedFiles);
            }
            Editor editor = event.getDataContext().getData(CommonDataKeys.EDITOR);
            PsiFile editorFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
            if (editor != null && editorFile != null) {
                PsiElement referenceAt = editorFile.findElementAt(editor.getCaretModel().getOffset());
                data.selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
                data.selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
            }
            return data;
        }
    }


    /**
     * 步骤的执行结果
     *
     * @author mabin
     * @project EasyTool
     * @package easy.action.api.AbstractAction
     * @date 2024/05/11 18:02
     */
    public static class StepResult<T> {

        private final StepType type;
        private T data;

        public enum StepType {
            CONTINUE, STOP
        }

        public boolean isContinue() {
            return type == StepType.CONTINUE;
        }

        public StepResult(StepType type, T data) {
            this.type = type;
            this.data = data;
        }

        public static <T> StepResult<T> ok(T data) {
            return new StepResult<>(StepResult.StepType.CONTINUE, data);
        }

        public static <T> StepResult<T> stop() {
            return new StepResult<>(StepResult.StepType.STOP, null);
        }

        public T getData() {
            return data;
        }
    }

}
