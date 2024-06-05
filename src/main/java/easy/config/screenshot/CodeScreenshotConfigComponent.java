package easy.config.screenshot;

import cn.hutool.core.date.DatePattern;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(name = Constants.PLUGIN_NAME + "CodeScreenshotConfig", storages = {@Storage(Constants.PLUGIN_NAME + "CodeScreenshotConfig.xml")})
public class CodeScreenshotConfigComponent implements PersistentStateComponent<CodeScreenshotConfig> {

    private CodeScreenshotConfig codeScreenshotConfig;

    @Override
    public @Nullable CodeScreenshotConfig getState() {
        if (Objects.isNull(codeScreenshotConfig)) {
            codeScreenshotConfig = new CodeScreenshotConfig();
            codeScreenshotConfig.setScale(1.5D);
            codeScreenshotConfig.setInnerPadding(16D);
            codeScreenshotConfig.setOuterPadding(10D);
            codeScreenshotConfig.setWindowRoundness(10);
            codeScreenshotConfig.setBackgroundColor(0xffabb8c3);
            codeScreenshotConfig.setRemoveIndentation(Boolean.TRUE);
            codeScreenshotConfig.setShowWindowIcons(Boolean.TRUE);
            codeScreenshotConfig.setAutoCopyPayboard(Boolean.TRUE);
            codeScreenshotConfig.setCustomFileName("screenshot_");
            codeScreenshotConfig.setCustomFileNameFormat(DatePattern.PURE_DATETIME_PATTERN);
            codeScreenshotConfig.setCustomFileNameSuffix("png");
        } else {
            codeScreenshotConfig.setScale(Objects.isNull(codeScreenshotConfig.getScale()) ? 1.5D : codeScreenshotConfig.getScale());
            codeScreenshotConfig.setInnerPadding(Objects.isNull(codeScreenshotConfig.getInnerPadding()) ? 16D : codeScreenshotConfig.getInnerPadding());
            codeScreenshotConfig.setOuterPadding(Objects.isNull(codeScreenshotConfig.getOuterPadding()) ? 10D : codeScreenshotConfig.getOuterPadding());
            codeScreenshotConfig.setWindowRoundness(Objects.isNull(codeScreenshotConfig.getWindowRoundness()) ? 10 : codeScreenshotConfig.getWindowRoundness());
            codeScreenshotConfig.setBackgroundColor(Objects.isNull(codeScreenshotConfig.getBackgroundColor()) ? 0xffabb8c3 : codeScreenshotConfig.getBackgroundColor());
            codeScreenshotConfig.setRemoveIndentation(Objects.isNull(codeScreenshotConfig.getRemoveIndentation()) ? Boolean.TRUE : codeScreenshotConfig.getRemoveIndentation());
            codeScreenshotConfig.setShowWindowIcons(Objects.isNull(codeScreenshotConfig.getShowWindowIcons()) ? Boolean.TRUE : codeScreenshotConfig.getShowWindowIcons());
            codeScreenshotConfig.setAutoCopyPayboard(Objects.isNull(codeScreenshotConfig.getAutoCopyPayboard()) ? Boolean.TRUE : codeScreenshotConfig.getAutoCopyPayboard());
            codeScreenshotConfig.setCustomFileName(Objects.isNull(codeScreenshotConfig.getCustomFileName()) ? "screenshot_" : codeScreenshotConfig.getCustomFileName());
            codeScreenshotConfig.setCustomFileNameFormat(Objects.isNull(codeScreenshotConfig.getCustomFileNameFormat()) ? DatePattern.PURE_DATETIME_PATTERN : codeScreenshotConfig.getCustomFileNameFormat());
            codeScreenshotConfig.setCustomFileNameSuffix(Objects.isNull(codeScreenshotConfig.getCustomFileNameSuffix()) ? "png" : codeScreenshotConfig.getCustomFileNameSuffix());
        }
        return codeScreenshotConfig;
    }

    @Override
    public void loadState(@NotNull CodeScreenshotConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
