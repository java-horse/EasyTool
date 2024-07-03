package easy.enums;

import easy.form.widget.core.QrCodeCoreView;
import easy.form.widget.core.UrlEncodeCoreView;
import easy.form.widget.core.YmlConvertCoreView;
import easy.form.widget.core.cron.CronCoreView;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public enum WidgetCoreTabEnum {

    URL("URL转码", new UrlEncodeCoreView().getContent()),
    CRON("Cron预览", new CronCoreView().getContent()),
    QR("QR二维码", new QrCodeCoreView().getContent()),
    YML("YML转换", new YmlConvertCoreView().getContent());

    private final String title;
    private final Component component;

    public static Component getComponent(String title) {
        if (StringUtils.isBlank(title)) {
            return null;
        }
        for (WidgetCoreTabEnum value : values()) {
            if (value.getTitle().equals(title)) {
                return value.getComponent();
            }
        }
        return null;
    }

    WidgetCoreTabEnum(String title, Component component) {
        this.title = title;
        this.component = component;
    }

    public String getTitle() {
        return title;
    }

    public Component getComponent() {
        return component;
    }

}
