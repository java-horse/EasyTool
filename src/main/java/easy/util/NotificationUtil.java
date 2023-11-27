package easy.util;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import easy.base.Constants;
import easy.icons.EasyIcons;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 通知工具类
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/01 14:28
 **/
public class NotificationUtil {

    private NotificationUtil() {
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
     * @param type
     * @param actions
     * @return void
     * @author mabin
     * @date 2023/9/17 16:26
     */
    public static void notify(String title, String content, NotificationType type, AnAction... actions) {
        NotificationGroup group = new NotificationGroup(Constants.PLUGIN_NAME, NotificationDisplayType.BALLOON, true, null, EasyIcons.ICON.LOGO);
        Notification notification = group.createNotification(title, content, type, NotificationListener.URL_OPENING_LISTENER);
        if (ArrayUtils.isNotEmpty(actions)) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }
        notification.notify(null);
    }


}
