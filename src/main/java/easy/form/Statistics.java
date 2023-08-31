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
        return initTextField();
    }

    public static Statistics getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Statistics();
        }
        return INSTANCE;
    }

    /**
     * 初始化TextField实例
     *
     * @param
     * @return javax.swing.JTextField
     * @author mabin
     * @date 2023/8/31 14:56
     **/
    private JTextField initTextField() {
        if (textField == null) {
            textField = new JTextField();
            textField.setFocusable(false);
            textField.setBorder(null);
        }
        return textField;
    }

}
