package easy.enums;

import easy.form.widget.core.*;
import easy.form.widget.core.clac.CalculatorCoreView;
import easy.form.widget.core.convert.ConvertCoreView;
import easy.form.widget.core.cron.CronCoreView;
import easy.form.widget.core.excel.ExcelCoreView;
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
    EXCEL("EXCEL解析") {
        @Override
        public Component getComponent() {
            return new ExcelCoreView().getContent();
        }
    },
    SIGN("Sign签到") {
        @Override
        public Component getComponent() {
            return new SignCoreView().getContent();
        }
    },
    TIMESTAMP("Timestamp转换") {
        @Override
        public Component getComponent() {
            return new TimestampCoreView().getContent();
        }
    },
    CONVERT("Convert转换") {
        @Override
        public Component getComponent() {
            return new ConvertCoreView().getContent();
        }
    },
    BASE64("Base64转码") {
        @Override
        public Component getComponent() {
            return new Base64CoreView().getContent();
        }
    },
    CALCULATOR("Simple计算") {
        @Override
        public Component getComponent() {
            return new CalculatorCoreView().getContent();
        }
    },
    WINDOWS_PROCESS("Windows进程") {
        @Override
        public Component getComponent() {
            return new ProcessCoreView().getContent();
        }
    },
    UUID("ID生成") {
        @Override
        public Component getComponent() {
            return new IdCoreView().getContent();
        }
    },
    ;


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
