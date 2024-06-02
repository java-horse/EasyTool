package easy.form.api;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import easy.api.sdk.yapi.model.ImportDataRequest;
import easy.enums.YApiImportDataSyncTypeEnum;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class YApiImportView extends DialogWrapper {
    private JPanel panel;
    private JLabel importTypeLabel;
    private JComboBox ImportTypeComboBox;
    private JLabel syncTypeLabel;
    private JComboBox syncTypeComboBox;
    private JTextArea dataSourcePasteTextArea;
    private JScrollPane dataSourcePasteScrollPane;

    private ImportDataRequest importDataRequest;

    public YApiImportView() {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("YApi Import View");
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 700), Math.max(size.height, 400));
        return panel;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String pasteJsonText = dataSourcePasteTextArea.getText();
        if (StringUtils.isBlank(pasteJsonText) || !JsonUtil.isJson(pasteJsonText)) {
            return new ValidationInfo("Please enter data in JSON format", dataSourcePasteTextArea);
        }
        return super.doValidate();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        this.importDataRequest = new ImportDataRequest();
        importDataRequest.setJson(dataSourcePasteTextArea.getText());
        importDataRequest.setMerge(YApiImportDataSyncTypeEnum.getCode(String.valueOf(syncTypeComboBox.getSelectedItem())));
        importDataRequest.setType(String.valueOf(ImportTypeComboBox.getSelectedItem()));
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    /**
     * 获取导入API请求数据
     *
     * @return {@link easy.api.sdk.yapi.model.ImportDataRequest}
     * @author mabin
     * @date 2024/06/02 15:16
     */
    public ImportDataRequest getImportDataRequest() {
        return importDataRequest;
    }

}
