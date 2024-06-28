package easy.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import easy.base.Constants;
import easy.form.SupportView;
import easy.handler.PluginForUpdateHandler;
import easy.util.EasyCommonUtil;
import easy.util.NotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * IDEA启动监听
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 14:12
 **/

public class AppActiveListener implements ApplicationActivationListener {

    // 通知时间间隔 (30天之内打开项目只弹窗一次提示)
    private static final long SUPPORT_INTERVAL = 30 * 24 * 60 * 60 * 1000L;
    /**
     * 自动更新间隔(3天检测一次)
     */
    private static final long AUTO_UPDATE_INTERVAL = 3 * 24 * 60 * 60 * 1000L;

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
        this.autoUpdate();
    }

    /**
     * 赞赏支持
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/4 21:21
     **/
    private void support() {
        long lastNoticeTime = PropertiesComponent.getInstance().getLong(Constants.Persistence.COMMON.LAST_NOTIFY_TIME,
                DateUtil.offsetDay(new Date(), -31).getTime());
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastNoticeTime < SUPPORT_INTERVAL) {
            return;
        }
        AnAction starAction = new NotificationAction("\uD83C\uDF1F 点个star") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                EasyCommonUtil.confirmOpenLink(Constants.GITEE_URL);
            }
        };
        AnAction reviewsAction = new NotificationAction("\uD83D\uDC4D 五星好评") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                EasyCommonUtil.confirmOpenLink(Constants.JETBRAINS_URL);
            }
        };
        AnAction payAction = new NotificationAction("☕ 喝个咖啡") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                new SupportView().show();
            }
        };
        NotifyUtil.notify("如果觉得" + Constants.PLUGIN_NAME + "有趣, 欢迎支持哦!", starAction, reviewsAction, payAction);
        PropertiesComponent.getInstance().setValue(Constants.Persistence.COMMON.LAST_NOTIFY_TIME, Long.toString(currentTimeMillis));
    }

    /**
     * 自动更新
     *
     * @author mabin
     * @date 2024/06/27 15:59
     */
    private void autoUpdate() {
        long lastNoticeTime = PropertiesComponent.getInstance().getLong(Constants.Persistence.COMMON.AUTO_UPDATE_LAST_NOTIFY_TIME,
                DateUtil.offsetDay(new Date(), -4).getTime());
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastNoticeTime < AUTO_UPDATE_INTERVAL) {
            return;
        }
        // 睡眠5s后再进行插件自动更新处理
        ThreadUtil.sleep(5 * Constants.NUM.ONE_THOUSAND);
        PluginForUpdateHandler.listenerAutoUpdate();
        PropertiesComponent.getInstance().setValue(Constants.Persistence.COMMON.AUTO_UPDATE_LAST_NOTIFY_TIME, Long.toString(currentTimeMillis));
    }

    @Override
    public void applicationDeactivated(@NotNull IdeFrame ideFrame) {
        this.applicationActivated(ideFrame);
    }

}
