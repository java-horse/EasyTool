package easy.background;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.wm.impl.IdeBackgroundUtil;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class BackgroundService {

    private static ScheduledThreadPoolExecutor executor = null;

    public static void start() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        int interval = propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.INTERVAL, Constants.NUM.ZERO);
        if (interval == Constants.NUM.ZERO) {
            return;
        }
        if (executor != null) {
            stop();
        }
        executor = new ScheduledThreadPoolExecutor(1, new MyThreadFactory(Constants.PLUGIN_NAME + "_RandomBackgroundTask"));
        try {

            TimeUnit timeUnitEnum;
            int timeUnit = propertiesComponent.getInt(Constants.Persistence.BACKGROUND_IMAGE.TIME_UNIT, Constants.NUM.ONE);
            timeUnitEnum = switch (timeUnit) {
                case 0 -> TimeUnit.SECONDS;
                case 2 -> TimeUnit.HOURS;
                case 3 -> TimeUnit.DAYS;
                default -> TimeUnit.MINUTES;
            };
            int delay = propertiesComponent.isValueSet(IdeBackgroundUtil.EDITOR_PROP) ? interval : Constants.NUM.ZERO;
            executor.scheduleAtFixedRate(new RandomBackgroundTask(), delay, interval, timeUnitEnum);
        } catch (Exception e) {
            stop();
        }
    }

    public static void stop() {
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
        }
        executor = null;
    }

    public static void restart() {
        stop();
        start();
    }

    public static class MyThreadFactory implements ThreadFactory {

        private int counter;

        private String name;

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

}
