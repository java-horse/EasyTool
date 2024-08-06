package easy.form.widget.core.excel;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ExcelSqlInTypeDialogView extends DialogWrapper {

    private JPanel panel;
    private JRadioButton stringRadioButton;
    private JRadioButton integerRadioButton;
    private JBCheckBox splitCheckBox;
    private ButtonGroup buttonGroup;

    public static final String STRING = "String";
    public static final String INTEGER = "Integer";

    public ExcelSqlInTypeDialogView() {
        super(ProjectManagerEx.getInstanceEx().getDefaultProject());
        setTitle("Sql In Type View");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        panel = new JPanel(new BorderLayout());
        stringRadioButton = new JRadioButton(STRING, true);
        integerRadioButton = new JRadioButton(INTEGER);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(stringRadioButton);
        buttonGroup.add(integerRadioButton);
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(stringRadioButton);
        radioPanel.add(integerRadioButton);
        panel.add(radioPanel, BorderLayout.CENTER);

        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 250), Math.max(size.height, 100));
        return panel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    /**
     * 获取sql类型
     *
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/07/12 10:05
     */
    public String getSqlInType() {
        if (stringRadioButton.isSelected()) {
            return STRING;
        } else if (integerRadioButton.isSelected()) {
            return INTEGER;
        }
        return null;
    }

}
