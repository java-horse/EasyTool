package easy.config.translate;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.enums.TranslateEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 翻译渠道配置持久化
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/03 15:39
 **/

@State(name = "EasyCharTranslateConfig", storages = {@Storage("easyCharTranslateConfig.xml")})
public class TranslateConfigComponent implements PersistentStateComponent<TranslateConfig> {

    private TranslateConfig translateConfig;

    @Nullable
    @Override
    public TranslateConfig getState() {
        if (Objects.isNull(translateConfig)) {
            translateConfig = new TranslateConfig();
            translateConfig.setTranslateChannel(TranslateEnum.BAIDU.getTranslate());
        }
        return translateConfig;
    }

    @Override
    public void loadState(@NotNull TranslateConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
