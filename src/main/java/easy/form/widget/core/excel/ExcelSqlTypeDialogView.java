package easy.form.widget.core.excel;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

public class ExcelSqlTypeDialogView extends DialogWrapper {
    private JPanel panel;
    private JRadioButton stringRadioButton;
    private JRadioButton integerRadioButton;
    private JCheckBox splitCheckBox;
    private JSpinner splitSpinner;
    private TextFieldWithBrowseButton splitFilePathButton;

    public static final String STRING = "String";
    public static final String INTEGER = "Integer";

    public ExcelSqlTypeDialogView() {
        super(ProjectManagerEx.getInstanceEx().getDefaultProject());
        setTitle("Sql In Type View");
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        splitSpinner.setModel(new SpinnerNumberModel(10000, 1, 100000, 1000));
        splitFilePathButton.addBrowseFolderListener(new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("请选择导出拆分文件夹");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String currentFolder = splitFilePathButton.getText();
                if (StringUtils.isNotBlank(currentFolder)) {
                    fileChooser.setCurrentDirectory(new File(currentFolder));
                }
                fileChooser.showOpenDialog(panel);
                File selectedFile = fileChooser.getSelectedFile();
                splitFilePathButton.setText(Objects.isNull(selectedFile) ? StringUtils.EMPTY : selectedFile.getAbsolutePath());
            }
        });
        return panel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    @Override
    protected void doOKAction() {
        getSqlTypeConfig();
        super.doOKAction();
    }

    /**
     * 获取SQL类型配置
     *
     * @return {@link easy.form.widget.core.excel.ExcelSqlTypeDialogView.SqlTypeConfig}
     * @author mabin
     * @date 2024/08/05 18:03
     */
    public SqlTypeConfig getSqlTypeConfig() {
        SqlTypeConfig config = new SqlTypeConfig();
        config.setSqlInType(stringRadioButton.isSelected() ? STRING : INTEGER);
        config.setSplitEnable(splitCheckBox.isSelected());
        config.setSplitSize((Integer) splitSpinner.getValue());
        config.setSplitFilePath(splitFilePathButton.getText());
        return config;
    }

    /**
     * SQL类型配置
     *
     * @author mabin
     * @project EasyTool
     * @package easy.form.widget.core.excel.ExcelSqlTypeDialogView
     * @date 2024/08/05 17:58
     */
    public static class SqlTypeConfig {
        private String sqlInType;
        private Boolean splitEnable;
        private Integer splitSize;
        private String splitFilePath;

        public String getSqlInType() {
            return sqlInType;
        }

        public void setSqlInType(String sqlInType) {
            this.sqlInType = sqlInType;
        }

        public Boolean getSplitEnable() {
            return splitEnable;
        }

        public void setSplitEnable(Boolean splitEnable) {
            this.splitEnable = splitEnable;
        }

        public Integer getSplitSize() {
            return splitSize;
        }

        public void setSplitSize(Integer splitSize) {
            this.splitSize = splitSize;
        }

        public String getSplitFilePath() {
            return splitFilePath;
        }

        public void setSplitFilePath(String splitFilePath) {
            this.splitFilePath = splitFilePath;
        }
    }

}
