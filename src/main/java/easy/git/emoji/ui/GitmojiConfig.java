package easy.git.emoji.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.util.Arrays;

public class GitmojiConfig implements SearchableConfigurable {
    private static final String CONFIG_RENDER_COMMIT_LOG = "gitmoji.render.commit.log";
    private static final String CONFIG_DISPLAY_EMOJI = "gitmoji.display.emoji";
    private static final String CONFIG_USE_UNICODE = "gitmoji.use.unicode";
    private static final String CONFIG_INSERT_IN_CURSOR_POSITION = "gitmoji.insert.cursor.position";
    private static final String CONFIG_INCLUDE_GITMOJI_DESCRIPTION = "gitmoji.include.description";
    private static final String CONFIG_AFTER_EMOJI = "gitmoji.after.emoji";
    private static final String CONFIG_LANGUAGE = "gitmoji.language";

    private Project project;
    private JPanel mainPanel;
    private JBCheckBox renderCommitLog;
    private JBCheckBox useUnicode;
    private JBCheckBox displayEmoji;
    private JBCheckBox insertInCursorPosition;
    private JBCheckBox includeGitmojiDescription;
    private JBLabel afterEmojiText;
    private JBRadioButton[] afterEmojiOptions;
    private ButtonGroup afterEmojiButtonGroup;
    private JBRadioButton englishLanguageOption;
    private JBRadioButton chineseSimplifiedLanguageOption;
    private PropertiesComponent propertiesComponent;

    public GitmojiConfig(Project project) {
        this.project = project;
        propertiesComponent = PropertiesComponent.getInstance(project);
    }

    private void createUIComponents() {
        // 初始化语言选项
        englishLanguageOption = new JBRadioButton("English");
        chineseSimplifiedLanguageOption = new JBRadioButton("简体中文");

        // 初始化afterEmoji的单选按钮组和选项
        afterEmojiOptions = new JBRadioButton[6];
        afterEmojiButtonGroup = new ButtonGroup();
        Arrays.stream(new String[]{"<nothing>", "<space>", ":", "(", "_", "["}).forEach(option -> {
            JBRadioButton radioButton = new JBRadioButton(option);
            afterEmojiOptions[Arrays.asList(afterEmojiOptions).indexOf(radioButton)] = radioButton;
            afterEmojiButtonGroup.add(radioButton);
        });

        // 初始化主面板布局
        mainPanel = new JPanel(new GridLayoutManager(6, 2));
    }

