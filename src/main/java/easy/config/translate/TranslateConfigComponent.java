package easy.config.translate;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.ModelConstants;
import easy.enums.BaiDuTranslateDomainEnum;
import easy.enums.OpenModelTranslateEnum;
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
            translateConfig.setBaiduDomainCheckBox(Boolean.FALSE);
            translateConfig.setBaiduDomainComboBox(BaiDuTranslateDomainEnum.IT.getName());
            translateConfig.setOpenModelChannel(OpenModelTranslateEnum.TONG_YI.getModel());
            translateConfig.setTyModel(ModelConstants.TONG_YI.MAX.getModel());
        } else {
            translateConfig.setTranslateChannel(Objects.isNull(translateConfig.getTranslateChannel()) ? TranslateEnum.BAIDU.getTranslate() : translateConfig.getTranslateChannel());
            translateConfig.setBaiduDomainCheckBox(Objects.isNull(translateConfig.getBaiduDomainCheckBox()) ? Boolean.FALSE : translateConfig.getBaiduDomainCheckBox());
            translateConfig.setBaiduDomainComboBox(Objects.isNull(translateConfig.getBaiduDomainComboBox()) ? BaiDuTranslateDomainEnum.IT.getName() : translateConfig.getBaiduDomainComboBox());
            translateConfig.setOpenModelChannel(Objects.isNull(translateConfig.getOpenModelChannel()) ? OpenModelTranslateEnum.TONG_YI.getModel() : translateConfig.getOpenModelChannel());
            translateConfig.setTyModel(Objects.isNull(translateConfig.getTyModel()) ? ModelConstants.TONG_YI.MAX.getModel() : translateConfig.getTyModel());
        }
        return translateConfig;
    }

    @Override
    public void loadState(@NotNull TranslateConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
