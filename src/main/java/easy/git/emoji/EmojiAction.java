package easy.git.emoji;

import com.google.gson.Gson;
import com.intellij.ide.TextCopyProvider;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.ui.CommitMessage;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.UIUtil;
import easy.git.emoji.base.GitmojiData;
import easy.git.emoji.base.Gitmojis;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.editorActions.TextBlockTransferable.convertLineSeparators;
import static com.intellij.openapi.util.text.StringUtil.first;
import static com.intellij.ui.speedSearch.SpeedSearchUtil.applySpeedSearchHighlighting;
import static com.intellij.vcs.commit.message.CommitMessageInspectionProfile.getSubjectRightMargin;

public class GitCommitAction extends AnAction {

    private static final String CONFIG_LANGUAGE = "gitmoji.language";
    private static final String CONFIG_DISPLAY_EMOJI = "gitmoji.display.emoji";
    // ... 其他配置项

    private List<GitmojiData> gitmojis = new ArrayList<>();
    private PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    private String language = "en";

    public GitCommitAction() {
        setEnabledInModalContext(true);
        language = propertiesComponent.getValue(CONFIG_LANGUAGE, "en");
        loadRemoteGitmoji();
    }

    private final String regexPattern = ":[a-z0-9_]+:";

    @Override
    public boolean isDumbAware() {
        return true;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        CommitMessage commitMessage = getCommitMessage(e);

        if (commitMessage != null && project != null) {
            if (!language.equals(propertiesComponent.getValue(CONFIG_LANGUAGE, "en"))) {
                language = propertiesComponent.getValue(CONFIG_LANGUAGE, "en");
                loadRemoteGitmoji();
            }
            JBPopup popup = createPopup(project, commitMessage, gitmojis);
            popup.showInBestPositionFor(e.getDataContext());
        }
    }

    private JBPopup createPopup(Project project, CommitMessage commitMessage, List<GitmojiData> listGitmoji) {
        GitmojiData chosenMessage = null;
        GitmojiData selectedMessage = null;
        int rightMargin = getSubjectRightMargin(project);
        boolean displayEmoji = propertiesComponent.getBoolean(CONFIG_DISPLAY_EMOJI, Gitmojis.defaultDisplayEmoji());
        String currentCommitMessage = commitMessage.getEditorField().getText();
        int currentOffset = commitMessage.getEditorField().getCaretModel().getOffset();

        JBPopup jbPopup = JBPopupFactory.getInstance().createPopupChooserBuilder(listGitmoji)
                .setFont(commitMessage.getEditorField().getEditor().getColorsScheme().getFont(EditorFontType.PLAIN))
                .setVisibleRowCount(7)
                .setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                .setItemSelectedCallback(gitmoji -> {
                    selectedMessage = gitmoji;
                    preview(project, commitMessage, gitmoji, currentCommitMessage, currentOffset);
                })
                .setItemChosenCallback(chosenMessageConsumer -> chosenMessage = chosenMessageConsumer)
                .setRenderer(new ColoredListCellRenderer<GitmojiData>() {
                    @Override
                    protected void customizeCellRenderer(JList<? extends GitmojiData> list, GitmojiData value, int index, boolean selected, boolean hasFocus) {
                        if (displayEmoji) {
                            append(" " + value.getEmoji());
                        } else {
                            setIcon(value.getIcon());
                        }
                        append("\t" + value.getCode(), SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES);
                        appendTextPadding(5);
                        append(
                                first(
                                        convertLineSeparators(value.getDescription(), RETURN_SYMBOL),
                                        rightMargin,
                                        false
                                )
                        );
                        applySpeedSearchHighlighting(list, this, true, selected);
                    }
                })
                .addListener(new JBPopupListener() {
                    @Override
                    public void beforeShown(LightweightWindowEvent event) {
                        JBPopup popup = (JBPopup) event.asPopup();
                        RelativePoint relativePoint = new RelativePoint(commitMessage.getEditorField(), new Point(0, -UIUtil.scale(3)));
                        Point screenPoint = new Point(relativePoint.screenPoint()).translate(0, -popup.getSize().height);

                        popup.setLocation(screenPoint);
                    }

                    @Override
                    public void onClosed(LightweightWindowEvent event) {
                        IdeFocusManager.findInstance().requestFocus(commitMessage.getEditorField(), true);
                        ApplicationManager.getApplication().invokeLater(() -> {
                            if (chosenMessage != null) {
                                applyGitmojiToCommitMessage(project, commitMessage, chosenMessage, currentCommitMessage, currentOffset);
                            } else {
                                cancelPreview(project, commitMessage);
                            }
                        });
                    }
                })
                .setNamerForFiltering(gitmoji -> gitmoji.getCode() + " " + gitmoji.getDescription())
                .setAutoPackHeightOnFiltering(false)
                .createPopup();

        jbPopup.setDataProvider(dataId -> {
            if (CommonDataKeys.COPY_PROVIDER.getName().equals(dataId)) {
                return new TextCopyProvider() {
                    @Override
                    public String[] getTextLinesToCopy() {
                        return selectedMessage != null ? new String[]{selectedMessage.getCode()} : null;
                    }
                };
            }
            return null;
        });

        return jbPopup;
    }

