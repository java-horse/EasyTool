package easy.action.background;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import easy.background.BackgroundService;
import easy.background.ImagesHandler;
import easy.background.RandomBackgroundTask;
import easy.base.Constants;
import easy.enums.BackgroundImageActionEnum;
import easy.util.EasyCommonUtil;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BackgroundImageAction extends AnAction {

    private final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();

    public BackgroundImageAction(BackgroundImageActionEnum backgroundImageActionEnum) {
        super(backgroundImageActionEnum.title, backgroundImageActionEnum.desc, backgroundImageActionEnum.icon);
        if (StringUtils.equals(backgroundImageActionEnum.title, BackgroundImageActionEnum.START.title)
                && propertiesComponent.getBoolean(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, false)) {
            BackgroundService.start();
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        // 背景图片文件夹是否定义
        String folder = propertiesComponent.getValue(Constants.Persistence.BACKGROUND_IMAGE.FOLDER);
        if (StringUtils.isBlank(folder)) {
            NotifyUtil.notify("Background image folder not set", EasyCommonUtil.getPluginSettingAction());
            return;
        }
        String actionText = e.getPresentation().getText();
        boolean changeSwitch = propertiesComponent.getBoolean(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, false);
        if (StringUtils.equals(actionText, BackgroundImageActionEnum.START.title)) {
            // 按照当前状态启动或者重启轮播图切换线程
            if (changeSwitch) {
                BackgroundService.restart();
            } else {
                new RandomBackgroundTask().run();
                propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, true);
            }
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.RESTART.title)) {
            // 背景图片列表重新洗牌并启动
            ImagesHandler.INSTANCE.resetRandomImageList();
            if (!changeSwitch) {
                BackgroundService.restart();
                propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, true);
            }
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.PAUSE.title)) {
            // 暂停背景图切换（停止线程，保留已存在的背景图）
            BackgroundService.stop();
            propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, false);
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.CLEAR.title)) {
            // 暂停背景图切换（停止线程，清空已存在的背景图）
            BackgroundService.stop();
            propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, false);
            propertiesComponent.setValue(IdeBackgroundUtil.EDITOR_PROP, null);
            propertiesComponent.setValue(IdeBackgroundUtil.FRAME_PROP, null);
            ImagesHandler.INSTANCE.clearRandomImageList();
        }
    }

}
