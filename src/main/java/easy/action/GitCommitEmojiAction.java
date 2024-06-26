package easy.action;

import com.intellij.ide.TextCopyProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.UndoConfirmationPolicy;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.ContentChooser;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.CommitMessage;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.vcs.commit.message.CommitMessageInspectionProfile;
import easy.config.emoji.GitEmojiConfig;
import easy.config.emoji.GitEmojiConfigComponent;
import easy.git.emoji.GitEmojiData;
import easy.git.emoji.GitEmojiHelper;
import easy.git.emoji.Gitmojis;
import easy.helper.ServiceHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GitCommitEmojiAction extends AnAction {

    private GitEmojiConfig gitEmojiConfig = ServiceHelper.getService(GitEmojiConfigComponent.class).getState();
    private final GitEmojiHelper gitEmojiResourceLoadService = new GitEmojiHelper();

    @Override
    public boolean isDumbAware() {
        return true;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent actionEvent) {
        Project project = actionEvent.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        CommitMessage commitMessage = (CommitMessage) actionEvent.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
        if (Objects.isNull(commitMessage)) {
            return;
        }
        List<GitEmojiData> gitmojis = gitEmojiResourceLoadService.loadEmoji(gitEmojiConfig.getLanguageRealValue());
        JBPopup popup = createPopup(project, commitMessage, gitmojis);
        popup.showInBestPositionFor(actionEvent.getDataContext());
    }

    /**
     * 创建 git emoji 弹窗面板
     *
     * @param project
     * @param commitMessage
     * @param gitmojis
     * @return com.intellij.openapi.ui.popup.JBPopup
     * @author mabin
     * @date 2024/1/15 10:06
     */
    private JBPopup createPopup(Project project, CommitMessage commitMessage, List<GitEmojiData> gitmojis) {
        final GitEmojiData[] chosenMessage = {null};
        final GitEmojiData[] selectedMessage = {null};
        Object previewCommandGroup = ObjectUtils.sentinel("Preview Commit Message");
        String currentCommitMessage = commitMessage.getEditorField().getText();
        int currentOffset = commitMessage.getEditorField().getCaretModel().getOffset();

        IPopupChooserBuilder<GitEmojiData> popupChooserBuilder = JBPopupFactory.getInstance().createPopupChooserBuilder(gitmojis);
        Editor editor = commitMessage.getEditorField().getEditor();
        JBPopup jbPopup = popupChooserBuilder
                .setFont(Objects.nonNull(editor) ? editor.getColorsScheme().getFont(EditorFontType.PLAIN) : EditorFontType.getGlobalPlainFont())
                .setVisibleRowCount(8)
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                .setItemSelectedCallback(item -> {
                    selectedMessage[0] = item;
                    if (item != null) {
                        preview(project, commitMessage, item, currentCommitMessage, currentOffset, previewCommandGroup, gitmojis);
                    }
                })
                .setItemChosenCallback(chosenItem -> chosenMessage[0] = chosenItem)
                .setRenderer(new ColoredListCellRenderer<GitEmojiData>() {
                    @Override
                    protected void customizeCellRenderer(@NotNull JList<? extends GitEmojiData> list, GitEmojiData value, int index, boolean selected, boolean hasFocus) {
                        if (Boolean.TRUE.equals(gitEmojiConfig.getDisplayEmojiCheckBox())) {
                            append(StringUtils.SPACE + value.getEmoji());
                        } else {
                            setIcon(value.getIcon());
                        }
                        append("\t" + value.getCode(), SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
                        appendTextPadding(5);
                        append(StringUtil.first(StringUtil.convertLineSeparators(value.getDescription(), ContentChooser.RETURN_SYMBOL),
                                CommitMessageInspectionProfile.getSubjectRightMargin(project), false));
                        SpeedSearchUtil.applySpeedSearchHighlighting(list, this, true, selected);
                    }
                })
                .addListener(new JBPopupListener() {
                    @Override
                    public void beforeShown(@NotNull LightweightWindowEvent event) {
                        JBPopup popup = event.asPopup();
                        RelativePoint relativePoint = new RelativePoint(commitMessage.getEditorField(), new Point(0, -JBUIScale.scale(3)));
                        Point screenPoint = relativePoint.getScreenPoint();
                        screenPoint.translate(0, -popup.getSize().height);
                        popup.setLocation(screenPoint);
                    }

                    @Override
                    public void onClosed(@NotNull LightweightWindowEvent event) {
                        IdeFocusManager.findInstanceByComponent(commitMessage.getEditorField()).requestFocus(commitMessage.getEditorField(), true);
                        ApplicationManager.getApplication().invokeLater(() -> {
                            if (chosenMessage[0] == null) {
                                cancelPreview(project, commitMessage);
                            }
                        });
                    }
                })
                .setNamerForFiltering(item -> item.getCode() + StringUtils.SPACE + item.getDescription())
                .setAutoPackHeightOnFiltering(false)
                .createPopup();
        jbPopup.setDataProvider((dataId) -> {
            if (PlatformDataKeys.COPY_PROVIDER.getName().equals(dataId)) {
                return new TextCopyProvider() {
                    @Override
                    public Collection<String> getTextLinesToCopy() {
                        return Arrays.stream(selectedMessage).map(GitEmojiData::getCode).filter(Objects::nonNull).collect(Collectors.toList());
                    }
                };
            }
            return null;
        });
        return jbPopup;
    }

    /**
     * git emoji 预览处理
     *
     * @param project
     * @param commitMessage
     * @param gitmoji
     * @param currentCommitMessage
     * @param currentOffset
     * @param groupId
     * @param gitmojis
     * @return void
     * @author mabin
     * @date 2024/1/15 10:26
     */
    private void preview(Project project, CommitMessage commitMessage, GitEmojiData gitmoji, String currentCommitMessage, int currentOffset, Object groupId, List<GitEmojiData> gitmojis) {
        CommandProcessor.getInstance().executeCommand(project, () -> {
            boolean useUnicode = gitEmojiConfig.getUseUnicodeCheckBox();
            boolean insertInCursorPosition = gitEmojiConfig.getInsertInCursorPositionCheckBox();
            boolean includeGitmojiDescription = gitEmojiConfig.getIncludeEmojiDescCheckBox();
            int insertPosition = insertInCursorPosition ? currentOffset : 0;
            String textAfterUnicode = gitEmojiConfig.getAfterEmojiRealValue();
            String selectedGitmoji = useUnicode ? gitmoji.getEmoji() + textAfterUnicode : gitmoji.getCode() + textAfterUnicode;
            boolean replaced = false;
            String message = currentCommitMessage;
            if (!insertInCursorPosition && (useUnicode)) {
                for (GitEmojiData emoji : gitmojis) {
                    if (message.contains(emoji.getEmoji() + textAfterUnicode)) {
                        message = message.replaceFirst(emoji.getEmoji() + textAfterUnicode, selectedGitmoji);
                        replaced = true;
                        break;
                    }
                }
            }
            if (!replaced) {
                message = Gitmojis.insertAt(message, insertPosition, selectedGitmoji);
            }
            int startPosition = insertPosition + selectedGitmoji.length();
            if (includeGitmojiDescription) {
                message = message.substring(0, startPosition) + gitmoji.getDescription();
            }
            commitMessage.setCommitMessage(message);
            Editor editor = commitMessage.getEditorField().getEditor();
            if (Objects.isNull(editor)) {
                return;
            }
            if (!insertInCursorPosition) {
                editor.getSelectionModel().setSelection(insertPosition, editor.getDocument().getTextLength());
                editor.getCaretModel().moveToOffset(startPosition);
            } else if (currentOffset < startPosition) {
                editor.getCaretModel().moveToOffset(startPosition);
            }
        }, StringUtils.EMPTY, groupId, UndoConfirmationPolicy.DEFAULT, commitMessage.getEditorField().getDocument());
    }

    /**
     * 取消 git emoji 预览处理
     *
     * @param project
     * @param commitMessage
     * @return void
     * @author mabin
     * @date 2024/1/15 10:27
     */
    private void cancelPreview(Project project, CommitMessage commitMessage) {
        UndoManager manager = UndoManager.getInstance(project);
        Editor editor = commitMessage.getEditorField().getEditor();
        FileEditor fileEditor = (editor != null) ? TextEditorProvider.getInstance().getTextEditor(editor) : null;
        if (fileEditor != null && manager.isUndoAvailable(fileEditor)) {
            manager.undo(fileEditor);
        }
    }

}
