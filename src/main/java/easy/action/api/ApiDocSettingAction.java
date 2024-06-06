package easy.action.api;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.enums.ApiDocTypeEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * ApiDoc设置页面Action
 *
 * @author mabin
 * @project EasyTool
 * @package easy.action.api
 * @date 2024/05/11 11:37
 */
public class ApiDocSettingAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        ShowSettingsUtil.getInstance().showSettingsDialog(project, Constants.PLUGIN_NAME);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        presentation.setText(ApiDocTypeEnum.SETTING.getTitle());
        e.getPresentation().setIcon(ApiDocTypeEnum.SETTING.getIcon());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}