    private void preview(
            Project project,
            CommitMessage commitMessage,
            GitmojiData gitmoji,
            String currentCommitMessage,
            int currentOffset,
            Object groupId
    ) {
        PropertiesComponent properties = PropertiesComponent.getInstance(project);
        boolean useUnicode = properties.getBoolean(CONFIG_USE_UNICODE, false);
        boolean insertInCursorPosition = properties.getBoolean(CONFIG_INSERT_IN_CURSOR_POSITION, false);
        boolean includeGitmojiDescription = properties.getBoolean(CONFIG_INCLUDE_GITMOJI_DESCRIPTION, false);

        String message = currentCommitMessage;
        int insertPosition = insertInCursorPosition ? currentOffset : 0;
        String textAfterEmoji = properties.getValue(CONFIG_AFTER_EMOJI, " ");
        String selectedGitmoji = useUnicode ? gitmoji.getEmoji() + textAfterEmoji : gitmoji.getCode() + textAfterEmoji;
        boolean replaced = false;

        if (!insertInCursorPosition) {
            if (useUnicode) {
                for (GitmojiData moji : gitmojis) {
                    if (message.contains(moji.getEmoji() + textAfterEmoji)) {
                        message = message.replaceFirst(Pattern.quote(moji.getEmoji() + textAfterEmoji), selectedGitmoji);
                        replaced = true;
                        break;
                    }
                }
            } else {
                Pattern actualRegex = Pattern.compile(regexPattern + Pattern.quote(textAfterEmoji));
                Matcher matcher = actualRegex.matcher(message);
                if (matcher.find()) {
                    message = matcher.replaceAll(selectedGitmoji);
                    replaced = true;
                }
            }
        }

        if (!replaced) {
            message = insertAt(message, insertPosition, selectedGitmoji);
        }
        int startPosition = insertPosition + selectedGitmoji.length();

        if (includeGitmojiDescription) {
            message = message.substring(0, startPosition) + gitmoji.getDescription();
        }

        commitMessage.setCommitMessage(message);
        Editor editor = commitMessage.getEditorField().getEditor();
        CaretModel caretModel = editor.getCaretModel();

        if (!insertInCursorPosition) {
            SelectionModel selectionModel = editor.getSelectionModel();
            selectionModel.selectAll();
            caretModel.removeSecondaryCarets();
            caretModel.setSelection(startPosition, editor.getDocument().getTextLength(), false);
        } else {
            caretModel.moveToOffset(startPosition);
        }
    }

    private void cancelPreview(Project project, CommitMessage commitMessage) {
        UndoManager undoManager = UndoManager.getInstance(project);
        FileEditor fileEditor = TextEditorProvider.getInstance().getTextEditor(commitMessage.getEditorField().getEditor());

        if (undoManager.isUndoAvailable(fileEditor)) {
            undoManager.undo(fileEditor);
        }
    }

    private CommitMessage getCommitMessage(AnActionEvent e) {
        return e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
    }

    private void loadRemoteGitmoji() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SafeGuardInterceptor())
                .build();

        String url = getUrlBasedOnLanguage();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadLocalGitmoji(language);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    loadLocalGitmoji(language);
                } else {
                    loadGitmoji(response.body().string());
                }
            }
        });
    }

    private String getUrlBasedOnLanguage() {
        switch (language) {
            case "en":
                return "https://gitmoji.dev/api/gitmojis";
            default:
                return "https://raw.githubusercontent.com/h3110w0r1d-y/gitmoji-plus-intellij-plugin/master/src/main/resources/gitmojis/" + language + ".json";
        }
    }

    private void loadLocalGitmoji(String language) {
        try (InputStream inputStream = getClass().getResourceAsStream("/gitmojis/" + language + ".json")) {
            if (inputStream != null) {
                String text = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
                loadGitmoji(text);
            }
        } catch (IOException e) {
            // Handle exception
        }
    }

    private void loadGitmoji(String jsonText) {
        Gson gson = new Gson();
        Gitmojis gitmojisResponse = gson.fromJson(jsonText, Gitmojis.class);

        gitmojis.clear();
        for (Gitmoji gitmoji : gitmojisResponse.getGitmojis()) {
            gitmojis.add(new GitmojiData(gitmoji.getCode(), gitmoji.getEmoji(), gitmoji.getDescription()));
        }
    }

}
