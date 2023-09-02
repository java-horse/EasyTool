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

    public static Statistics getInstance() {
        synchronized (Statistics.class) {
            if (INSTANCE == null) {
                INSTANCE = new Statistics();
            }
        }
        return INSTANCE;
    }

    public Statistics() {
        this.textField = new JTextField();
        this.textField.setFocusable(false);
        this.textField.setBorder(null);
    }

    public JTextField getTextField() {
        return textField;
    }

}
