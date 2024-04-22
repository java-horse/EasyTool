package easy.handler;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.util.EasyCommonUtil;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import easy.util.NotificationUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 检查插件更新处理
 *
 * @author mabin
 * @project EasyTool
 * @package easy.handler
 * @date 2024/03/12 17:30
 */
public class PluginForUpdateHandler {

    /**
     * 检查插件是否最新版本及跳出更新弹窗
     *
     * @param project 项目
     * @author mabin
     * @date 2024/03/12 17:46
     */
    public static void forUpdate(@NotNull Project project) {
        // 获取当前插件版本
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("easy.char"));
        if (Objects.isNull(plugin)) {
            return;
        }
        String version = plugin.getVersion();
        if (StringUtils.isBlank(version)) {
            return;
        }
        // 远程获取插件最新版本
        String response = HttpUtil.doGet("https://plugins.jetbrains.com/api/plugins/21589/updates?size=1");
        if (StringUtils.isBlank(response)) {
            return;
        }
        String remoteVersion = JsonUtil.fromArray(response).get(0).getAsJsonObject().get("version").getAsString();
        // 比对版本信息
        if (StringUtils.equals(version, remoteVersion)) {
            NotificationUtil.notify(String.format("🎉🎉🎉 %s 已是最新版：v%s 🎉🎉🎉", Constants.PLUGIN_NAME, remoteVersion), new NotificationAction("👍 五星好评") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                    EasyCommonUtil.confirmOpenLink(Constants.JETBRAINS_URL);
                }
            });
            return;
        }
        // 发送插件最新版本通知
        NotificationUtil.notify(String.format("🎉🎉🎉 %s 已发布最新版：v%s 🎉🎉🎉", Constants.PLUGIN_NAME, remoteVersion), new NotificationAction("😎 立即更新") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, "Plugins");
            }
        });

    }

}
