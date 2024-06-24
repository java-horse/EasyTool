package easy.restful.icons;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.IconLoader;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.helper.ServiceHelper;
import easy.restful.api.HttpMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Icons {

    private static CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

    private Icons() {
    }

    @NotNull
    public static Icon load(@NotNull String path) {
        return IconLoader.getIcon(path, Icons.class);
    }

    /**
     * 获取方法对应的图标
     *
     * @param method 请求类型
     * @return icon
     */
    @NotNull
    public static Icon getMethodIcon(@Nullable HttpMethod method) {
        return getMethodIcon(method, false);
    }

    public static Icon getMethodIcon(@Nullable HttpMethod method, boolean selected) {
        IconType iconType = Boolean.TRUE.equals(commonConfig.getSearchApiDefaultIconRadioButton()) ? new DefaultIconType() : new CuteIconType();
        method = method == null ? HttpMethod.REQUEST : method;
        if (selected) {
            return iconType.getSelectIcon(method);
        }
        return iconType.getDefaultIcon(method);
    }

}
