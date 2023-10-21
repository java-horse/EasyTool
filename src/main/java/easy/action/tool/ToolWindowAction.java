package easy.action.tool;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import easy.base.Constants;
import easy.enums.ToolWindowEnum;
import easy.form.WeChatOfficialView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private static final Logger log = Logger.getInstance(ToolWindowAction.class);

    public ToolWindowAction(@Nullable String text, @Nullable String description) {
        super(text, description, null);
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
        } else if (StringUtils.equals(ToolWindowEnum.PLUGIN_STATISTICS.title, actionText)) {
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(e.getProject());
            ToolWindow toolWindow = toolWindowManager.getToolWindow(Constants.PLUGIN_NAME);
            if (Objects.isNull(toolWindow)) {
                return;
            }
            toolWindow.show();
        } else if (StringUtils.equals(ToolWindowEnum.PLUGIN_SETTING.title, actionText)) {
            ActionManager actionManager = ActionManager.getInstance();
            AnAction action = actionManager.getAction("ShowSettings");
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