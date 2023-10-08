package easy.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import easy.form.WeChatOfficialView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 微信公众号弹窗Action
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 15:39
 **/

public class WeChatOfficialAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        WeChatOfficialView weChatOfficialView = new WeChatOfficialView();
        weChatOfficialView.show();
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
