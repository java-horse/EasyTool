package easy.form;


import com.intellij.openapi.application.ApplicationManager;
import easy.config.emoji.GitEmojiConfig;
import easy.config.emoji.GitEmojiConfigComponent;
import easy.enums.GitEmojiLanguageEnum;
import easy.helper.ServiceHelper;
import easy.util.BundleUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class GitEmojiSettingView {

    private GitEmojiConfig gitEmojiConfig = ServiceHelper.getService(GitEmojiConfigComponent.class).getState();

    private JPanel panel;
    private JPanel emojiPanel;
    private JCheckBox renderCommitLogCheckBox;
    private JCheckBox useUnicodeCheckBox;
    private JCheckBox displayEmojiCheckBox;
    private JCheckBox insertInCursorPositionCheckBox;
    private JCheckBox includeEmojiDescCheckBox;
    private JLabel afterEmojiLabel;
    private JComboBox afterEmojiComboBox;
    private JLabel languageLabel;
    private JComboBox languageComboBox;

    public JComponent getComponent() {
        return panel;
    }

    public GitEmojiSettingView() {
        renderCommitLogCheckBox.setText(BundleUtil.getI18n("git.emoji.render.commit.log.text"));
        useUnicodeCheckBox.setText(BundleUtil.getI18n("git.emoji.use.unicode.text"));
        displayEmojiCheckBox.setText(BundleUtil.getI18n("git.emoji.display.emoji.text"));
        insertInCursorPositionCheckBox.setText(BundleUtil.getI18n("git.emoji.insert.in.cursor.position.text"));
        includeEmojiDescCheckBox.setText(BundleUtil.getI18n("git.emoji.include.git.emoji.description.text"));
        afterEmojiLabel.setText(BundleUtil.getI18n("git.emoji.after.emoji.text"));
        languageLabel.setText(BundleUtil.getI18n("git.emoji.language.text"));
    }


    private void createUIComponents() {
        gitEmojiConfig = ServiceHelper.getService(GitEmojiConfigComponent.class).getState();
    }

    public void reset() {
        setRenderCommitLogCheckBox(gitEmojiConfig.getRenderCommitLogCheckBox());
        setUseUnicodeCheckBox(gitEmojiConfig.getUseUnicodeCheckBox());
        setDisplayEmojiCheckBox(gitEmojiConfig.getDisplayEmojiCheckBox());
        setInsertInCursorPositionCheckBox(gitEmojiConfig.getInsertInCursorPositionCheckBox());
        setIncludeEmojiDescCheckBox(gitEmojiConfig.getIncludeEmojiDescCheckBox());
        String afterEmojiValue = gitEmojiConfig.getAfterEmojiRealValue();
        if (StringUtils.equals(afterEmojiValue, StringUtils.EMPTY)) {
            setAfterEmojiComboBox("<nothing>");
        } else if (StringUtils.equals(afterEmojiValue, StringUtils.SPACE)) {
            setAfterEmojiComboBox("<space>");
        } else {
            setAfterEmojiComboBox(afterEmojiValue);
        }
        setLanguageComboBox(GitEmojiLanguageEnum.getLanguage(gitEmojiConfig.getLanguageRealValue()));
    }

    public JPanel getEmojiPanel() {
        return emojiPanel;
    }

    public void setEmojiPanel(JPanel emojiPanel) {
        this.emojiPanel = emojiPanel;
    }

    public JCheckBox getRenderCommitLogCheckBox() {
        return renderCommitLogCheckBox;
    }

    public void setRenderCommitLogCheckBox(Boolean renderCommitLogCheckBox) {
        this.renderCommitLogCheckBox.setSelected(renderCommitLogCheckBox);
    }

    public JCheckBox getUseUnicodeCheckBox() {
        return useUnicodeCheckBox;
    }

    public void setUseUnicodeCheckBox(Boolean useUnicodeCheckBox) {
        this.useUnicodeCheckBox.setSelected(useUnicodeCheckBox);
    }

    public JCheckBox getDisplayEmojiCheckBox() {
        return displayEmojiCheckBox;
    }

    public void setDisplayEmojiCheckBox(Boolean displayEmojiCheckBox) {
        this.displayEmojiCheckBox.setSelected(displayEmojiCheckBox);
    }

    public JCheckBox getInsertInCursorPositionCheckBox() {
        return insertInCursorPositionCheckBox;
    }

    public void setInsertInCursorPositionCheckBox(Boolean insertInCursorPositionCheckBox) {
        this.insertInCursorPositionCheckBox.setSelected(insertInCursorPositionCheckBox);
    }

    public JCheckBox getIncludeEmojiDescCheckBox() {
        return includeEmojiDescCheckBox;
    }

    public void setIncludeEmojiDescCheckBox(Boolean includeEmojiDescCheckBox) {
        this.includeEmojiDescCheckBox.setSelected(includeEmojiDescCheckBox);
    }

    public JLabel getAfterEmojiLabel() {
        return afterEmojiLabel;
    }

    public void setAfterEmojiLabel(JLabel afterEmojiLabel) {
        this.afterEmojiLabel = afterEmojiLabel;
    }

    public JComboBox getAfterEmojiComboBox() {
        return afterEmojiComboBox;
    }

    public void setAfterEmojiComboBox(String afterEmojiComboBox) {
        this.afterEmojiComboBox.setSelectedItem(afterEmojiComboBox);
    }

    public JLabel getLanguageLabel() {
        return languageLabel;
    }

    public void setLanguageLabel(JLabel languageLabel) {
        this.languageLabel = languageLabel;
    }

    public JComboBox getLanguageComboBox() {
        return languageComboBox;
    }

    public void setLanguageComboBox(String languageComboBox) {
        this.languageComboBox.setSelectedItem(languageComboBox);
    }

}
