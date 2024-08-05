package easy.action.doc;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.NumberUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import easy.base.Constants;
import easy.config.doc.JavaDocConfig;
import easy.config.doc.JavaDocConfigComponent;
import easy.doc.service.JavaDocGenerateService;
import easy.doc.service.JavaDocWriterService;
import easy.enums.ExtraPackageNameEnum;
import easy.form.doc.BatchJavaDocCheckView;
import easy.helper.ServiceHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class JavaDocBatchAction extends DumbAwareAction {
    private final JavaDocConfig javaDocConfig = ServiceHelper.getService(JavaDocConfigComponent.class).getState();
    private final JavaDocGenerateService javaDocGenerateService = ServiceHelper.getService(JavaDocGenerateService.class);
    private final JavaDocWriterService javaDocWriterService = ServiceHelper.getService(JavaDocWriterService.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project) || Objects.isNull(javaDocConfig)) {
            return;
        }
        VirtualFile[] virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        if (Objects.isNull(virtualFiles) || virtualFiles.length == 0) {
            return;
        }
        List<PsiClass> psiClassList = new ArrayList<>();
        PsiManager psiManager = PsiManager.getInstance(project);
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile psiFile = psiManager.findFile(virtualFile);
            if (Objects.isNull(psiFile) || !(psiFile instanceof PsiJavaFile psiJavaFile)) {
                continue;
            }
            psiClassList.addAll(Arrays.stream(psiJavaFile.getClasses()).toList());
        }
        if (CollectionUtils.isEmpty(psiClassList)) {
            return;
        }
        if (!new BatchJavaDocCheckView().showAndGet()) {
            return;
        }
        Map<PsiElement, String> psiElementMap = new HashMap<>(16);
        for (PsiClass psiClass : psiClassList.stream().filter(PsiElement::isPhysical).toList()) {
            List<PsiClass> tempPsiClassList = new ArrayList<>();
            if (javaDocConfig.getInnerClassBatchEnable()) {
                tempPsiClassList.addAll(Arrays.stream(psiClass.getInnerClasses()).filter(PsiElement::isPhysical).toList());
            }
            if (javaDocConfig.getClassBatchEnable()) {
                tempPsiClassList.add(psiClass);
            }
            for (PsiClass tempPsiClass : tempPsiClassList) {
                if (javaDocConfig.getClassBatchEnable() || javaDocConfig.getInnerClassBatchEnable()) {
                    psiElementMap.put(tempPsiClass, tempPsiClass.getQualifiedName());
                }
                if (javaDocConfig.getMethodBatchEnable()) {
                    psiElementMap.putAll(Arrays.stream(tempPsiClass.getMethods()).filter(PsiElement::isPhysical)
                            .collect(Collectors.toMap(psiMethod -> psiMethod, PsiMethod::getName, (v1, v2) -> v1)));
                }
                if (javaDocConfig.getFieldBatchEnable()) {
                    psiElementMap.putAll(Arrays.stream(tempPsiClass.getFields()).filter(PsiElement::isPhysical)
                            .filter(psiField -> Arrays.stream(psiField.getAnnotations()).noneMatch(annotation -> StringUtils.equalsAny(annotation.getQualifiedName(),
                                    ExtraPackageNameEnum.RESOURCE.getName(), ExtraPackageNameEnum.AUTOWIRED.getName())))
                            .filter(psiField -> !StringUtils.equals(psiField.getName(), CommonClassNames.SERIAL_VERSION_UID_FIELD_NAME))
                            .collect(Collectors.toMap(psiField -> psiField, PsiField::getName, (v1, v2) -> v1)));
                }
            }
        }
        if (MapUtils.isEmpty(psiElementMap)) {
            return;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(project, String.format("%s Batch Generate JavaDoc", Constants.PLUGIN_NAME), true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                List<Map.Entry<PsiElement, String>> psiElementList = psiElementMap.entrySet().stream().toList();
                int total = psiElementList.size();
                StopWatch watch = new StopWatch();
                watch.start();
                for (int i = 0; i < total; i++) {
                    Map.Entry<PsiElement, String> entry = psiElementList.get(i);
                    PsiElement psiElement = entry.getKey();
                    String comment = javaDocGenerateService.generate(psiElement);
                    if (StringUtils.isNotBlank(comment)) {
                        javaDocWriterService.writeJavadoc(project, psiElement, comment, Constants.NUM.ZERO);
                    }
                    if (indicator.isCanceled()) {
                        break;
                    }
                    double fraction = (double) (i + 1) / total;
                    indicator.setFraction(fraction);
                    indicator.setText(String.format("%s completed success %s", entry.getValue(), NumberUtil.formatPercent(fraction, 2)));
                }
                watch.stop();
                indicator.setText(String.format("%s completed consume %sms", Constants.PLUGIN_NAME, watch.getTotalTimeMillis()));
            }
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile[] virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        e.getPresentation().setVisible(Objects.nonNull(virtualFiles) && virtualFiles.length != 0);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
