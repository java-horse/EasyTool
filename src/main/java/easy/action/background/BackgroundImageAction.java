package easy.action.background;

import cn.hutool.core.io.FileUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import easy.background.BackgroundService;
import easy.background.ImagesHandler;
import easy.config.background.BackgroundImageConfig;
import easy.config.background.BackgroundImageConfigComponent;
import easy.enums.BackgroundImageActionEnum;
import easy.enums.BackgroundImageChangeScopeEnum;
import easy.helper.ServiceHelper;
import easy.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BackgroundImageAction extends AnAction {

    private BackgroundImageConfig config = ServiceHelper.getService(BackgroundImageConfigComponent.class).getState();

    public BackgroundImageAction(@NotNull BackgroundImageActionEnum backgroundImageActionEnum) {
        super(backgroundImageActionEnum.title, backgroundImageActionEnum.desc, backgroundImageActionEnum.icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (Objects.isNull(project)) {
            return;
        }
        String actionText = e.getPresentation().getText();
        boolean changeSwitch = config.getImageSwitch();
        if (StringUtils.equals(actionText, BackgroundImageActionEnum.START.title)) {
            // 按照当前状态启动或者重启轮播图切换线程
            if (changeSwitch) {
                BackgroundService.restart();
            } else {
                new BackgroundService.RandomBackgroundTask().run();
                config.setImageSwitch(Boolean.TRUE);
            }
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.RESTART.title)) {
            // 背景图片列表重新洗牌并启动
            ImagesHandler.INSTANCE.resetRandomImageList();
            if (!changeSwitch) {
                BackgroundService.restart();
                config.setImageSwitch(Boolean.TRUE);
            }
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.PAUSE.title)) {
            // 暂停背景图切换（停止线程，保留已存在的背景图）
            BackgroundService.stop();
            config.setImageSwitch(Boolean.FALSE);
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.CLEAR.title)) {
            // 暂停背景图切换（停止线程，清空已存在的背景图）
            BackgroundService.stop();
            config.setImageSwitch(Boolean.FALSE);
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
            propertiesComponent.setValue(IdeBackgroundUtil.EDITOR_PROP, null);
            propertiesComponent.setValue(IdeBackgroundUtil.FRAME_PROP, null);
            ImagesHandler.INSTANCE.clearRandomImageList();
        } else if (StringUtils.equals(actionText, BackgroundImageActionEnum.NEXT.title)) {
            // 手动切换下一张背景图
            if (BackgroundService.isRunning()) {
                MessageUtil.showWarningDialog("背景轮播任务正在运行中...");
                return;
            }
            if (StringUtils.isBlank(config.getImageFilePath()) || !FileUtil.exist(config.getImageFilePath())) {
                // TODO 显示跳转按钮
                MessageUtil.showOkCancelDialog("配置文件路径不存在");
                return;
            }
            String nextImage = ImagesHandler.INSTANCE.getRandomImage(config.getImageFilePath());
            if (StringUtils.isNotBlank(nextImage)) {
                PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
                if (BackgroundImageChangeScopeEnum.BOTH.getName().equals(config.getImageScope())) {
                    propertiesComponent.setValue(IdeBackgroundUtil.EDITOR_PROP, nextImage);
                    propertiesComponent.setValue(IdeBackgroundUtil.FRAME_PROP, nextImage);
                } else if (BackgroundImageChangeScopeEnum.FRAME.getName().equals(config.getImageScope())) {
                    propertiesComponent.setValue(IdeBackgroundUtil.FRAME_PROP, nextImage);
                } else if (BackgroundImageChangeScopeEnum.EDITOR.getName().equals(config.getImageScope())) {
                    propertiesComponent.setValue(IdeBackgroundUtil.EDITOR_PROP, nextImage);
                }
            }
        }
    }

}
