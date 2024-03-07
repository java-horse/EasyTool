package easy.enums;

import easy.icons.EasyIcons;

import javax.swing.*;

public enum BackgroundImageActionEnum {

    START("Start", "Random Background Image", EasyIcons.ICON.GREEN),
    PAUSE("Pause", "Pause Random Background Image", EasyIcons.ICON.YELLOW),
    CLEAR("Clear", "Clear Random Background Image", EasyIcons.ICON.RED),
    RESTART("Restart", "Restart Random Background Image", EasyIcons.ICON.GREEN);

    BackgroundImageActionEnum(String title, String desc, Icon icon) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
    }

    public final String title;
    public final String desc;
    public final Icon icon;

}
