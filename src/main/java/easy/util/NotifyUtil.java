package easy.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import easy.base.Constants;
import easy.icons.EasyIcons;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * 通知工具类
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/01 14:28
 **/
public class NotifyUtil {

    private NotifyUtil() {
    }


    /**
     * 全局消息通知
     *
     * @param content
     * @param actions
     * @return void
     * @author mabin
     * @date 2023/9/17 16:26
     */
    public static void notify(String content, AnAction... actions) {
        notify(Constants.PLUGIN_NAME, content, NotificationType.INFORMATION, actions);
    }

    /**
     * 全局消息通知
     *
     * @param content
     * @param type
     * @param actions
     * @return void
     * @author mabin
     * @date 2023/11/27 15:47
     */
    public static void notify(String content, NotificationType type, AnAction... actions) {
        notify(Constants.PLUGIN_NAME, content, type, actions);
    }

    /**
     * 全局消息通知
     *
     * @param title
     * @param content
     * @param actions
     * @return void
     * @author mabin
     * @date 2024/1/5 10:55
     */
    public static void notify(String title, String content, AnAction... actions) {
        notify(title, content, NotificationType.INFORMATION, actions);
    }

    /**
     * 全局消息通知
     *
     * @param title
     * @param content
     * @param type
     * @param actions
     * @return void
     * @author mabin
     * @date 2023/12/16 16:05
     */
    public static void notify(String title, String content, NotificationType type, AnAction... actions) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("easy.tool.notify.group")
                .createNotification(title, content, type)
                .setDisplayId(Constants.PLUGIN_NAME)
                .setIcon(EasyIcons.ICON.LOGO)
                .setImportant(true);
        if (ArrayUtils.isNotEmpty(actions)) {
            Arrays.stream(actions).forEach(notification::addAction);
        }
        notification.notify(null);
    }

}
