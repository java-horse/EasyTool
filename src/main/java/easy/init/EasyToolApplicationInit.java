package easy.init;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import easy.action.ConvertAction;
import easy.listener.TabHighlighterFileEditorListener;
import org.jetbrains.annotations.NotNull;

/**
 * Tab标签高亮初始化
 *
 * @project: EasyTool
 * @package: easy.init
 * @author: mabin
 * @date: 2023/12/20 11:24:00
 */
public class EasyToolApplicationInit implements StartupActivity, DumbAware {

    @Override
    public void runActivity(@NotNull Project project) {
        Application application = ApplicationManager.getApplication();
        convertInit(application);
        activeTabHighlightInit(project, application);
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
        application.invokeLater(ConvertAction::new);
    }

    /**
     * tab标签高亮功能初始化
     *
     * @param project
     * @param application
     * @return void
     * @author mabin
     * @date 2023/12/20 14:10
     */
    private void activeTabHighlightInit(@NotNull Project project, @NotNull Application application) {
        MessageBus messageBus = application.getMessageBus();
        MessageBusConnection messageBusConnection = messageBus.connect();
        TabHighlighterFileEditorListener tabHighlighterFileEditorListener = new TabHighlighterFileEditorListener(project);
        messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, tabHighlighterFileEditorListener);
        messageBusConnection.subscribe(TabHighlighterFileEditorListener.CHANGE_HIGHLIGHTER_TOPIC, tabHighlighterFileEditorListener);
    }

}
