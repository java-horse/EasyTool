package easy.util;

import com.intellij.openapi.ui.Messages;
import easy.base.Constants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MessageUtil {

    private MessageUtil() {
    }

    public static int showInfoMessage(@NotNull String message) {
        return Messages.showDialog(message, Constants.PLUGIN_NAME, new String[]{Messages.getOkButton()}, 0, Messages.getInformationIcon());
    }

    public static int showErrorDialog(@NotNull String message) {
        return Messages.showDialog(message, Constants.PLUGIN_NAME, new String[]{Messages.getOkButton()}, 0, Messages.getErrorIcon());
    }

    public static int showWarningDialog(@NotNull String message) {
        return Messages.showDialog(message, Constants.PLUGIN_NAME, new String[]{Messages.getOkButton()}, 0, Messages.getWarningIcon());
    }

    public static int showQuestionDialog(@NotNull String message) {
        return Messages.showDialog(message, Constants.PLUGIN_NAME, new String[]{Messages.getOkButton()}, 0, Messages.getQuestionIcon());
    }

    public static int showOkCancelDialog(@NotNull String message) {
        return showOkCancelDialog(message, Messages.getQuestionIcon());
    }

    public static int showOkCancelDialog(@NotNull String message, @NotNull String okButtonText) {
        return Messages.showOkCancelDialog(message, Constants.PLUGIN_NAME, okButtonText, BundleUtil.getI18n("global.button.cancel.text"), Messages.getQuestionIcon());
    }

    public static int showOkCancelDialog(@NotNull String message, @NotNull String okButtonText, @NotNull String cancelButtonText) {
        return Messages.showOkCancelDialog(message, Constants.PLUGIN_NAME, okButtonText, cancelButtonText, Messages.getQuestionIcon());
    }

    public static int showOkCancelDialog(@NotNull String message, @NotNull Icon icon) {
        return Messages.showOkCancelDialog(message, Constants.PLUGIN_NAME, BundleUtil.getI18n("global.button.confirm.text"),
                BundleUtil.getI18n("global.button.cancel.text"), icon);
    }

    public static int showOkCancelDialog(@NotNull String message, @NotNull String okButtonText, @NotNull Icon icon) {
        return Messages.showOkCancelDialog(message, Constants.PLUGIN_NAME, okButtonText, BundleUtil.getI18n("global.button.cancel.text"), icon);
    }

    public static int showOkCancelDialog(@NotNull String message, @NotNull Icon icon, @NotNull String cancelButtonText) {
        return Messages.showOkCancelDialog(message, Constants.PLUGIN_NAME, BundleUtil.getI18n("global.button.confirm.text"), cancelButtonText, icon);
    }

    public static int showOkCancelDialog(@NotNull String message, @NotNull String okButtonText, @NotNull String cancelButtonText, @NotNull Icon icon) {
        return Messages.showOkCancelDialog(message, Constants.PLUGIN_NAME, okButtonText, cancelButtonText, icon);
    }

    public static int showYesNoDialog(@NotNull String message) {
        return Messages.showYesNoDialog(message, Constants.PLUGIN_NAME, BundleUtil.getI18n("global.button.yes.text"),
                BundleUtil.getI18n("global.button.no.text"), Messages.getQuestionIcon());
    }

    public static int showYesNoDialog(@NotNull String message, @NotNull Icon icon) {
        return Messages.showYesNoDialog(message, Constants.PLUGIN_NAME, icon);
    }


}
