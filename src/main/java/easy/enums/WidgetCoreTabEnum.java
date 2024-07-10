package easy.enums;

import easy.form.widget.core.QrCodeCoreView;
import easy.form.widget.core.TimestampCoreView;
import easy.form.widget.core.UrlEncodeCoreView;
import easy.form.widget.core.YmlConvertCoreView;
import easy.form.widget.core.cron.CronCoreView;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public enum WidgetCoreTabEnum {

    URL("URL转码") {
        @Override
        public Component getComponent() {
            return new UrlEncodeCoreView().getContent();
        }
    },
    CRON("Cron预览") {
        @Override
        public Component getComponent() {
            return new CronCoreView().getContent();
        }
    },
    QR("QR二维码") {
        @Override
        public Component getComponent() {
            return new QrCodeCoreView().getContent();
        }
    },
    YML("YML转换") {
        @Override
        public Component getComponent() {
            return new YmlConvertCoreView().getContent();
        }
    },
    TIMESTAMP("Timestamp转换") {
        @Override
        public Component getComponent() {
            return new TimestampCoreView().getContent();
        }
    };

    private final String title;

    public abstract Component getComponent();

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

    WidgetCoreTabEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
