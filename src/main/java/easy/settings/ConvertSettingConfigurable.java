package easy.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import easy.handler.ConvertHandler;
import easy.util.BundleUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * EasyTool的中英文字符映射关系自定义配置
 *
 * @author mabin
 * @project EasyTool
 * @date 2023/04/24 14:39
 **/

public class ConvertSettingConfigurable implements Configurable {

    private JPanel settingPanel;
    private JTextField[] text1;
    private JTextField[] text2;
    private JLabel btnDefault;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Convert";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingPanel != null) {
            settingPanel.repaint();
            return settingPanel;
        }
        settingPanel = new JPanel();
        settingPanel.setLayout(null);

        text1 = new JTextField[Constants.TOTAL_LENGTH];
        text2 = new JTextField[Constants.TOTAL_LENGTH];
        JLabel[] labels1 = new JLabel[Constants.TOTAL_LENGTH];
        JLabel[] labels2 = new JLabel[Constants.TOTAL_LENGTH];
        for (int i = 0; i < Constants.TOTAL_LENGTH; i++) {
            text1[i] = new JTextField();
            text2[i] = new JTextField();
            labels1[i] = new JLabel();
            labels2[i] = new JLabel();
            text1[i].setBounds(35 + (i / 15) * 200, 32 * (i % 15), 60, 32);
            text2[i].setBounds(120 + (i / 15) * 200, 32 * (i % 15), 60, 32);
            labels1[i].setBounds(5 + (i / 15) * 200, 32 * (i % 15), 30, 32);
            labels2[i].setBounds(95 + (i / 15) * 200, 32 * (i % 15), 25, 32);
            labels1[i].setText((i + 1) + ".");
            labels2[i].setText("->");
            labels1[i].setHorizontalAlignment(SwingConstants.CENTER);
            labels2[i].setHorizontalAlignment(SwingConstants.CENTER);
            text1[i].setHorizontalAlignment(SwingConstants.CENTER);
            text2[i].setHorizontalAlignment(SwingConstants.CENTER);
            settingPanel.add(text1[i]);
            settingPanel.add(text2[i]);
            settingPanel.add(labels1[i]);
            settingPanel.add(labels2[i]);
        }
        btnDefault = new JLabel();
        btnDefault.setText(BundleUtil.getI18n("convert.reset.button.text"));
        btnDefault.setForeground(JBColor.BLUE);
        btnDefault.setBounds(30, 32 * 15, 100, 32);
        btnDefault.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDefault.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int result = Messages.showYesNoDialog(BundleUtil.getI18n("convert.reset.message"), BundleUtil.getI18n("convert.reset.button.text"), Messages.getQuestionIcon());
                if (result == MessageConstants.YES) {
                    PropertiesComponent.getInstance().setValue(Constants.Persistence.CONVERT.EASY_CHAR_KEY, Constants.DEFAULT_STRING);
                    ConvertHandler.reload();
                    reset();
                }
            }
        });
        settingPanel.add(btnDefault);
        return settingPanel;
    }

    @Override
    public boolean isModified() {
        String oldStr = PropertiesComponent.getInstance().getValue(Constants.Persistence.CONVERT.EASY_CHAR_KEY, Constants.DEFAULT_STRING).trim();
        String newStr = getConfigString().trim();
        return !newStr.equals(oldStr);
    }

    @Override
    public void apply() {
        PropertiesComponent.getInstance().setValue(Constants.Persistence.CONVERT.EASY_CHAR_KEY, getConfigString());
        ConvertHandler.reload();
    }

    @Override
    public void reset() {
        String str = PropertiesComponent.getInstance().getValue(Constants.Persistence.CONVERT.EASY_CHAR_KEY, Constants.DEFAULT_STRING);
        String[] configString = str.split("\n");
        int length = configString.length;
        for (int i = 0; i < Constants.TOTAL_LENGTH; i++) {
            text1[i].setText((2 * i) < length ? configString[2 * i].trim() : "");
            text2[i].setText((2 * i + 1) < length ? configString[2 * i + 1].trim() : "");
        }
    }

    /***
     * 获取配置字符串
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/4/24 14:55
     **/
    private String getConfigString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Constants.TOTAL_LENGTH; i++) {
            sb.append(text1[i].getText().trim())
                    .append("\n")
                    .append(text2[i].getText().trim())
                    .append("\n");
        }
        return sb.toString().trim();
    }

}
