package easy.action.background;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import easy.background.BackgroundService;
import easy.background.ImagesHandlerSingleton;
import easy.background.RandomBackgroundTask;
import easy.base.Constants;
import easy.enums.BackgroundImageActionEnum;
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
        String actionText = e.getPresentation().getText();
        boolean changeSwitch = propertiesComponent.getBoolean(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, false);
        if (StringUtils.equals(actionText, BackgroundImageActionEnum.START.title)) {
            if (changeSwitch) {
                BackgroundService.restart();
            } else {
                new RandomBackgroundTask().run();
                propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, true);
            }
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.RESTART.title)) {
            // 清空随机背景图片列表，重新按顺序轮播切换
            ImagesHandlerSingleton.INSTANCE.resetRandomImageList();
            if (!changeSwitch) {
                BackgroundService.restart();
                propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, true);
            }
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.STOP.title)) {
            BackgroundService.stop();
            propertiesComponent.setValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SWITCH, false);
            propertiesComponent.setValue(IdeBackgroundUtil.EDITOR_PROP, null);
            propertiesComponent.setValue(IdeBackgroundUtil.FRAME_PROP, null);
        }
    }

}
