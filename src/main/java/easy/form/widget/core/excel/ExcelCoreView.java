package easy.form.widget.core.excel;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.*;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import easy.base.Constants;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import easy.util.JsonUtil;
import easy.util.MessageUtil;
import easy.widget.core.CoreCommonView;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelCoreView extends CoreCommonView {
    private JPanel panel;
    private JButton jsonButton;
    private JButton copyButton;
    private JLabel selectFileTipLabel;
    private JTextArea resultTextArea;
    private TextFieldWithBrowseButton selectFileTextField;
    private JComboBox encodeComboBox;
    private JLabel summaryTextLabel;
    private JButton sqlButton;

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
        summaryTextLabel.setForeground(JBColor.GREEN);
        areaListener(resultTextArea, copyButton);
        jsonButton.addActionListener(e -> {
            if (StringUtils.isNotBlank(resultTextArea.getText())) {
                int confirm = Messages.showOkCancelDialog("是否重新解析?", Constants.PLUGIN_NAME,
                        BundleUtil.getI18n("global.button.confirm.text"), BundleUtil.getI18n("global.button.cancel.text"), Messages.getQuestionIcon());
                if (confirm == MessageConstants.CANCEL) {
                    return;
                }
            }
            StopWatch watch = new StopWatch();
            watch.start();
            List<Map<String, String>> dataList = parseFile(selectFileTextField.getText());
            watch.stop();
            summaryTextLabel.setText(String.format("Summary: 共【%s】行, 耗时【%s】毫秒", dataList.size(), watch.getTotalTimeMillis()));
            resultTextArea.setText(JsonUtil.toPrettyJson(dataList));
        });
        sqlButton.addActionListener(e -> {
            StopWatch watch = new StopWatch();
            watch.start();
            List<Map<String, String>> dataList = parseFile(selectFileTextField.getText());
            ExcelSqlInTypeDialogView excelSqlInTypeDialogView = new ExcelSqlInTypeDialogView();
            if (excelSqlInTypeDialogView.showAndGet()) {
                String sqlJoin = StringUtils.EMPTY;
                List<String> filterList = dataList.stream().map(Map::values).flatMap(Collection::stream)
                        .filter(StringUtils::isNotBlank)
                        .distinct().toList();
                if (StringUtils.equals(excelSqlInTypeDialogView.getSqlInType(), ExcelSqlInTypeDialogView.STRING)) {
                    sqlJoin = filterList.stream().map(s -> "'" + s + "'").collect(Collectors.joining(StrUtil.COMMA + StrUtil.LF));
                } else if (StringUtils.equals(excelSqlInTypeDialogView.getSqlInType(), ExcelSqlInTypeDialogView.INTEGER)) {
                    sqlJoin = String.join(StrUtil.COMMA + StrUtil.LF, filterList);
                }
                watch.stop();
                resultTextArea.setText(sqlJoin);
                summaryTextLabel.setText(String.format("Summary: 共【%s】行, 过滤去重【%s】行, 剩余【%s】行, 耗时【%s】毫秒", dataList.size(),
                        dataList.size() - filterList.size(), filterList.size(), watch.getTotalTimeMillis()));
            }
        });
        copyButton.addActionListener(e -> {
            if (StringUtils.isBlank(resultTextArea.getText())) {
                return;
            }
            CopyPasteManagerEx.getInstanceEx().setContents(new StringSelection(resultTextArea.getText()));
        });
    }

    private List<Map<String, String>> parseFile(String filePath) {
        if (StringUtils.isBlank(filePath) || !FileUtil.exist(filePath)) {
            MessageUtil.showInfoMessage("请上传合法Excel或CSV文件!");
            return Collections.emptyList();
        }
        File file = FileUtil.file(filePath);
        if (StrUtil.endWithAnyIgnoreCase(file.getName(), ".xls", ".xlsx")) {
            return parseExcel(file);
        } else if (StrUtil.endWithIgnoreCase(file.getName(), ".csv")) {
            return parseCsv(file);
        }
        return Collections.emptyList();
    }

    private List<Map<String, String>> parseExcel(File file) {
        List<Map<String, Object>> rowMapList = ExcelUtil.getReader(file).readAll();
        if (CollectionUtils.isEmpty(rowMapList)) {
            return Collections.emptyList();
        }
        List<Map<String, String>> dataList = new ArrayList<>(rowMapList.size());
        for (Map<String, Object> rowMap : rowMapList) {
            Map<String, String> map = new HashMap<>(rowMap.size());
            for (Map.Entry<String, Object> entry : rowMap.entrySet()) {
                if (StringUtils.isBlank(entry.getKey())) {
                    rowMap.remove(entry.getKey());
                    continue;
                }
                map.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
            dataList.add(map);
        }
        return dataList;
    }

    private List<Map<String, String>> parseCsv(File file) {
        CsvReadConfig config = new CsvReadConfig();
        config.setHeaderLineNo(0);
        CsvReader reader = CsvUtil.getReader(config);
        CsvData csvData = reader.read(file, ENCODE_MAP.getOrDefault(String.valueOf(encodeComboBox.getSelectedItem()), CharsetUtil.CHARSET_UTF_8));
        if (Objects.isNull(csvData) || csvData.getRowCount() <= Constants.NUM.ZERO) {
            return Collections.emptyList();
        }
        List<Map<String, String>> dataList = csvData.getRows().stream()
                .skip(Constants.NUM.ONE)
                .map(CsvRow::getFieldMap).collect(Collectors.toList());
        for (Map<String, String> rowMap : dataList) {
            rowMap.remove(StringUtils.EMPTY);
        }
        return dataList;
    }

    public JPanel getContent() {
        return this.panel;
    }

}
