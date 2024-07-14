package easy.form.widget.core.clac;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.intellij.icons.AllIcons;
import easy.config.widget.WidgetConfig;
import easy.config.widget.WidgetConfigComponent;
import easy.helper.ServiceHelper;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 简易计算器核心视图
 *
 * @author mabin
 * @project EasyTool
 * @package easy.form.widget.core.clac
 * @date 2024/07/13 14:20
 */
public class CalculatorCoreView extends CoreCommonView {

    private final WidgetConfig widgetConfig = ServiceHelper.getService(WidgetConfigComponent.class).getState();

    private JPanel panel;
    private JButton a0Button;
    private JButton a1Button;
    private JButton a2Button;
    private JButton a3Button;
    private JButton a4Button;
    private JButton a5Button;
    private JButton a6Button;
    private JButton a7Button;
    private JButton a8Button;
    private JButton a9Button;
    private JButton subButton;
    private JButton addButton;
    private JButton multiButton;
    private JButton divideButton;
    private JButton negateButton;
    private JButton equalButton;
    private JButton dotButton;
    private JButton rollbackButton;
    private JButton clearButton;
    private JFormattedTextField expressionTextField;
    private JTextArea resultTextArea;
    private JButton historyButton;
    private JButton bracketLeftButton;
    private JButton bracketRightButton;

    private String operator = StringUtils.EMPTY;
    private String previous = StringUtils.EMPTY;
    private final static List<String> NUMBER = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private final static List<String> OPERATOR = List.of("+", "-", "*", "/", "(", ")", ".");

    public CalculatorCoreView() {
        ButtonClickListener clickListener = new ButtonClickListener();
        a0Button.addActionListener(clickListener);
        a1Button.addActionListener(clickListener);
        a2Button.addActionListener(clickListener);
        a3Button.addActionListener(clickListener);
        a4Button.addActionListener(clickListener);
        a5Button.addActionListener(clickListener);
        a6Button.addActionListener(clickListener);
        a7Button.addActionListener(clickListener);
        a8Button.addActionListener(clickListener);
        a9Button.addActionListener(clickListener);
        addButton.addActionListener(clickListener);
        subButton.addActionListener(clickListener);
        multiButton.addActionListener(clickListener);
        divideButton.addActionListener(clickListener);
        dotButton.addActionListener(clickListener);
        equalButton.addActionListener(clickListener);
        rollbackButton.addActionListener(clickListener);
        clearButton.addActionListener(clickListener);
        negateButton.addActionListener(clickListener);
        bracketLeftButton.addActionListener(clickListener);
        bracketRightButton.addActionListener(clickListener);

        historyButton.setIcon(AllIcons.Actions.SearchWithHistory);
        historyButton.addActionListener(e -> new CalculatorHistoryDialogView().show());
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String buttonText = StringUtils.trim(((JButton) e.getSource()).getText());
            String expressionTextFieldText = expressionTextField.getText();
            switch (buttonText) {
                case "(":
                    if (StringUtils.endsWithAny(expressionTextFieldText, ")", ".")
                            || StringUtils.endsWithAny(expressionTextFieldText, NUMBER.toArray(new String[]{}))) {
                        MessageUtil.showErrorDialog("Unreasonable expression!");
                        break;
                    }
                    expressionTextField.setText(expressionTextFieldText + buttonText);
                    previous = buttonText;
                    break;
                case ")":
                    if (StringUtils.endsWithAny(expressionTextFieldText, OPERATOR.toArray(new String[]{}))
                            || !StringUtils.containsAny(expressionTextFieldText, "(")) {
                        MessageUtil.showErrorDialog("Unreasonable expression!");
                        break;
                    }
                    expressionTextField.setText(expressionTextFieldText + buttonText);
                    previous = buttonText;
                    break;
                case "0":
                case "1":
                case "2":
                case "3":
                case "4":
                case "5":
                case "6":
                case "7":
                case "8":
                case "9":
                case ".":
                    expressionTextField.setText(expressionTextFieldText + buttonText);
                    previous = buttonText;
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                    expressionTextField.setText(expressionTextFieldText + buttonText);
                    operator = buttonText;
                    previous = buttonText;
                    break;
                case "C":
                    expressionTextField.setText(StringUtils.EMPTY);
                    resultTextArea.setText(StringUtils.EMPTY);
                    operator = StringUtils.EMPTY;
                    previous = StringUtils.EMPTY;
                    break;
                case "R":
                    if (StringUtils.isNotBlank(expressionTextFieldText)) {
                        expressionTextField.setText(expressionTextFieldText.substring(0, expressionTextFieldText.length() - 1));
                    }
                    break;
                case "=":
                    if (StringUtils.isBlank(expressionTextFieldText)) {
                        MessageUtil.showErrorDialog("Please enter an arithmetic expression");
                        return;
                    }
                    try {
                        String calculateResult = new BigDecimal(Double.toString(NumberUtil.calculate(expressionTextFieldText)))
                                .stripTrailingZeros().toPlainString();
                        resultTextArea.setText(calculateResult);
                        if (Objects.nonNull(widgetConfig)) {
                            widgetConfig.getCalculatorHistoryMap().put(DateUtil.formatDateTime(new Date()) + StrUtil.UNDERLINE + expressionTextFieldText, calculateResult);
                        }
                    } catch (Exception ex) {
                        MessageUtil.showErrorDialog(String.format("Calculate exception: %s", ex.getMessage()));
                        return;
                    }
                    break;
                case "+/-":
                    if (StringUtils.isBlank(previous) || OPERATOR.contains(previous)) {
                        MessageUtil.showErrorDialog("Unreasonable expression!");
                        return;
                    }
                    if (NUMBER.contains(previous)) {
                        if (StringUtils.isBlank(operator)) {
                            if (StringUtils.startsWith(expressionTextFieldText, "-")) {
                                expressionTextField.setText(expressionTextFieldText.substring(1));
                            } else {
                                expressionTextField.setText("-" + expressionTextFieldText);
                            }
                            return;
                        }
                        String preNumber = StrUtil.subBefore(expressionTextFieldText, operator, true);
                        String lastNumber = StrUtil.subAfter(expressionTextFieldText, operator, true);
                        if (StringUtils.startsWith(lastNumber, "-")) {
                            expressionTextField.setText(preNumber + operator + lastNumber.substring(1));
                            return;
                        }
                        expressionTextField.setText(preNumber + operator + "-" + lastNumber);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public JPanel getContent() {
        return panel;
    }

}
