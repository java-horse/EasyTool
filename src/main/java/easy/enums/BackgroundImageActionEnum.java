package easy.enums;

import com.intellij.icons.AllIcons;

import javax.swing.*;

public enum BackgroundImageActionEnum {

    START("Start", "Random Background Image", AllIcons.Actions.Execute),
    STOP("Stop", "Stop Random Background Image", AllIcons.Actions.Restart),
    RESTART("Restart", "Restart Random Background Image", AllIcons.Actions.Suspend);

    BackgroundImageActionEnum(String title, String desc, Icon icon) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
    }

    public final String title;
    public final String desc;
    public final Icon icon;

}
