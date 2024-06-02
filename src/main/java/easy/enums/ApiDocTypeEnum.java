package easy.enums;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import easy.action.api.ApiDocSettingAction;
import easy.action.api.YApiAction;
import easy.action.api.YApiImportAction;
import easy.icons.EasyIcons;

import javax.swing.*;

public enum ApiDocTypeEnum {

    SETTING("Setting", AllIcons.General.Settings) {
        @Override
        public AnAction getAction() {
            return new ApiDocSettingAction();
        }
    },
    YAPI("YApi", EasyIcons.ICON.YAPI) {
        @Override
        public AnAction getAction() {
            return new YApiAction();
        }
    },
    YAPI_IMPORT("YApi Import", AllIcons.Actions.Upload) {
        @Override
        public AnAction getAction() {
            return new YApiImportAction();
        }
    };

    private final String title;
    private final Icon icon;

    ApiDocTypeEnum(String title, Icon icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public Icon getIcon() {
        return icon;
    }

    public abstract AnAction getAction();

}
