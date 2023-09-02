package easy.init;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import easy.action.ConvertAction;

/**
 * EasyChar项目初始化处理
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/04/24 18:27
 **/

public class EasyCharComponent implements ApplicationComponent {

    @Override
    public void initComponent() {
        Application application = ApplicationManager.getApplication();
        application.invokeLater(ConvertAction::new);
    }

    @Override
    public void disposeComponent() {

    }

    @Override
    public String getComponentName() {
        return "EasyCharComponent";
    }

}
