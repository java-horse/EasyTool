package easy.handler;

import com.intellij.openapi.application.ApplicationManager;

public class ServiceHelper {

    /**
     * 从应用全局获取已注册Service实例
     *
     * @param clazz clazz
     * @return {@link T }
     * @author mabin
     * @date 2024/04/02 13:48
     */
    public static <T> T getService(Class<T> clazz) {
        return ApplicationManager.getApplication().getService(clazz);
    }

}
