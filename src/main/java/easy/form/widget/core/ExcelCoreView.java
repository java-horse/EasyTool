package easy.form.widget.core;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.*;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import easy.util.JsonUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExcelCoreView extends CoreCommonView {
    private JPanel panel;
    private JButton generateButton;
    private JButton copyButton;
    private JLabel selectFileTipLabel;
    private JTextArea resultTextArea;
    private TextFieldWithBrowseButton selectFileTextField;
    private JComboBox encodeComboBox;
    private JLabel summaryTextLabel;

    /**
     * 编码格式映射
     */
    private static final Map<String, Charset> ENCODE_MAP = new HashMap<>(16) {{
        put("ISO-8859-1", CharsetUtil.CHARSET_ISO_8859_1);
        put("UTF-8", CharsetUtil.CHARSET_UTF_8);
        put("GBK", CharsetUtil.CHARSET_GBK);
    }};


    public ExcelCoreView() {
        EasyCommonUtil.customLabelTipText(selectFileTipLabel, "请上传Excel或CSV文件");
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                .withFileFilter(file -> StrUtil.endWithAnyIgnoreCase(file.getName(), ".xls", ".xlsx", ".csv"));
        selectFileTextField.addBrowseFolderListener(new TextBrowseFolderListener(fileChooserDescriptor));
        generateButton.setText(BundleUtil.getI18n("global.button.generate.text"));
        copyButton.setText(BundleUtil.getI18n("global.button.copy.text"));
        summaryTextLabel.setForeground(JBColor.GREEN);
        areaListener(resultTextArea, copyButton);
        generateButton.addActionListener(e -> {
            String filePath = selectFileTextField.getText();
            if (StringUtils.isBlank(filePath) || !FileUtil.exist(filePath)) {
                return;
            }
            File file = FileUtil.file(filePath);
            StopWatch watch = new StopWatch();
            watch.start();
            if (StrUtil.endWithAnyIgnoreCase(file.getName(), ".xls", ".xlsx")) {
                List<Map<String, Object>> rowMapList = ExcelUtil.getReader(file).readAll();
                if (CollectionUtils.isEmpty(rowMapList)) {
                    return;
                }
                for (Map<String, Object> rowMap : rowMapList) {
                    rowMap.remove(StringUtils.EMPTY);
                }
                watch.stop();
                summaryTextLabel.setText(String.format("Summary: 共【%s】行, 耗时【%s】毫秒", rowMapList.size(), watch.getTotalTimeMillis()));
                resultTextArea.setText(JsonUtil.toPrettyJson(rowMapList));
            } else if (StrUtil.endWithIgnoreCase(file.getName(), ".csv")) {
                CsvReadConfig config = new CsvReadConfig();
                config.setHeaderLineNo(0);
                CsvReader reader = CsvUtil.getReader(config);
                CsvData csvData = reader.read(file, ENCODE_MAP.getOrDefault(String.valueOf(encodeComboBox.getSelectedItem()), CharsetUtil.CHARSET_UTF_8));
                if (Objects.isNull(csvData) || csvData.getRowCount() <= Constants.NUM.ZERO) {
                    return;
                }
                List<Map<String, String>> dataList = csvData.getRows().stream()
                        .skip(Constants.NUM.ONE)
                        .map(CsvRow::getFieldMap).toList();
                watch.stop();
                summaryTextLabel.setText(String.format("Summary: 共【%s】行, 耗时【%s】毫秒", dataList.size(), watch.getTotalTimeMillis()));
                resultTextArea.setText(JsonUtil.toPrettyJson(dataList));
            }
        });
        copyButton.addActionListener(e -> {
            if (StringUtils.isBlank(resultTextArea.getText())) {
                return;
            }
            CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(resultTextArea.getText()));
        });
    }

    public JPanel getContent() {
        return this.panel;
    }

}
