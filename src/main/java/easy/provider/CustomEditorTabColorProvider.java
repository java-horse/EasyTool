package easy.provider;

import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorComposite;
import com.intellij.openapi.fileEditor.impl.EditorTabColorProvider;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.FileColorManager;
import com.intellij.ui.JBColor;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.helper.ServiceHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

public class CustomEditorTabColorProvider implements EditorTabColorProvider {

    private final CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

    @Nullable
    @Override
    public Color getEditorTabColor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        FileEditorManagerEx fileEditorManagerEx = FileEditorManagerEx.getInstanceEx(project);
        FileColorManager fileColorManager = FileColorManager.getInstance(project);
        try {
            // 是否开启Tab选项卡高亮配置
            EditorWindow activeWindow = fileEditorManagerEx.getCurrentWindow();
            if (Objects.nonNull(activeWindow) && Objects.nonNull(commonConfig) && Boolean.TRUE.equals(commonConfig.getTabHighlightEnableCheckBox())) {
                EditorComposite selectedComposite = activeWindow.getSelectedComposite();
                if (Objects.nonNull(selectedComposite) && Objects.equals(virtualFile, selectedComposite.getFile())) {
                    return new JBColor(new Color(commonConfig.getTabHighlightBackgroundColor(), true), new Color(commonConfig.getTabHighlightBackgroundColor(), true));
                }
            }
        } catch (Throwable ignored) {
        }
        return fileColorManager.getFileColor(virtualFile);
    }

}
