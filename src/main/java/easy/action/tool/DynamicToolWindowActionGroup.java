package easy.action.tool;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import easy.action.mybatis.log.MyBatisLogAction;
import easy.enums.ToolWindowEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 动态菜单侧边栏生成
 *
 * @project: EasyChar
 * @package: easy.action.tool
 * @author: mabin
 * @date: 2023/10/14 11:50:59
 */
public class DynamicToolWindowActionGroup extends DefaultActionGroup {

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new ToolWindowAction(ToolWindowEnum.SEARCH_API),
                new ToolWindowAction(ToolWindowEnum.WECHAT_OFFICIAL),
                new ToolWindowAction(ToolWindowEnum.PLUGIN_SETTING),
                new MyBatisLogAction()
        };
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
