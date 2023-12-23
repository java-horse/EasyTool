package easy.enums;

import easy.icons.EasyIcons;

import javax.swing.*;

/**
 * CopyUrl方式枚举
 *
 * @project: EasyTool
 * @package: easy.enums
 * @author: mabin
 * @date: 2023/12/23 11:22:56
 */
public enum CopyUrlEnum {

    COPY_FULL_URL("Copy Full Url", EasyIcons.ICON.COPY_FULL),
    COPY_HTTP_URL("Copy Http Url", EasyIcons.ICON.COPY_HTTP);

    public final String title;
    public final Icon icon;

    CopyUrlEnum(String title, Icon icon) {
        this.title = title;
        this.icon = icon;
    }

}
