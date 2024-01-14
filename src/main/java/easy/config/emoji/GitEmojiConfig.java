package easy.config.emoji;

/**
 * Git Emoji配置项
 *
 * @author 马滨
 * @date 2024/01/13 14:18
 **/

public class GitEmojiConfig {

    private Boolean renderCommitLogCheckBox;
    private Boolean useUnicodeCheckBox;
    private Boolean displayEmojiCheckBox;
    private Boolean insertInCursorPositionCheckBox;
    private Boolean includeEmojiDescCheckBox;
    private String afterEmojiComboBox;
    private String afterEmojiRealValue;
    private String languageComboBox;
    private String languageRealValue;

    public Boolean getRenderCommitLogCheckBox() {
        return renderCommitLogCheckBox;
    }

    public void setRenderCommitLogCheckBox(Boolean renderCommitLogCheckBox) {
        this.renderCommitLogCheckBox = renderCommitLogCheckBox;
    }

    public Boolean getUseUnicodeCheckBox() {
        return useUnicodeCheckBox;
    }

    public void setUseUnicodeCheckBox(Boolean useUnicodeCheckBox) {
        this.useUnicodeCheckBox = useUnicodeCheckBox;
    }

    public Boolean getDisplayEmojiCheckBox() {
        return displayEmojiCheckBox;
    }

    public void setDisplayEmojiCheckBox(Boolean displayEmojiCheckBox) {
        this.displayEmojiCheckBox = displayEmojiCheckBox;
    }

    public Boolean getInsertInCursorPositionCheckBox() {
        return insertInCursorPositionCheckBox;
    }

    public void setInsertInCursorPositionCheckBox(Boolean insertInCursorPositionCheckBox) {
        this.insertInCursorPositionCheckBox = insertInCursorPositionCheckBox;
    }

    public Boolean getIncludeEmojiDescCheckBox() {
        return includeEmojiDescCheckBox;
    }

    public void setIncludeEmojiDescCheckBox(Boolean includeEmojiDescCheckBox) {
        this.includeEmojiDescCheckBox = includeEmojiDescCheckBox;
    }

    public String getAfterEmojiComboBox() {
        return afterEmojiComboBox;
    }

    public void setAfterEmojiComboBox(String afterEmojiComboBox) {
        this.afterEmojiComboBox = afterEmojiComboBox;
    }

    public String getLanguageComboBox() {
        return languageComboBox;
    }

    public void setLanguageComboBox(String languageComboBox) {
        this.languageComboBox = languageComboBox;
    }

    public String getAfterEmojiRealValue() {
        return afterEmojiRealValue;
    }

    public void setAfterEmojiRealValue(String afterEmojiRealValue) {
        this.afterEmojiRealValue = afterEmojiRealValue;
    }

    public String getLanguageRealValue() {
        return languageRealValue;
    }

    public void setLanguageRealValue(String languageRealValue) {
        this.languageRealValue = languageRealValue;
    }

    @Override
    public String toString() {
        return "GitEmojiConfig{" +
                "renderCommitLogCheckBox=" + renderCommitLogCheckBox +
                ", useUnicodeCheckBox=" + useUnicodeCheckBox +
                ", displayEmojiCheckBox=" + displayEmojiCheckBox +
                ", insertInCursorPositionCheckBox=" + insertInCursorPositionCheckBox +
                ", includeEmojiDescCheckBox=" + includeEmojiDescCheckBox +
                ", afterEmojiComboBox='" + afterEmojiComboBox + '\'' +
                ", afterEmojiRealValue='" + afterEmojiRealValue + '\'' +
                ", languageComboBox='" + languageComboBox + '\'' +
                ", languageRealValue='" + languageRealValue + '\'' +
                '}';
    }

}
