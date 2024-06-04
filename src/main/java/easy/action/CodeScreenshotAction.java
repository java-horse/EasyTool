package easy.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.handler.CodeScreenshotHandler;
import easy.util.NotifyUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class CodeScreenshotAction extends AnAction {
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
        BufferedImage image = CodeScreenshotHandler.createImage(editor);
        if (Objects.isNull(image)) {
            NotifyUtil.notify(String.format("%s action generate code image fail", e.getPresentation().getText()), NotificationType.ERROR);
            return;
        }
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
        return ActionUpdateThread.EDT;
    }

}
