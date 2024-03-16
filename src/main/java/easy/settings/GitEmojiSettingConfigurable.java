package easy.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.emoji.GitEmojiConfig;
import easy.config.emoji.GitEmojiConfigComponent;
import easy.enums.GitEmojiLanguageEnum;
import easy.form.GitEmojiSettingView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * Git Emoji配置项
 *
 * @author mabin
 * @date 2024/01/13 20:42
 **/
public class GitEmojiSettingConfigurable implements Configurable {

    private GitEmojiConfig gitEmojiConfig = ApplicationManager.getApplication().getService(GitEmojiConfigComponent.class).getState();
    private GitEmojiSettingView gitEmojiSettingView = new GitEmojiSettingView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "GitEmoji";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return gitEmojiSettingView.getComponent();
    }

    @Override
    public boolean isModified() {
        return !Objects.equals(gitEmojiConfig.getRenderCommitLogCheckBox(), gitEmojiSettingView.getRenderCommitLogCheckBox().isSelected())
                || !Objects.equals(gitEmojiConfig.getUseUnicodeCheckBox(), gitEmojiSettingView.getUseUnicodeCheckBox().isSelected())
                || !Objects.equals(gitEmojiConfig.getDisplayEmojiCheckBox(), gitEmojiSettingView.getDisplayEmojiCheckBox().isSelected())
                || !Objects.equals(gitEmojiConfig.getInsertInCursorPositionCheckBox(), gitEmojiSettingView.getInsertInCursorPositionCheckBox().isSelected())
                || !Objects.equals(gitEmojiConfig.getIncludeEmojiDescCheckBox(), gitEmojiSettingView.getIncludeEmojiDescCheckBox().isSelected())
                || !Objects.equals(gitEmojiConfig.getAfterEmojiComboBox(), gitEmojiSettingView.getAfterEmojiComboBox().getSelectedItem())
                || !Objects.equals(gitEmojiConfig.getLanguageComboBox(), gitEmojiSettingView.getLanguageComboBox().getSelectedItem());
    }

    @Override
    public void reset() {
        gitEmojiSettingView.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        gitEmojiConfig.setRenderCommitLogCheckBox(gitEmojiSettingView.getRenderCommitLogCheckBox().isSelected());
        gitEmojiConfig.setUseUnicodeCheckBox(gitEmojiSettingView.getUseUnicodeCheckBox().isSelected());
        gitEmojiConfig.setDisplayEmojiCheckBox(gitEmojiSettingView.getDisplayEmojiCheckBox().isSelected());
        gitEmojiConfig.setInsertInCursorPositionCheckBox(gitEmojiSettingView.getInsertInCursorPositionCheckBox().isSelected());
        gitEmojiConfig.setIncludeEmojiDescCheckBox(gitEmojiSettingView.getIncludeEmojiDescCheckBox().isSelected());
        String afterEmojiComboBox = String.valueOf(gitEmojiSettingView.getAfterEmojiComboBox().getSelectedItem());
        gitEmojiConfig.setAfterEmojiComboBox(afterEmojiComboBox);
        if (StringUtils.equals(afterEmojiComboBox, "<nothing>")) {
            gitEmojiConfig.setAfterEmojiRealValue(StringUtils.EMPTY);
        } else if (StringUtils.equals(afterEmojiComboBox, "<space>")) {
            gitEmojiConfig.setAfterEmojiRealValue(StringUtils.SPACE);
        } else {
            gitEmojiConfig.setAfterEmojiRealValue(afterEmojiComboBox);
        }
        String languageComboBox = String.valueOf(gitEmojiSettingView.getLanguageComboBox().getSelectedItem());
        gitEmojiConfig.setLanguageComboBox(languageComboBox);
        gitEmojiConfig.setLanguageRealValue(GitEmojiLanguageEnum.getCode(languageComboBox));
    }

}
