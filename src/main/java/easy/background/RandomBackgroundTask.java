package easy.background;

import com.intellij.ide.util.PropertiesComponent;
import easy.base.Constants;
import easy.util.EasyCommonUtil;
import easy.util.NotificationUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class RandomBackgroundTask implements Runnable {

    @Override
    public void run() {
        PropertiesComponent prop = PropertiesComponent.getInstance();
        String folder = prop.getValue(Constants.Persistence.BACKGROUND_IMAGE.FOLDER);
        if (StringUtils.isBlank(folder)) {
            NotificationUtil.notify("Background image folder not set", EasyCommonUtil.getPluginSettingAction());
            BackgroundService.stop();
            return;
        }
        File file = new File(folder);
        if (!file.exists()) {
            NotificationUtil.notify("Background image folder not set", EasyCommonUtil.getPluginSettingAction());
            BackgroundService.stop();
            return;
        }
        String changeScope = prop.getValue(Constants.Persistence.BACKGROUND_IMAGE.CHANGE_SCOPE);
        if (StringUtils.isBlank(changeScope)) {
            BackgroundService.stop();
            return;
        }
        String[] scopeArray = changeScope.split(",");
        String image = null;
        for (int i = 0; i < scopeArray.length; i++) {
            if (i == 0) {
                image = ImagesHandler.INSTANCE.getRandomImage(folder);
            }
            if (image == null) {
                NotificationUtil.notify("Background image folder no image resource found", EasyCommonUtil.getPluginSettingAction());
                BackgroundService.stop();
                return;
            }
            if (image.contains(",")) {
                NotificationUtil.notify("IDE wont load images with ',' character image：" + image, EasyCommonUtil.getPluginSettingAction());
                BackgroundService.stop();
                return;
            }
            String type = scopeArray[i];
            String lastImage = prop.getValue(type);
            if (lastImage != null && lastImage.contains(",")) {
                String s1 = image + lastImage.substring(lastImage.indexOf(","));
                prop.setValue(type, s1);
            } else {
                prop.setValue(type, image);
            }
        }
    }

}
