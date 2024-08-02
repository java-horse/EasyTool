package easy.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.thread.ThreadUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.helper.ServiceHelper;
import easy.ui.CommonNotifyDialog;
import easy.util.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
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
    private static final Logger log = Logger.getInstance(PluginForUpdateHandler.class);
    private static CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

    /**
     * 获取插件远程版本
     *
     * @author mabin
     * @date 2024/06/27 16:03
     */
    public static JsonObject getPluginRemoteVersion() {
        String response = HttpUtil.doGet("https://plugins.jetbrains.com/api/plugins/21589/updates?size=1");
        if (StringUtils.isBlank(response)) {
            return new JsonObject();
        }
        JsonObject pluginObject = JsonUtil.fromArray(response).get(0).getAsJsonObject();
        return Objects.isNull(pluginObject) ? new JsonObject() : pluginObject;
    }

    /**
     * 检查插件是否最新版本及跳出更新弹窗
     *
     * @param project 项目
     * @author mabin
     * @date 2024/03/12 17:46
     */
    public static void forUpdate(@NotNull Project project) {
        // 获取当前插件版本
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId(Constants.PLUGIN_ID));
        if (Objects.isNull(plugin)) {
            return;
        }
        String version = plugin.getVersion();
        if (StringUtils.isBlank(version)) {
            return;
        }
        // 远程获取插件最新版本
        JsonObject pluginObject = getPluginRemoteVersion();
        String remoteVersion = pluginObject.get("version").getAsString();
        String notes = pluginObject.get("notes").getAsString();
        // 比对版本信息
        if (StringUtils.equals(version, remoteVersion)) {
            NotifyUtil.notify(String.format("🎉🎉🎉 %s 已是最新版：v%s 🎉🎉🎉", Constants.PLUGIN_NAME, remoteVersion),
                    new NotificationAction("👍 五星好评") {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                            EasyCommonUtil.confirmOpenLink(Constants.JETBRAINS_URL);
                        }
                    }, genNoteAction(notes, remoteVersion));
            return;
        }
        // 发送插件最新版本通知
        NotifyUtil.notify(String.format("🎉🎉🎉 %s 已发布最新版：v%s（当前版本：v%s）🎉🎉🎉", Constants.PLUGIN_NAME, remoteVersion, version),
                new NotificationAction("😎 插件面板") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent anActionEvent, @NotNull Notification notification) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Plugins");
                    }
                }, genNoteAction(notes, remoteVersion), genAutoUpdateAction(remoteVersion));
    }

    /**
     * 监听器自动更新
     *
     * @author mabin
     * @date 2024/06/27 16:13
     */
    public static void listenerAutoUpdate() {
        if (Objects.isNull(commonConfig) || Boolean.FALSE.equals(commonConfig.getPluginAutoUpdateEnable())) {
            return;
        }
        JsonObject pluginObject = getPluginRemoteVersion();
        String remoteVersion = pluginObject.get("version").getAsString();
        autoUpdate(remoteVersion);
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
                new CommonNotifyDialog(Constants.PLUGIN_NAME, parsePluginNotes(notes, remoteVersion), Boolean.FALSE).show();
            }
        };
    }

    /**
     * 显示自动更新操作
     *
     * @param remoteVersion 远程版本
     * @return {@link com.intellij.openapi.actionSystem.AnAction}
     * @author mabin
     * @date 2024/06/26 09:42
     */
    private static AnAction genAutoUpdateAction(String remoteVersion) {
        return new NotificationAction("🎉 自动更新") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                int confirm = Messages.showOkCancelDialog(String.format("确认更新插件到【v%s】最新版本? (实验性功能: 存在不稳定性, 孤勇者可背水一试(建议在IDE插件面板更新))", remoteVersion),
                        Constants.PLUGIN_NAME, BundleUtil.getI18n("global.button.confirm.text"), BundleUtil.getI18n("global.button.plugin.panel.text"),
                        Messages.getQuestionIcon());
                if (confirm == MessageConstants.OK) {
                    autoUpdate(remoteVersion);
                    return;
                }
                if (confirm == MessageConstants.CANCEL) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(ProjectManagerEx.getInstance().getDefaultProject(), "Plugins");
                }
            }
        };
    }

    /**
     * 自动更新
     *
     * @param remoteVersion 远程版本
     * @author mabin
     * @date 2024/06/26 09:47
     */
    private static void autoUpdate(String remoteVersion) {
        // 获取插件的安装目录
        String pluginPath = PathManager.getPluginsPath();
        String pluginTempPath = PathManager.getPluginTempPath();
        // 远程下载插件资源(zip文件, 进度条显示下载进度)
        ProgressManager.getInstance().run(new Task.Backgroundable(ProjectManagerEx.getInstance().getDefaultProject(),
                String.format("%s Plugin Download", Constants.PLUGIN_NAME), true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(false);
                cn.hutool.http.HttpUtil.downloadFile(String.format("https://plugins.jetbrains.com/plugin/download?pluginId=%s&version=%s",
                        Constants.PLUGIN_ID, remoteVersion), FileUtil.file(pluginTempPath), new StreamProgress() {
                    @Override
                    public void start() {
                        indicator.setText(String.format("%s start download...", Constants.PLUGIN_NAME));
                    }

                    @Override
                    public void progress(long total, long progressSize) {
                        indicator.setText(String.format("%s download progress %s / %s", Constants.PLUGIN_NAME,
                                FileUtil.readableFileSize(progressSize),
                                FileUtil.readableFileSize(total)));
                    }

                    @Override
                    public void finish() {
                        indicator.setText(String.format("%s completed download...", Constants.PLUGIN_NAME));
                        ThreadUtil.sleep(Constants.NUM.ONE_THOUSAND);
                        // 删除旧插件
                        if (Boolean.FALSE.equals(FileUtil.del(pluginPath + "/" + Constants.PLUGIN_NAME))) {
                            return;
                        }
                        ThreadUtil.sleep(Constants.NUM.ONE_THOUSAND);
                        // 安装新插件
                        unzipPluginFile(remoteVersion, pluginTempPath, pluginPath);
                    }
                });
            }
        });
    }

    /**
     * 解压缩插件文件
     *
     * @param remoteVersion  远程版本
     * @param pluginTempPath 插件临时路径
     * @param pluginPath     插件路径
     * @author mabin
     * @date 2024/06/26 14:22
     */
    private static void unzipPluginFile(String remoteVersion, String pluginTempPath, String pluginPath) {
        // 解压新版本的插件到插件目录
        String zipFilePath = pluginTempPath + String.format("/%s-%s.zip", Constants.PLUGIN_NAME, remoteVersion);
        File destDir = FileUtil.mkdir(pluginPath);
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                File targetFile = new File(destDir, entry.getName());
                FileUtil.mkdir(targetFile.getParent());
                if (entry.isDirectory()) {
                    continue;
                }
                try (InputStream is = zipFile.getInputStream(entry);
                     FileOutputStream fos = new FileOutputStream(targetFile)) {
                    IoUtil.copy(is, fos);
                } catch (Exception e) {
                    log.error("file decompression write exception", e);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("file decompression exception", e);
            return;
        }
        // 显示文件自动更新成功
        NotifyUtil.notify(String.format("插件【%s】更新完成, 重启IDE生效", Constants.PLUGIN_NAME), new NotificationAction("♨️ 立即重启") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                int confirmRestart = Messages.showOkCancelDialog("Restart IDE to apply changes in plugins?", "IDE and Plugin Updates",
                        BundleUtil.getI18n("global.button.restart.text"), BundleUtil.getI18n("global.button.later.text"), Messages.getQuestionIcon());
                if (confirmRestart == MessageConstants.YES) {
                    restartIde();
                }
            }
        }, new NotificationAction("⚙️ 修改自动更新") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                EasyCommonUtil.getPluginSettingAction(ProjectManagerEx.getInstance().getDefaultProject());
            }
        });
    }

    /**
     * 重新启动ide
     *
     * @author mabin
     * @date 2024/06/26 10:11
     */
    private static void restartIde() {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(() -> {
            // 检查当前是否在写操作中
            if (!application.isWriteAccessAllowed()) {
                application.restart();
            } else {
                // 如果在写操作中，计划在写操作结束后重启
                application.runWriteAction(() -> {
                    // 保存所有文件后重启
                    PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(ProjectManagerEx.getInstance().getDefaultProject());
                    Document[] documents = psiDocumentManager.getUncommittedDocuments();
                    for (Document document : documents) {
                        PsiFile psiFile = psiDocumentManager.getPsiFile(document);
                        if (Objects.nonNull(psiFile) && psiFile.isValid()) {
                            psiDocumentManager.commitDocument(document);
                        }
                    }
                    application.restart();
                });
            }
        }, ModalityState.NON_MODAL);
    }

}
