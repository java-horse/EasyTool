package easy.init;

import com.intellij.AppTopics;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.handler.ConvertCharHandler;
import easy.listener.AppActiveListener;
import easy.listener.EventBusListener;
import easy.listener.FileDocumentSaveListener;
import easy.listener.TabHighlighterFileEditorListener;
import easy.translate.TranslateService;
import easy.util.EventBusUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * EasyTool插件启动初始化
 *
 * @author mabin
 * @project EasyTool
 * @package easy.init
 * @date 2024/03/09 10:20
 */
public class EasyToolApplicationInit implements StartupActivity, DumbAware {

    @Override
    public void runActivity(@NotNull Project project) {
        Application application = ApplicationManager.getApplication();
        if (application.isUnitTestMode() || project.isDisposed()) {
            return;
        }
        notifyInit(application);
        translateServiceInit(application);
        convertInit(application);

        MessageBus messageBus = application.getMessageBus();
        MessageBusConnection messageBusConnection = messageBus.connect();
        activeTabHighlightInit(project, application, messageBusConnection);
        fileDocumentSaveInit(project, messageBusConnection);
        application.invokeLater(this::eventBusRegisterInit);
    }

    /**
     * 消息监听器初始化
     *
     * @param application
     * @return void
     * @author mabin
     * @date 2024/1/2 9:37
     */
    private void notifyInit(@NotNull Application application) {
        AppActiveListener appActiveListener = new AppActiveListener();
        Disposable disposable = Disposer.newDisposable();
        Disposer.register(application, disposable);
        MessageBusConnection connect = application.getMessageBus().connect(disposable);
        connect.subscribe(ApplicationActivationListener.TOPIC, appActiveListener);
        appActiveListener.activate();
    }

    /**
     * 翻译引擎服务初始化
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/4 21:21
     **/
    private void translateServiceInit(@NotNull Application application) {
        TranslateConfig translateConfig = application.getService(TranslateConfigComponent.class).getState();
        TranslateService translateService = application.getService(TranslateService.class);
        if (Objects.isNull(translateService) || Objects.isNull(translateConfig)) {
            return;
        }
        translateService.init(translateConfig);
    }

    /**
     * 中英字符映射功能初始化
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/12/20 14:07
     */
    private void convertInit(@NotNull Application application) {
        application.invokeLater(() -> {
            TypedAction typedAction = TypedAction.getInstance();
            typedAction.setupRawHandler(new ConvertCharHandler(typedAction.getRawHandler()));
        });
    }

    /**
     * tab标签高亮功能初始化
     *
     * @param project
     * @param application
     * @param messageBusConnection
     * @return void
     * @author mabin
     * @date 2023/12/20 14:10
     */
    private void activeTabHighlightInit(@NotNull Project project, @NotNull Application application, @NotNull MessageBusConnection messageBusConnection) {
        TabHighlighterFileEditorListener tabHighlighterFileEditorListener = new TabHighlighterFileEditorListener(project);
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, tabHighlighterFileEditorListener);
        messageBusConnection.subscribe(TabHighlighterFileEditorListener.CHANGE_HIGHLIGHTER_TOPIC, tabHighlighterFileEditorListener);
    }

    /**
     * 文件文档保存监听器初始化
     *
     * @param project              项目
     * @param messageBusConnection
     * @author mabin
     * @date 2024/07/20 15:42
     */
    private void fileDocumentSaveInit(@NotNull Project project, @NotNull MessageBusConnection messageBusConnection) {
        messageBusConnection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new FileDocumentSaveListener(project));
    }

    /**
     * EventBus事件监听器注册
     *
     * @author mabin
     * @date 2024/07/25 10:53
     */
    private void eventBusRegisterInit() {
        EventBusUtil.register(new EventBusListener());
    }

}
