package easy.action;

import com.google.gson.Gson;
import com.intellij.ide.TextCopyProvider;
import com.intellij.ide.util.PropertiesComponent;
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
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.CommitMessage;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBList;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.vcs.commit.message.CommitMessageInspectionProfile;
import easy.base.Constants;
import easy.config.enoji.GitEmojiConfig;
import easy.config.enoji.GitEmojiConfigComponent;
import easy.git.emoji.base.GitmojiData;
import easy.git.emoji.base.Gitmojis;
import easy.util.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitCommitAction extends AnAction {

    private GitEmojiConfig gitEmojiConfig = ApplicationManager.getApplication().getService(GitEmojiConfigComponent.class).getState();

    private final Pattern regexPattern = Pattern.compile(":[a-z0-9_]+:");
    private final ArrayList<GitmojiData> gitmojis = new ArrayList<>();

    {
        loadLocalGitEmoji(gitEmojiConfig.getLanguageComboBox());
    }

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
        if (commitMessage != null) {
            if (CollectionUtils.isEmpty(gitmojis)) {
                loadLocalGitEmoji(gitEmojiConfig.getLanguageComboBox());
            }
            JBPopup popup = createPopup(project, commitMessage, gitmojis);
            popup.showInBestPositionFor(actionEvent.getDataContext());
        }
    }

    private JBPopup createPopup(Project project, CommitMessage commitMessage, List<GitmojiData> gitmojis) {
        final GitmojiData[] chosenMessage = {null};
        final GitmojiData[] selectedMessage = {null};
        int rightMargin = CommitMessageInspectionProfile.getSubjectRightMargin(project);
        Object previewCommandGroup = ObjectUtils.sentinel("Preview Commit Message");
        String currentCommitMessage = commitMessage.getEditorField().getText();
        int currentOffset = commitMessage.getEditorField().getCaretModel().getOffset();

        DefaultListModel<GitmojiData> model = new DefaultListModel<>();
        for (GitmojiData data : gitmojis) {
            model.addElement(data);
        }
        JBList<GitmojiData> jbList = new JBList<>(model);

        JBPopup jbPopup = JBPopupFactory.getInstance()
                .createListPopupBuilder(jbList)
                .setFont(commitMessage.getEditorField().getEditor().getColorsScheme().getFont(EditorFontType.PLAIN))
                .setVisibleRowCount(7)
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                .setItemSelectedCallback(item -> {
                    selectedMessage[0] = item;
                    if (item != null) {
                        preview(project, commitMessage, item, currentCommitMessage, currentOffset, previewCommandGroup);
                    }
                })
                .setItemChosenCallback(chosenItem -> chosenMessage[0] = chosenItem)
                .setRenderer(new ColoredListCellRenderer<GitmojiData>() {
                    @Override
                    protected void customizeCellRenderer(@NotNull JList<? extends GitmojiData> list, GitmojiData value, int index, boolean selected, boolean hasFocus) {
                        if (gitEmojiConfig.getDisplayEmojiCheckBox()) {
                            append(StringUtils.SPACE + value.getEmoji());
                        } else {
                            setIcon(value.getIcon());
                        }
                        append("\t" + value.getCode(), SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
                        appendTextPadding(5);
                        append(StringUtil.first(StringUtil.convertLineSeparators(value.getDescription(), ContentChooser.RETURN_SYMBOL), rightMargin, false));
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
                        return Arrays.stream(selectedMessage).map(GitmojiData::getCode).filter(Objects::nonNull).collect(Collectors.toList());
                    }
                };
            }
            return null;
        });
        return jbPopup;
    }

    private void preview(Project project, CommitMessage commitMessage, GitmojiData gitmoji, String currentCommitMessage, int currentOffset, Object groupId) {
        CommandProcessor.getInstance().executeCommand(project, () -> {
            boolean useUnicode = gitEmojiConfig.getUseUnicodeCheckBox();
            boolean insertInCursorPosition = gitEmojiConfig.getInsertInCursorPositionCheckBox();
            boolean includeGitmojiDescription = gitEmojiConfig.getIncludeEmojiDescCheckBox();
            int insertPosition = insertInCursorPosition ? currentOffset : 0;
            String textAfterUnicode = gitEmojiConfig.getAfterEmojiComboBox();
            String selectedGitmoji = useUnicode ? gitmoji.getEmoji() + textAfterUnicode : gitmoji.getCode() + textAfterUnicode;
            boolean replaced = false;
            String message = currentCommitMessage;
            if (!insertInCursorPosition) {
                if (useUnicode) {
                    for (GitmojiData emoji : gitmojis) {
                        if (message.contains(emoji.getEmoji() + textAfterUnicode)) {
                            message = message.replaceFirst(emoji.getEmoji() + textAfterUnicode, selectedGitmoji);
                            replaced = true;
                            break;
                        }
                    }
                } else {
                    String actualRegex = regexPattern + Pattern.quote(textAfterUnicode);
                    if (message.matches("(?s).*" + actualRegex + ".*")) {
                        message = actualRegex.replaceFirst(message, selectedGitmoji);
                        replaced = true;
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
            if (!insertInCursorPosition) {
                Editor editor = commitMessage.getEditorField().getEditor();
                editor.getSelectionModel().setSelection(insertPosition, editor.getDocument().getTextLength());
                editor.getCaretModel().moveToOffset(startPosition);
            } else if (currentOffset < startPosition) {
                Editor editor = commitMessage.getEditorField().getEditor();
                editor.getCaretModel().moveToOffset(startPosition);
            }
        }, StringUtils.EMPTY, groupId, UndoConfirmationPolicy.DEFAULT, commitMessage.getEditorField().getDocument());
    }

    private void cancelPreview(Project project, CommitMessage commitMessage) {
        UndoManager manager = UndoManager.getInstance(project);
        Editor editor = commitMessage.getEditorField().getEditor();
        FileEditor fileEditor = (editor != null) ? TextEditorProvider.getInstance().getTextEditor(editor) : null;
        if (fileEditor != null && manager.isUndoAvailable(fileEditor)) {
            manager.undo(fileEditor);
        }
    }

    private void loadLocalGitEmoji(String language) {
        try (InputStream inputStream = getClass().getResourceAsStream("/emoji/json/" + language + ".json")) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                loadGitEmoji(content.toString());
            }
        } catch (IOException e) {
            // Handle exception
        }
    }

    private void loadGitEmoji(String text) {
        gitmojis.clear();
        Gitmojis gitmojisJson = JsonUtil.fromJson(text, Gitmojis.class);
        if (Objects.isNull(gitmojisJson)) {
            return;
        }
        for (Gitmojis.Gitmoji gitmoji : gitmojisJson.getGitmojis()) {
            this.gitmojis.add(new GitmojiData(gitmoji.getCode(), gitmoji.getEmoji(), gitmoji.getDescription()));
        }
    }

}
