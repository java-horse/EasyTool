package easy.config.screenshot;

import cn.hutool.core.date.DatePattern;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import easy.enums.FontStyleEnum;
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
            codeScreenshotConfig.setInnerPadding(5D);
            codeScreenshotConfig.setOuterPadding(5D);
            codeScreenshotConfig.setWindowRoundness(10);
            codeScreenshotConfig.setBackgroundColor(0xffabb8c3);
            codeScreenshotConfig.setRemoveIndentation(Boolean.TRUE);
            codeScreenshotConfig.setShowWindowIcons(Boolean.TRUE);
            codeScreenshotConfig.setAutoCopyPayboard(Boolean.TRUE);
            codeScreenshotConfig.setCustomFileName("screenshot_");
            codeScreenshotConfig.setCustomFileNameFormat(DatePattern.PURE_DATETIME_PATTERN);
            codeScreenshotConfig.setCustomFileNameSuffix("png");
            codeScreenshotConfig.setWaterMarkFontFamily("微软雅黑");
            codeScreenshotConfig.setWaterMarkFontSize("18");
            codeScreenshotConfig.setWaterMarkFontStyle(FontStyleEnum.PLAIN.getName());
            codeScreenshotConfig.setWaterMarkFontText(Constants.PLUGIN_NAME);
            codeScreenshotConfig.setWaterMarkFontColor(0xffabb8c3);
            codeScreenshotConfig.setAutoAddWaterMark(Boolean.FALSE);
            codeScreenshotConfig.setAutoFullScreenWatermark(Boolean.FALSE);
            codeScreenshotConfig.setFontWaterMarkTransparency(2);
            codeScreenshotConfig.setFontWaterMarkRotate(20);
            codeScreenshotConfig.setFontWaterMarkSparsity(2);
        } else {
            codeScreenshotConfig.setScale(Objects.isNull(codeScreenshotConfig.getScale()) ? 1.5D : codeScreenshotConfig.getScale());
            codeScreenshotConfig.setInnerPadding(Objects.isNull(codeScreenshotConfig.getInnerPadding()) ? 5D : codeScreenshotConfig.getInnerPadding());
            codeScreenshotConfig.setOuterPadding(Objects.isNull(codeScreenshotConfig.getOuterPadding()) ? 5D : codeScreenshotConfig.getOuterPadding());
            codeScreenshotConfig.setWindowRoundness(Objects.isNull(codeScreenshotConfig.getWindowRoundness()) ? 10 : codeScreenshotConfig.getWindowRoundness());
            codeScreenshotConfig.setBackgroundColor(Objects.isNull(codeScreenshotConfig.getBackgroundColor()) ? 0xffabb8c3 : codeScreenshotConfig.getBackgroundColor());
            codeScreenshotConfig.setRemoveIndentation(Objects.isNull(codeScreenshotConfig.getRemoveIndentation()) ? Boolean.TRUE : codeScreenshotConfig.getRemoveIndentation());
            codeScreenshotConfig.setShowWindowIcons(Objects.isNull(codeScreenshotConfig.getShowWindowIcons()) ? Boolean.TRUE : codeScreenshotConfig.getShowWindowIcons());
            codeScreenshotConfig.setAutoCopyPayboard(Objects.isNull(codeScreenshotConfig.getAutoCopyPayboard()) ? Boolean.TRUE : codeScreenshotConfig.getAutoCopyPayboard());
            codeScreenshotConfig.setCustomFileName(Objects.isNull(codeScreenshotConfig.getCustomFileName()) ? "screenshot_" : codeScreenshotConfig.getCustomFileName());
            codeScreenshotConfig.setCustomFileNameFormat(Objects.isNull(codeScreenshotConfig.getCustomFileNameFormat()) ? DatePattern.PURE_DATETIME_PATTERN : codeScreenshotConfig.getCustomFileNameFormat());
            codeScreenshotConfig.setCustomFileNameSuffix(Objects.isNull(codeScreenshotConfig.getCustomFileNameSuffix()) ? "png" : codeScreenshotConfig.getCustomFileNameSuffix());
            codeScreenshotConfig.setWaterMarkFontFamily(Objects.isNull(codeScreenshotConfig.getWaterMarkFontFamily()) ? "微软雅黑" : codeScreenshotConfig.getWaterMarkFontFamily());
            codeScreenshotConfig.setWaterMarkFontSize(Objects.isNull(codeScreenshotConfig.getWaterMarkFontSize()) ? "18" : codeScreenshotConfig.getWaterMarkFontSize());
            codeScreenshotConfig.setWaterMarkFontStyle(Objects.isNull(codeScreenshotConfig.getWaterMarkFontStyle()) ? FontStyleEnum.PLAIN.getName() : codeScreenshotConfig.getWaterMarkFontStyle());
            codeScreenshotConfig.setWaterMarkFontText(Objects.isNull(codeScreenshotConfig.getWaterMarkFontText()) ? Constants.PLUGIN_NAME : codeScreenshotConfig.getWaterMarkFontText());
            codeScreenshotConfig.setWaterMarkFontColor(Objects.isNull(codeScreenshotConfig.getWaterMarkFontColor()) ? 0xffabb8c3 : codeScreenshotConfig.getWaterMarkFontColor());
            codeScreenshotConfig.setAutoAddWaterMark(Objects.isNull(codeScreenshotConfig.getAutoAddWaterMark()) ? Boolean.FALSE : codeScreenshotConfig.getAutoAddWaterMark());
            codeScreenshotConfig.setAutoFullScreenWatermark(Objects.isNull(codeScreenshotConfig.getAutoFullScreenWatermark()) ? Boolean.FALSE : codeScreenshotConfig.getAutoFullScreenWatermark());
            codeScreenshotConfig.setFontWaterMarkTransparency(Objects.isNull(codeScreenshotConfig.getFontWaterMarkTransparency()) ? 2 : codeScreenshotConfig.getFontWaterMarkTransparency());
            codeScreenshotConfig.setFontWaterMarkRotate(Objects.isNull(codeScreenshotConfig.getFontWaterMarkRotate()) ? 20 : codeScreenshotConfig.getFontWaterMarkRotate());
            codeScreenshotConfig.setFontWaterMarkSparsity(Objects.isNull(codeScreenshotConfig.getFontWaterMarkSparsity()) ? 2 : codeScreenshotConfig.getFontWaterMarkSparsity());
        }
        return codeScreenshotConfig;
    }

    @Override
    public void loadState(@NotNull CodeScreenshotConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
