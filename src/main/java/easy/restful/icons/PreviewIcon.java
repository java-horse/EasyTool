package easy.restful.icons;

import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PreviewIcon extends JBLabel {

    public PreviewIcon(@NotNull String text, @NotNull Icon icon) {
        super(text, icon, CENTER);
    }
}
