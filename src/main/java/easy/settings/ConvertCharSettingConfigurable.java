package easy.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import easy.config.convert.ConvertCharConfig;
import easy.config.convert.ConvertCharConfigComponent;
import easy.form.convert.ConvertCharView;
import easy.helper.ServiceHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class ConvertCharSettingConfigurable implements Configurable {

    private ConvertCharConfig config = ServiceHelper.getService(ConvertCharConfigComponent.class).getState();
    private ConvertCharView view = new ConvertCharView();

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ConvertChar";
    }

    @Override
    public @Nullable JComponent createComponent() {
        return view.getComponent();
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        int size = view.getCharMappingList().getModel().getSize();
        Map<String, String> convertCharMap = config.getConvertCharMap();
        for (int i = 0; i < size; i++) {
            Map.Entry<String, String> entry = view.getCharMappingList().getModel().getElementAt(i);
            convertCharMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
        config.setConvertCharMap(convertCharMap);
    }

}