    @Override
    public JComponent createComponent() {
        createUIComponents();

        // 添加复选框和标签到主面板
        mainPanel.add(renderCommitLog, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        // ...（按照Kotlin代码，依次添加其他组件）

        // 设置语言选项初始状态
        setLanguage();

        return mainPanel;
    }

    private void setLanguage() {
        if (englishLanguageOption.isSelected()) {
            renderCommitLog.setText("Render commit log (replace text to emoji, need restart)");
            // ...（设置其他英文文本）
        } else if (chineseSimplifiedLanguageOption.isSelected()) {
            renderCommitLog.setText("渲染Commit日志中的文本Emoji（替换文本为Unicode表情，重启生效）");
            // ...（设置其他简体中文文本）
        }
    }

    private String languageOpt2Cfg(String languageOpt) {
        switch (languageOpt) {
            case "English":
                return "en";
            case "简体中文":
                return "zh-CN";
            default:
                return "en";
        }
    }

    private String languageCfg2Opt(String languageCfg) {
        switch (languageCfg) {
            case "en":
                return "English";
            case "zh-CN":
                return "简体中文";
            default:
                return "English";
        }
    }

    private String afterEmojiOpt2Cfg(String afterEmojiOpt) {
        switch (afterEmojiOpt) {
            case "<nothing>":
                return "";
            case "<space>":
                return " ";
            default:
                return afterEmojiOpt;
        }
    }

    private String afterEmojiCfg2Opt(String afterEmojiCfg) {
        switch (afterEmojiCfg) {
            case "":
                return "<nothing>";
            case " ":
                return "<space>";
            default:
                return afterEmojiCfg;
        }
    }

    @Override
    public boolean isModified() {
        return renderCommitLog.isSelected() != propertiesComponent.getBoolean(CONFIG_RENDER_COMMIT_LOG, true)
                || displayEmoji.isSelected() != propertiesComponent.getBoolean(CONFIG_DISPLAY_EMOJI, true)
                || useUnicode.isSelected() != propertiesComponent.getBoolean(CONFIG_USE_UNICODE, false)
                || insertInCursorPosition.isSelected() != propertiesComponent.getBoolean(CONFIG_INSERT_IN_CURSOR_POSITION, false)
                || includeGitmojiDescription.isSelected() != propertiesComponent.getBoolean(CONFIG_INCLUDE_GITMOJI_DESCRIPTION, false)
                || afterEmojiButtonGroup.getSelection().getActionCommand() != afterEmojiCfg2Opt(propertiesComponent.getValue(CONFIG_AFTER_EMOJI, " "))
                || (englishLanguageOption.isSelected() ? "English" : "简体中文") != languageCfg2Opt(propertiesComponent.getValue(CONFIG_LANGUAGE, "English"));
    }

    @Override
    public String getDisplayName() {
        return "Gitmoji";
    }

    @Override
    public String getId() {
        return "com.h3110w0r1d.gitmoji.config";
    }

    @Override
    public void apply() {
        propertiesComponent.setValue(CONFIG_RENDER_COMMIT_LOG, Boolean.toString(renderCommitLog.isSelected()));
        propertiesComponent.setValue(CONFIG_DISPLAY_EMOJI, Boolean.toString(displayEmoji.isSelected()));
        propertiesComponent.setValue(CONFIG_USE_UNICODE, Boolean.toString(useUnicode.isSelected()));
        propertiesComponent.setValue(CONFIG_INSERT_IN_CURSOR_POSITION, Boolean.toString(insertInCursorPosition.isSelected()));
        propertiesComponent.setValue(CONFIG_INCLUDE_GITMOJI_DESCRIPTION, Boolean.toString(includeGitmojiDescription.isSelected()));
        String selectedAfterEmoji = afterEmojiButtonGroup.getSelection().getActionCommand();
        propertiesComponent.setValue(CONFIG_AFTER_EMOJI, afterEmojiOpt2Cfg(selectedAfterEmoji));
        String selectedLanguage = englishLanguageOption.isSelected() ? "English" : "简体中文";
        propertiesComponent.setValue(CONFIG_LANGUAGE, languageOpt2Cfg(selectedLanguage));

        setLanguage();
    }

    @Override
    public void reset() {
        renderCommitLog.setSelected(Boolean.parseBoolean(propertiesComponent.getValue(CONFIG_RENDER_COMMIT_LOG, "true")));
        displayEmoji.setSelected(Boolean.parseBoolean(propertiesComponent.getValue(CONFIG_DISPLAY_EMOJI, "true")));
        useUnicode.setSelected(Boolean.parseBoolean(propertiesComponent.getValue(CONFIG_USE_UNICODE, "false")));
        insertInCursorPosition.setSelected(Boolean.parseBoolean(propertiesComponent.getValue(CONFIG_INSERT_IN_CURSOR_POSITION, "false")));
        includeGitmojiDescription.setSelected(Boolean.parseBoolean(propertiesComponent.getValue(CONFIG_INCLUDE_GITMOJI_DESCRIPTION, "false")));

        String configuredAfterEmoji = propertiesComponent.getValue(CONFIG_AFTER_EMOJI, " ");
        for (JBRadioButton option : afterEmojiOptions) {
            if (option.getActionCommand().equals(afterEmojiCfg2Opt(configuredAfterEmoji))) {
                option.setSelected(true);
                break;
            }
        }

        String configuredLanguage = propertiesComponent.getValue(CONFIG_LANGUAGE, "en");
        if ("en".equals(configuredLanguage)) {
            englishLanguageOption.setSelected(true);
        } else if ("zh-CN".equals(configuredLanguage)) {
            chineseSimplifiedLanguageOption.setSelected(true);
        }

        setLanguage();
    }
}

