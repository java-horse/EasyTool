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
import org.jetbrains.annotations.Nullable;

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
    private JButton remainderButton;

    private final static List<String> NUMBER = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private final static List<String> OPERATOR = List.of("+", "-", "*", "/", "%", "(", ")", ".");
    private final static List<String> MATH_OPERATOR = List.of("+", "-", "*", "/", "%");

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
        remainderButton.addActionListener(clickListener);

        historyButton.setIcon(AllIcons.Actions.SearchWithHistory);
        historyButton.addActionListener(e -> new CalculatorHistoryDialogView().show());
    }

    /**
     * 按钮单击监听器
     *
     * @author mabin
     * @project EasyTool
     * @package easy.form.widget.core.clac.CalculatorCoreView
     * @date 2024/07/20 09:33
     */
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
                    break;
                case ")":
                    if (StringUtils.endsWithAny(expressionTextFieldText, OPERATOR.toArray(new String[]{}))
                            || !StringUtils.containsAny(expressionTextFieldText, "(")) {
                        MessageUtil.showErrorDialog("Unreasonable expression!");
                        break;
                    }
                    expressionTextField.setText(expressionTextFieldText + buttonText);
                    break;
                case ".":
                    if (!StringUtils.endsWithAny(expressionTextFieldText, NUMBER.toArray(new String[]{}))) {
                        MessageUtil.showErrorDialog("Unreasonable expression!");
                        break;
                    }
                    expressionTextField.setText(expressionTextFieldText + buttonText);
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
                case "+":
                case "-":
                case "*":
                case "/":
                case "%":
                    expressionTextField.setText(expressionTextFieldText + buttonText);
                    break;
                case "CE":
                    expressionTextField.setText(StringUtils.EMPTY);
                    resultTextArea.setText(StringUtils.EMPTY);
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
                        String trimExpress = StringUtils.trim(expressionTextFieldText);
                        String calculateResult = new BigDecimal(Double.toString(NumberUtil.calculate(trimExpress))).stripTrailingZeros().toPlainString();
                        resultTextArea.setText(calculateResult);
                        if (Objects.nonNull(widgetConfig)) {
                            widgetConfig.getCalculatorHistoryMap().put(DateUtil.formatDateTime(new Date()) + StrUtil.UNDERLINE + trimExpress, calculateResult);
                        }
                    } catch (Exception ex) {
                        MessageUtil.showErrorDialog(String.format("Calculate exception: %s", ex.getMessage()));
                        return;
                    }
                    break;
                case "+/-":
                    String firstOperator = getExpressOperator(expressionTextFieldText, Boolean.FALSE);
                    if (StringUtils.isBlank(firstOperator)) {
                        if (StringUtils.startsWith(expressionTextFieldText, "-")) {
                            expressionTextField.setText(expressionTextFieldText.substring(1));
                        } else {
                            expressionTextField.setText("-" + expressionTextFieldText);
                        }
                        return;
                    } else {
                        String firstPre = StrUtil.subBefore(expressionTextFieldText, firstOperator, true);
                        if (StringUtils.startsWith(expressionTextFieldText, "-")) {
                            firstPre = StrUtil.subBefore(expressionTextFieldText.substring(1), firstOperator, true);
                        }
                        String firstLast = StrUtil.subAfter(expressionTextFieldText, firstOperator, true);
                        if (StringUtils.isBlank(firstLast) && !StrUtil.containsAny(firstPre, MATH_OPERATOR.toArray(new String[]{}))) {
                            if (StringUtils.startsWith(expressionTextFieldText, "-")) {
                                expressionTextField.setText(expressionTextFieldText.substring(1));
                            } else {
                                expressionTextField.setText("-" + expressionTextFieldText);
                            }
                            return;
                        } else {
                            String lastOperator = getExpressOperator(expressionTextFieldText, Boolean.TRUE);
                            String pre = StrUtil.subBefore(expressionTextFieldText, lastOperator, true);
                            String last = StrUtil.subAfter(expressionTextFieldText, lastOperator, true);
                            if (StringUtils.isBlank(last)) {
                                MessageUtil.showErrorDialog("Unreasonable expression!");
                                return;
                            }
                            if (StringUtils.equalsAny(lastOperator, "+", "*", "/", "%")) {
                                expressionTextField.setText(pre + lastOperator + "-" + last);
                                return;
                            }
                            if (StringUtils.equals(lastOperator, "-")) {
                                if (StringUtils.endsWithAny(pre, MATH_OPERATOR.toArray(new String[]{}))) {
                                    expressionTextField.setText(pre + last);
                                } else {
                                    expressionTextField.setText(pre + lastOperator + "-" + last);
                                }
                                return;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取算术表达式的操作符
     *
     * @param express   快递
     * @param isReverse 是反向的
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/07/17 15:46
     */
    private String getExpressOperator(String express, @Nullable Boolean isReverse) {
        if (StringUtils.isBlank(express)) {
            return null;
        }
        if (StringUtils.startsWithAny(express, MATH_OPERATOR.toArray(new String[]{}))) {
            express = express.substring(1);
        }
        if (StringUtils.endsWithAny(express, MATH_OPERATOR.toArray(new String[]{}))) {
            express = express.substring(0, express.length() - 1);
        }
        if (Boolean.TRUE.equals(isReverse)) {
            express = StrUtil.reverse(express);
        }
        for (char c : express.toCharArray()) {
            String charStr = String.valueOf(c);
            if (MATH_OPERATOR.contains(charStr)) {
                return charStr;
            }
        }
        return null;
    }


    public JPanel getContent() {
        return panel;
    }

}
