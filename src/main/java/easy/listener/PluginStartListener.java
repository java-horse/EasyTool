package easy.listener;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import easy.base.Constants;
import easy.form.SupportView;
import easy.util.NotificationUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;

/**
 * 插件运行（项目打开）监听器
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/01 14:26
 **/

public class PluginStartListener implements ProjectManagerListener {

    private static final Logger log = Logger.getInstance(PluginStartListener.class);

    // 上次通知时间
    private volatile long lastNoticeTime = 0L;

    // 通知时间间隔 (30分钟之内打开项目只弹窗一次提示)
    private static final long INTERVAL = 30 * 60 * 1000L;

    /**
     * project open listener
     *
     * @param project
     * @return void
     * @author mabin
     * @date 2023/9/1 14:27
     **/
    @Override
    public void projectOpened(@NotNull Project project) {
        activate();
    }

    /**
     * 激活消息
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/1 14:47
     **/
    private void activate() {
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

}
