package easy.action;

import cn.hutool.crypto.SecureUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.config.screenshot.CodeScreenshotConfig;
import easy.config.screenshot.CodeScreenshotConfigComponent;
import easy.handler.CodeScreenshotHandler;
import easy.helper.ServiceHelper;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CodeScreenshotAction extends AnAction {

    /**
     * 截图Cache: 如果5秒内重复截取相同内容, 则不允许此操作
     */
    private static final Cache<String, String> SCREEN_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .maximumSize(2).build();
    private CodeScreenshotConfig config = ServiceHelper.getService(CodeScreenshotConfigComponent.class).getState();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(project) || Objects.isNull(editor)) {
            NotifyUtil.notify(String.format("%s action is only available in an editor", e.getPresentation().getText()), NotificationType.ERROR);
            return;
        }
        if (!editor.getSelectionModel().hasSelection()) {
            NotifyUtil.notify(String.format("%s action first select code area", e.getPresentation().getText()), NotificationType.ERROR);
            return;
        }
        // 是否短时间内重复截取相同内容
        if (repeatScreen(editor.getSelectionModel().getSelectedText())) {
            NotifyUtil.notify(String.format("%s action repeated screen interception within 5s", e.getPresentation().getText()), NotificationType.WARNING);
            return;
        }
        // 创建代码截图
        BufferedImage image = CodeScreenshotHandler.createImage(editor, config);
        if (Objects.isNull(image)) {
            NotifyUtil.notify(String.format("%s action generate code image fail", e.getPresentation().getText()), NotificationType.ERROR);
            return;
        }
        // 添加水印
        BufferedImage waterMarkImage = Boolean.TRUE.equals(config.getAutoAddWaterMark()) ? CodeScreenshotHandler.addImageWaterMark(image, config) : image;
        // 保存至粘贴板
        if (Boolean.TRUE.equals(config.getAutoCopyPayboard())) {
            copyImage(waterMarkImage);
        }
        // 提示保存截图
        List<AnAction> actionList = new ArrayList<>();
        actionList.add(new NotificationAction("Click save") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                CodeScreenshotHandler.saveImage(waterMarkImage, project, config);
            }
        });
        if (Boolean.FALSE.equals(config.getAutoCopyPayboard())) {
            actionList.add(new NotificationAction("Click copy") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    copyImage(waterMarkImage);
                }
            });
        }
        NotifyUtil.notify("Save Code Screenshot", actionList.toArray(AnAction[]::new));
    }

    /**
     * 是否短时间内重复截取
     *
     * @param selectedText 所选文本
     * @return boolean
     * @author mabin
     * @date 2024/06/21 17:27
     */
    private boolean repeatScreen(String selectedText) {
        if (StringUtils.isBlank(selectedText)) {
            return false;
        }
        String md5 = SecureUtil.md5(selectedText);
        if (Objects.nonNull(SCREEN_CACHE.getIfPresent(md5))) {
            System.out.println("md5=" + md5);
            return true;
        }
        SCREEN_CACHE.put(md5, md5);
        return false;
    }

    /**
     * 复制图像
     *
     * @param image 图像
     * @author mabin
     * @date 2024/06/05 11:08
     */
    private static void copyImage(BufferedImage image) {
        Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
        cp.setContents(new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.imageFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }

            @NotNull
            @Override
            public Object getTransferData(DataFlavor flavor) {
                return image;
            }
        }, null);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(project) && Objects.nonNull(editor)
                && editor.getSelectionModel().hasSelection());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

}
