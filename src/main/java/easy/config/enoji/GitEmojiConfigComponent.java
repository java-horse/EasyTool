package easy.config.enoji;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Git Emoji配置持久化
 *
 * @author mabin
 * @date 2024/01/13 14:21
 **/
@State(name = Constants.PLUGIN_NAME + "GitEmojiConfig", storages = {@Storage(Constants.PLUGIN_NAME + "GitEmojiConfig.xml")})
public class GitEmojiConfigComponent implements PersistentStateComponent<GitEmojiConfig> {

    private GitEmojiConfig gitEmojiConfig;

    @Override
    public @Nullable GitEmojiConfig getState() {
        if (Objects.isNull(gitEmojiConfig)) {
            gitEmojiConfig = new GitEmojiConfig();
            gitEmojiConfig.setRenderCommitLogCheckBox(Boolean.TRUE);
            gitEmojiConfig.setDisplayEmojiCheckBox(Boolean.TRUE);
            gitEmojiConfig.setAfterEmojiComboBox("<space>");
            gitEmojiConfig.setLanguageComboBox("English");
        }
        return gitEmojiConfig;
    }

    @Override
    public void loadState(@NotNull GitEmojiConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
