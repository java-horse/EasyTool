package easy.enums;

import easy.icons.EasyIcons;

import javax.swing.*;

public enum ApiDocEnum {

    YAPI("YApi", "YApi Doc Sync", EasyIcons.ICON.YAPI),
    API_FOX("ApiFox", "ApiFox Doc Sync", EasyIcons.ICON.APIFOX);

    private final String title;
    private final String desc;
    private final Icon icon;

    ApiDocEnum(String title, String desc, Icon icon) {
        this.title = title;
        this.desc = desc;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Icon getIcon() {
        return icon;
    }
}
