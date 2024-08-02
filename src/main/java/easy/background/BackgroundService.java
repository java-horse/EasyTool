package easy.background;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import easy.base.Constants;
import easy.config.background.BackgroundImageConfig;
import easy.config.background.BackgroundImageConfigComponent;
import easy.enums.BackgroundImageChangeScopeEnum;
import easy.helper.ServiceHelper;
import easy.util.EasyCommonUtil;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BackgroundService {

    private static BackgroundImageConfig config = ServiceHelper.getService(BackgroundImageConfigComponent.class).getState();

    private static ScheduledThreadPoolExecutor executor = null;
    private List<String> randomImageList = null;
    private List<String> swapImageList = Collections.synchronizedList(new ArrayList<>());
    private String lastFolder = null;

    private static final Map<String, TimeUnit> TIME_UNIT_MAP = Map.of(
            "SECONDS", TimeUnit.SECONDS,
            "MINUTES", TimeUnit.MINUTES,
            "HOURS", TimeUnit.HOURS,
            "DAYS", TimeUnit.DAYS
    );

    /**
     * 开始
     *
     * @author mabin
     * @date 2024/08/02 10:41
     */
    public static void start() {
        if (config.getImageTimeModel() < Constants.NUM.FIVE) {
            return;
        }
        if (executor != null) {
            stop();
        }
        executor = new ScheduledThreadPoolExecutor(1, new MyThreadFactory(Constants.PLUGIN_NAME + "-RandomBackgroundTask"));
        try {
            executor.scheduleAtFixedRate(new RandomBackgroundTask(), Constants.NUM.TWO, config.getImageTimeModel(),
                    TIME_UNIT_MAP.getOrDefault(config.getImageTimeUnit(), TimeUnit.MINUTES));
            Console.log("start success...");
        } catch (Exception e) {
            Console.log("start error...", e);
            stop();
        }
    }

    /**
     * 停止
     *
     * @author mabin
     * @date 2024/08/02 10:52
     */
    public static void stop() {
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = null;
        Console.log("stop success...");
    }

    /**
     * 是否正在运行
     *
     * @return boolean
     * @author mabin
     * @date 2024/08/02 10:53
     */
    public static boolean isRunning() {
        return executor != null && !executor.isTerminated();
    }

    /**
     * 重新启动
     *
     * @author mabin
     * @date 2024/08/02 10:53
     */
    public static void restart() {
        stop();
        ThreadUtil.sleep(1000);
        start();
    }

    /**
     * 线程工厂
     *
     * @author mabin
     * @project EasyTool
     * @package easy.background.BackgroundService
     * @date 2024/08/02 10:37
     */
    public static class MyThreadFactory implements ThreadFactory {
        private int counter;

        private final String name;

        MyThreadFactory(String name) {
            counter = 0;
            this.name = name;
        }

        @Override
        public Thread newThread(@NotNull Runnable run) {
            Thread t = new Thread(run, name + "-Thread-" + counter);
            counter++;
            return t;
        }
    }

    /**
     * 随机背景图任务
     *
     * @author mabin
     * @project EasyTool
     * @package easy.background.BackgroundService
     * @date 2024/08/02 10:37
     */
    public static class RandomBackgroundTask implements Runnable {
        @Override
        public void run() {
            String folder = config.getImageFilePath();
            if (StringUtils.isBlank(folder)) {
                NotifyUtil.notify("Background image folder not set", EasyCommonUtil.getPluginSettingAction());
                stop();
                return;
            }
            if (!new File(folder).exists()) {
                NotifyUtil.notify("Background image folder not set", EasyCommonUtil.getPluginSettingAction());
                stop();
                return;
            }
            String changeScope = config.getImageScope();
            if (StringUtils.isBlank(changeScope)) {
                stop();
                return;
            }
            String image = ImagesHandler.INSTANCE.getRandomImage(folder);
            if (image == null) {
                NotifyUtil.notify("Background image folder no image resource found", EasyCommonUtil.getPluginSettingAction());
                stop();
                return;
            }
            PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
            if (BackgroundImageChangeScopeEnum.BOTH.getName().equals(changeScope)) {
                propertiesComponent.setValue(IdeBackgroundUtil.EDITOR_PROP, image);
                propertiesComponent.setValue(IdeBackgroundUtil.FRAME_PROP, image);
            } else if (BackgroundImageChangeScopeEnum.EDITOR.getName().equals(changeScope)) {
                propertiesComponent.setValue(IdeBackgroundUtil.EDITOR_PROP, image);
            } else if (BackgroundImageChangeScopeEnum.FRAME.getName().equals(changeScope)) {
                propertiesComponent.setValue(IdeBackgroundUtil.FRAME_PROP, image);
            }
            Console.log("{}, {} change image: {}", DateUtil.now(), Thread.currentThread().getName(), image);
        }
    }

}
