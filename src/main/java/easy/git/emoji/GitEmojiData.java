package easy.git.emoji;

import com.intellij.openapi.util.IconLoader;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class GitEmojiData {
    private String code;
    private String emoji;
    private String description;
    private Icon icon;

    public GitEmojiData(String code, String emoji, String description) {
        this.code = code;
        this.emoji = emoji;
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDescription() {
        return description;
    }

    public Icon getIcon() {
        if (icon == null) {
            try {
                icon = IconLoader.findIcon("/emoji/images/" + code.replace(":", StringUtils.EMPTY) + ".png", GitEmojiData.class, false, true);
                if (icon == null) {
                    icon = IconLoader.getIcon("/emoji/images/anguished.png", GitEmojiData.class);
                }
            } catch (Exception e) {
                icon = IconLoader.getIcon("/emoji/images/anguished.png", GitEmojiData.class);
            }
        }
        return icon;
    }

    @Override
    public String toString() {
        return "GitmojiData{" +
                "code='" + code + '\'' +
                ", emoji='" + emoji + '\'' +
                ", description='" + description +
                '}';
    }
}

