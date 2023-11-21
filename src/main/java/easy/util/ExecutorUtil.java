package easy.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局线程池
 *
 * @project: EasyChar
 * @package: easy.util
 * @author: mabin
 * @date: 2023/11/21 09:59:02
 */
public class ExecutorUtil {

    private static final int THREAD_POOL_SIZE = 10;

    private ExecutorService executorService;

    private static class LazyHolder {
        private static ExecutorUtil INSTANCE = new ExecutorUtil();
    }

    private ExecutorUtil() {
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public static ExecutorUtil getInstance() {
        return LazyHolder.INSTANCE;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

}
