package easy.util;

import com.intellij.openapi.ui.Messages;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MessageUtil {

    private MessageUtil() {
    }

    public static void showInfoMessage(@NotNull String message) {
        Messages.showInfoMessage(message, Constants.PLUGIN_NAME);
    }

    public static void showErrorDialog(@NotNull String message) {
        Messages.showErrorDialog(message, Constants.PLUGIN_NAME);
    }

    public static void showWarningDialog(@NotNull String message) {
        Messages.showWarningDialog(message, Constants.PLUGIN_NAME);
    }

    public static void showQuestionDialog(@NotNull String message) {
        Messages.showDialog(message, Constants.PLUGIN_NAME, new String[]{Messages.getOkButton()}, 0, Messages.getWarningIcon());

    }

    public static int showOkCancelDialog(@NotNull String message) {
        return showOkCancelDialog(message, Messages.getQuestionIcon());
    }

    public static int showOkCancelDialog(@NotNull String message, @NotNull Icon icon) {
        return Messages.showOkCancelDialog(message, Constants.PLUGIN_NAME, BundleUtil.getI18n("global.button.confirm.text"),
                BundleUtil.getI18n("global.button.cancel.text"), icon);
    }

    public static int showYesNoDialog(@NotNull String message) {
        return Messages.showYesNoDialog(message, Constants.PLUGIN_NAME, BundleUtil.getI18n("global.button.yes.text"),
                BundleUtil.getI18n("global.button.no.text"), Messages.getQuestionIcon());
    }


}
