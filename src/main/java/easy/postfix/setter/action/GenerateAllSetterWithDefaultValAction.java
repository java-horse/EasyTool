package easy.postfix.setter.action;

import easy.enums.PostfixShortCutEnum;
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
        return PostfixShortCutEnum.GENERATE_SETTER.getName();
    }

    @NotNull
    @Override
    public String getText() {
        return PostfixShortCutEnum.GENERATE_SETTER.getName();
    }

}
