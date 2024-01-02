package easy.settings;

import com.intellij.openapi.options.Configurable;
import easy.base.Constants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 插件设置主页面
 *
 * @project: EasyTool
 * @package: easy.settings
 * @author: mabin
 * @date: 2023/12/28 16:18:15
 */
public class EasyToolConfigurable implements Configurable {
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return Constants.PLUGIN_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        return null;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() {

    }

}
