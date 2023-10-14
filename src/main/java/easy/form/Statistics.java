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
    private JTextField totalTextField;

    public static Statistics getInstance() {
        synchronized (Statistics.class) {
            if (INSTANCE == null) {
                INSTANCE = new Statistics();
            }
        }
        return INSTANCE;
    }

    private void createUIComponents() {
        this.dayTextField = new JTextField();
        this.totalTextField = new JTextField();
    }

    public JTextField getDayTextField() {
        return dayTextField;
    }

    public JTextField getTotalTextField() {
        return totalTextField;
    }

    public JComponent getComponent() {
        return panel;
    }

}
