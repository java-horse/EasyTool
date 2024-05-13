package easy.action.base;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import easy.util.BundleUtil;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.filechooser.FileSystemView;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

/**
 * 下载JSON文件的Action
 *
 * @project: EasyTool
 * @package: easy.action.base
 * @author: mabin
 * @date: 2024/01/26 15:20:14
 */
public class DownloadJsonConsoleAction extends AnAction {

    private static final Logger log = Logger.getInstance(DownloadJsonConsoleAction.class);

    private final ConsoleViewImpl consoleView;

    public DownloadJsonConsoleAction(ConsoleViewImpl consoleView) {
        super(BundleUtil.getI18n("global.button.download.text"), BundleUtil.getI18n("global.button.download.text"), AllIcons.Actions.Download);
        this.consoleView = consoleView;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        String text = consoleView.getText();
        if (StringUtils.isBlank(text)) {
            return;
        }
        FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, false, false, false, false);
        VirtualFile virtualDirectory = FileChooser.chooseFile(fileChooserDescriptor, project, getDefaultDownloadDir());
        if (Objects.isNull(virtualDirectory) || !virtualDirectory.isDirectory()) {
            return;
        }
        String fileName = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + "-" + "download.json";
        String prefixPath = virtualDirectory.toNioPath().toString();
        Path filePath = Paths.get(prefixPath, fileName);
        try {
            Files.write(filePath, text.getBytes());
            NotifyUtil.notify("file: " + prefixPath + FileUtil.FILE_SEPARATOR + fileName + " download success!");
        } catch (Exception ex) {
            log.error("download file error!", ex);
            NotifyUtil.notify("file download error: " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    /**
     * 获取默认下载目录路径
     *
     * @param
     * @return com.intellij.openapi.vfs.VirtualFile
     * @author mabin
     * @date 2024/1/26 15:38
     */
    private VirtualFile getDefaultDownloadDir() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        String deskPath = fileSystemView.getHomeDirectory().getPath();
        VirtualFile defaultDownloadDir;
        try {
            defaultDownloadDir = LocalFileSystem.getInstance().findFileByPath(deskPath);
        } catch (Exception ex) {
            defaultDownloadDir = null;
        }
        return defaultDownloadDir;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enabled = consoleView.getContentSize() > 0;
        if (!enabled) {
            enabled = e.getData(LangDataKeys.CONSOLE_VIEW) != null;
            Editor editor = e.getData(CommonDataKeys.EDITOR);
            if (editor != null && editor.getDocument().getTextLength() == 0) {
                enabled = false;
            }
        }
        e.getPresentation().setEnabled(enabled);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}
