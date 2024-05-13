package easy.handler;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import easy.base.Constants;
import easy.ui.CommonNotifyDialog;
import easy.util.EasyCommonUtil;
import easy.util.HttpUtil;
import easy.util.JsonUtil;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
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
        JsonObject pluginObject = JsonUtil.fromArray(response).get(0).getAsJsonObject();
        String remoteVersion = pluginObject.get("version").getAsString();
        String notes = pluginObject.get("notes").getAsString();
        // 比对版本信息
        if (StringUtils.equals(version, remoteVersion)) {
            NotifyUtil.notify(String.format("🎉🎉🎉 %s 已是最新版：v%s 🎉🎉🎉", Constants.PLUGIN_NAME, remoteVersion), new NotificationAction("👍 五星好评") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                    EasyCommonUtil.confirmOpenLink(Constants.JETBRAINS_URL);
                }
            }, genNoteAction(notes, remoteVersion));
            return;
        }
        // 发送插件最新版本通知
        NotifyUtil.notify(String.format("🎉🎉🎉 %s 已发布最新版：v%s 🎉🎉🎉", Constants.PLUGIN_NAME, remoteVersion), new NotificationAction("😎 立即更新") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, "Plugins");
            }
        }, genNoteAction(notes, remoteVersion));

    }

    /**
     * 解析插件注释
     *
     * @param notes   注释
     * @param version 版本
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/04/25 16:34
     */
    private static String parsePluginNotes(String notes, String version) {
        if (StringUtils.isBlank(notes)) {
            return StringUtils.EMPTY;
        }
        List<String> noteList = Lists.newLinkedList();
        noteList.add("<ul>");
        noteList.addAll(Arrays.stream(notes.split("<h3>")[1]
                        .split("<li>-------------------------------</li>")[1]
                        .split(StringUtils.LF))
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::trim).toList());
        return String.format("<h3>v%s</h3>%s", version, String.join(StringUtils.LF, noteList));
    }

    /**
     * 显示新特性弹窗操作
     *
     * @param notes         注释
     * @param remoteVersion 远程版本
     * @return {@link com.intellij.openapi.actionSystem.AnAction}
     * @author mabin
     * @date 2024/04/25 17:41
     */
    private static AnAction genNoteAction(String notes, String remoteVersion) {
        return new NotificationAction("🎊 升级特性") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                new CommonNotifyDialog(Constants.PLUGIN_NAME, parsePluginNotes(notes, remoteVersion)).show();
            }
        };
    }

}
