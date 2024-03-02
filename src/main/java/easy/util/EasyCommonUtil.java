package easy.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import easy.base.Constants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;
import java.util.Objects;

/**
 * 项目通用处理
 *
 * @project: EasyTool
 * @package: easy.util
 * @author: mabin
 * @date: 2023/11/28 10:50:42
 */
public class EasyCommonUtil {

    private static final Logger log = Logger.getInstance(EasyCommonUtil.class);

    private EasyCommonUtil() {

    }

    /**
     * 浏览器打开
     *
     * @param url
     * @return void
     * @author mabin
     * @date 2023/11/28 10:54
     */
    public static void openLink(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (Objects.nonNull(desktop) && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(url));
            } catch (Exception e) {
                log.error("open link exception: " + url, e);
            }
        }
    }

    /**
     * 二次确认浏览器打开
     *
     * @param url
     * @return void
     * @author mabin
     * @date 2023/11/28 11:08
     */
    public static void confirmOpenLink(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        int result = Messages.showYesNoDialog("即将离开【" + applicationInfo.getFullApplicationName() + "】是否继续访问\n" + url + "\n",
                Constants.PLUGIN_NAME, "继续访问", "取消", Messages.getWarningIcon());
        if (result == MessageConstants.YES) {
            openLink(url);
        }
    }

    /**
     * 打开插件设置弹窗
     *
     * @return
     */
    public static AnAction getPluginSettingAction() {
        return getPluginSettingAction(null);
    }

    /**
     * 打开插件设置弹窗
     *
     * @param project
     * @return
     */
    public static AnAction getPluginSettingAction(Project project) {
        return new NotificationAction("⚙️ " + Constants.PLUGIN_NAME) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, Constants.PLUGIN_NAME);
            }
        };
    }

}
