package easy.postfix.setter.action;

import easy.base.Constants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GenerateAllSetterWithoutDefaultValAction extends GenerateAllSetterAction {

    public GenerateAllSetterWithoutDefaultValAction() {
        super(false);
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return Constants.POSTFIX_SHORTCUT_NAME.GENERATE_SETTER_NO_DEFAULT_VAL;
    }

    @NotNull
    @Override
    public String getText() {
        return Constants.POSTFIX_SHORTCUT_NAME.GENERATE_SETTER_NO_DEFAULT_VAL;
    }

}
