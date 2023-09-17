package easy.form;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/09/09 08:49
 **/

public class TranslateResultView extends DialogWrapper {
    private JPanel panel;
    private JTextArea resultTextArea;
    private JScrollPane resultScrollPane;

    public TranslateResultView(String translate, String result) {
        super(false);
        init();
        setTitle(translate);
        resultTextArea.setText(result);
        resultTextArea.setSize(600, 600);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JTextArea getFromTextArea() {
        return resultTextArea;
    }

    public JScrollPane getFromScrollPane() {
        return resultScrollPane;
    }


}
