package easy.form;

import javax.swing.*;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/05/22 17:28
 **/

public class Statistics {

    public static Statistics INSTANCE;
    private JTextField textField;

    public JTextField getTextField() {
        return textField;
    }

    public static Statistics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Statistics();
        }
        return INSTANCE;
    }

}
