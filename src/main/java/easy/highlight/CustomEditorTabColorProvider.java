package easy.highlight;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorComposite;
import com.intellij.openapi.fileEditor.impl.EditorTabColorProvider;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.FileColorManager;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

public class CustomEditorTabColorProvider implements EditorTabColorProvider {

    private CommonConfig commonConfig = ApplicationManager.getApplication().getService(CommonConfigComponent.class).getState();

    @Nullable
    @Override
    public Color getEditorTabColor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        FileEditorManagerEx fileEditorManagerEx = FileEditorManagerEx.getInstanceEx(project);
        FileColorManager fileColorManager = FileColorManager.getInstance(project);
        EditorWindow activeWindow = fileEditorManagerEx.getCurrentWindow();
        // 是否开启Tab选项卡高亮配置
        if (Objects.nonNull(activeWindow) && (Boolean.TRUE.equals(commonConfig.getTabHighlightEnableCheckBox()))) {
            EditorComposite selectedComposite = activeWindow.getSelectedComposite();
            if (Objects.nonNull(selectedComposite) && Objects.equals(virtualFile, selectedComposite.getFile())) {
                return commonConfig.getPersistentColor().getColor();
            }
        }
        return fileColorManager.getFileColor(virtualFile);
    }

}
