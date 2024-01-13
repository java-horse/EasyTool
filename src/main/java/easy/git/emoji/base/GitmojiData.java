package easy.git.emoji.base;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class GitmojiData {
    private final String code;
    private final String emoji;
    private final String description;
    private Icon icon;

    public GitmojiData(String code, String emoji, String description) {
        this.code = code;
        this.emoji = emoji;
        this.description = description;
    }

    public Icon getIcon() {
        if (icon == null) {
            try {
                icon = IconLoader.findIcon("/emoji/images/" + code.replace(":", "") + ".png", GitmojiData.class, false, true);
                if (icon == null) {
                    icon = IconLoader.getIcon("/emoji/images/anguished.png", GitmojiData.class);
                }
            } catch (Exception e) {
                icon = IconLoader.getIcon("/emoji/images/anguished.png", GitmojiData.class);
            }
        }
        return icon;
    }

}


