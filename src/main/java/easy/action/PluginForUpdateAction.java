package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import easy.enums.ToolWindowEnum;
import easy.handler.PluginForUpdateHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 用于更新操作的插件
 *
 * @project EasyTool
 * @package easy.action
 * @author mabin
 * @date 2024/03/12 17:59
 */
public class PluginForUpdateAction extends AnAction {


    public PluginForUpdateAction() {
        super("ForUpdate", ToolWindowEnum.FOR_UPDATE.desc, ToolWindowEnum.FOR_UPDATE.icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        PluginForUpdateHandler.forUpdate(project);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(e.getProject()));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}
