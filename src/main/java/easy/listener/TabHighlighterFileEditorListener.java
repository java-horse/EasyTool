package easy.listener;

import cn.hutool.core.convert.Convert;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorComposite;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.FileColorManager;
import com.intellij.ui.JBColor;
import com.intellij.util.messages.Topic;
import easy.base.Constants;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.helper.ServiceHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Objects;

public class TabHighlighterFileEditorListener implements FileEditorManagerListener, EventListener {
    public static final Topic<TabHighlighterFileEditorListener> CHANGE_HIGHLIGHTER_TOPIC = Topic.create("Highlighter Topic", TabHighlighterFileEditorListener.class);
    private CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();
    private LinkedList<VirtualFile> highlightQueue = new LinkedList<>();
    private final Project project;

    public TabHighlighterFileEditorListener(Project project) {
        this.project = project;
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        FileEditor selectedEditor = fileEditorManager.getSelectedEditor();
        if (Objects.nonNull(selectedEditor)) {
            VirtualFile file = selectedEditor.getFile();
            SwingUtilities.invokeLater(() -> {
                if (Boolean.TRUE.equals(commonConfig.getTabHighlightEnableCheckBox())) {
                    highlightQueue.add(file);
                    handleSelectionChange(file);
                } else {
                    unHighlightQueueFile();
                }
            });
        }
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager fileEditorManager, @NotNull VirtualFile virtualFile) {
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent fileEditorManagerEvent) {
        if (Objects.equals(fileEditorManagerEvent.getManager().getProject(), project)) {
            if (Boolean.TRUE.equals(commonConfig.getTabHighlightEnableCheckBox())) {
                handleSelectionChange(fileEditorManagerEvent.getNewFile());
            } else {
                unHighlightQueueFile();
            }
        }
    }

    private void handleSelectionChange(VirtualFile newFile) {
        FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
        FileColorManager fileColorManager = FileColorManager.getInstance(project);
        if (highlightQueue.contains(newFile)) {
            highlightQueue.removeIf(file -> Objects.equals(file, newFile));
        }
        if (highlightQueue.size() + Constants.NUM.ONE > Convert.toInt(commonConfig.getTabHighlightSizeComboBox())) {
            VirtualFile virtualFile = highlightQueue.removeFirst();
            for (EditorWindow editorWindow : manager.getWindows()) {
                unHighlightSafe(fileColorManager, virtualFile, editorWindow);
            }
        }
        highlightQueue.add(newFile);
        for (EditorWindow editorWindow : manager.getWindows()) {
            int size = highlightQueue.size();
            for (int i = 0; i < size; i++) {
                highlightSafe(highlightQueue.get(i), editorWindow, size - i - Constants.NUM.ONE);
            }
        }
    }

    private void highlightSafe(VirtualFile file, EditorWindow editorWindow, Integer colorFactor) {
        if (Objects.nonNull(file) && Objects.nonNull(editorWindow.getComposite(file))) {
            highlight(file, editorWindow, colorFactor);
        }
    }

    private void unHighlightSafe(FileColorManager fileColorManager, VirtualFile oldFile, EditorWindow editorWindow) {
        if (Objects.nonNull(oldFile) && Objects.nonNull(editorWindow.getComposite(oldFile))) {
            unHighlight(fileColorManager, oldFile, editorWindow);
        }
    }

    private void highlight(VirtualFile file, EditorWindow editorWindow, Integer colorFactor) {
        CommonConfig.PersistentColor persistentColor = commonConfig.getPersistentColor();
        int colorStep = colorFactor * Convert.toInt(commonConfig.getTabHighlightGradientStepFormattedTextField());
        int red = Math.max(persistentColor.getRed() - colorStep, Constants.NUM.ZERO);
        int green = Math.max(persistentColor.getGreen() - colorStep, Constants.NUM.ZERO);
        int blue = Math.max(persistentColor.getBlue() - colorStep, Constants.NUM.ZERO);
        setTabColor(new JBColor(Color.MAGENTA, new Color(red, green, blue)), file, editorWindow);
    }

    private void unHighlight(@NotNull FileColorManager fileColorManager, VirtualFile file, EditorWindow editorWindow) {
        setTabColor(fileColorManager.getFileColor(file), file, editorWindow);
    }

    private void setTabColor(Color color, @NotNull VirtualFile file, @NotNull EditorWindow editorWindow) {
        EditorComposite editorComposite = editorWindow.getComposite(file);
        int index = getEditorIndex(editorWindow, editorComposite);
        if (index >= 0) {
            editorWindow.getTabbedPane().getTabs().getTabAt(index).setTabColor(color);
        }
    }

    private int getEditorIndex(@NotNull EditorWindow editorWindow, EditorComposite editorComposite) {
        return editorWindow.getAllComposites().indexOf(editorComposite);
    }

    /**
     * 队列文件全部取消高亮
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/12/22 14:14
     */
    public void unHighlightQueueFile() {
        if (highlightQueue.isEmpty()) {
            return;
        }
        FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
        FileColorManager fileColorManager = FileColorManager.getInstance(project);
        for (EditorWindow editorWindow : manager.getWindows()) {
            for (VirtualFile virtualFile : highlightQueue) {
                unHighlightSafe(fileColorManager, virtualFile, editorWindow);
            }
        }
    }

}
