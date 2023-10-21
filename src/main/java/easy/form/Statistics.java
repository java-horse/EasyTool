package easy.form;

import javax.swing.*;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/05/22 17:28
 **/

public class Statistics {

    public static Statistics INSTANCE;
    private JPanel panel;
    private JTextField dayTextField;

    public static Statistics getInstance() {
        synchronized (Statistics.class) {
            if (INSTANCE == null) {
                INSTANCE = new Statistics();
            }
        }
        return INSTANCE;
    }

    public Statistics() {
        this.dayTextField = new JTextField();
        this.dayTextField.setFocusable(false);
        this.dayTextField.setBorder(null);
    }

    public JTextField getDayTextField() {
        return dayTextField;
    }

}
