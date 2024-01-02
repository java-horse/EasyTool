package easy.postfix.setter.action;

import easy.base.Constants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GenerateAllSetterWithDefaultValAction extends GenerateAllSetterAction {

    public GenerateAllSetterWithDefaultValAction() {
        super(true);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return Constants.POSTFIX_SHORTCUT_NAME.GENERATE_SETTER;
    }

    @NotNull
    @Override
    public String getText() {
        return Constants.POSTFIX_SHORTCUT_NAME.GENERATE_SETTER;
    }

}
