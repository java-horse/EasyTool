package easy.action.tool;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.enums.ToolWindowEnum;
import easy.form.WeChatOfficialView;
import easy.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 菜单工具栏Action
 *
 * @project: EasyChar
 * @package: easy.action.tool
 * @author: mabin
 * @date: 2023/10/14 11:45:40
 */
public class ToolWindowAction extends AnAction {

    public ToolWindowAction(ToolWindowEnum toolWindowEnum) {
        super(toolWindowEnum.title, toolWindowEnum.desc, toolWindowEnum.icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        Presentation presentation = e.getPresentation();
        String actionText = presentation.getText();
        if (StringUtils.equals(ToolWindowEnum.WECHAT_OFFICIAL.title, actionText)) {
            new WeChatOfficialView().show();
            MessageUtil.sendActionDingMessage(e);
        } else if (StringUtils.equals(ToolWindowEnum.PLUGIN_SETTING.title, actionText)) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, Constants.PLUGIN_NAME);
        } else if (StringUtils.equals(ToolWindowEnum.SEARCH_API.title, actionText)) {
            AnAction action = ActionManager.getInstance().getAction("EasyToolRestfulSearchAction");
            if (Objects.isNull(action)) {
                return;
            }
            action.actionPerformed(e);
        }
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
