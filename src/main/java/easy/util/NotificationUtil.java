package easy.util;

import com.intellij.icons.AllIcons;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
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
     * @param title
     * @param content
     * @param actions
     */
    public static void notify(String title, String content, AnAction... actions) {
        NotificationGroup group = new NotificationGroup("EasyChar", NotificationDisplayType.BALLOON, true, null, AllIcons.General.AddJdk);
        Notification notification = group.createNotification(title, content, NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER);
        if (ArrayUtils.isNotEmpty(actions)) {
            for (AnAction action : actions) {
                notification.addAction(action);
            }
        }
        notification.notify(null);
    }


}
