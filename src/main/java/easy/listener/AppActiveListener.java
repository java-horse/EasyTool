package easy.listener;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.IdeFrame;
import easy.base.Constants;
import easy.form.SupportView;
import easy.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;

/**
 * IDEA启动监听
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 14:12
 **/

public class AppActiveListener implements ApplicationActivationListener {

    private static final Logger log = Logger.getInstance(AppActiveListener.class);

    // 上次通知时间
    private volatile long lastNoticeTime = 0L;

    // 通知时间间隔 (7天之内打开项目只弹窗一次提示)
    private static final long INTERVAL = 7 * 24 * 60 * 60 * 1000L;

    @Override
    public synchronized void applicationActivated(@NotNull IdeFrame ideFrame) {
        this.activate();
    }


    /**
     * 激活消息
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/1 14:47
     **/
    public synchronized void activate() {
        this.support();
    }

    private void support() {
        if (System.currentTimeMillis() - lastNoticeTime < INTERVAL) {
            return;
        }
        AnAction starAction = new NotificationAction("\uD83C\uDF1F 点个star") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create(Constants.GITEE_URL));
                    }
                } catch (Exception ex) {
                    log.error("打开链接失败: " + Constants.GITEE_URL, ex);
                }
            }
        };
        AnAction reviewsAction = new NotificationAction("\uD83D\uDC4D 五星好评") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create(Constants.JETBRAINS_URL));
                    }
                } catch (Exception ex) {
                    log.error("打开链接失败: " + Constants.JETBRAINS_URL, ex);
                }
            }
        };
        AnAction payAction = new NotificationAction("☕ 喝个咖啡") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                SupportView supportView = new SupportView();
                supportView.show();
            }

        };
        NotificationUtil.notify("EasyChar", "如果EasyChar甚得您心, 请支持一下开发者!", starAction, reviewsAction, payAction);
        lastNoticeTime = System.currentTimeMillis();
    }

    @Override
    public void applicationDeactivated(@NotNull IdeFrame ideFrame) {
        this.applicationActivated(ideFrame);
    }

}
