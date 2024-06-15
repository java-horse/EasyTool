package easy.form;

import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import easy.base.Constants;
import easy.config.translate.TranslateConfig;
import easy.config.translate.TranslateConfigComponent;
import easy.enums.OpenModelTranslateEnum;
import easy.enums.TranslateEnum;
import easy.enums.TranslateLanguageEnum;
import easy.translate.TranslateService;
import easy.util.BundleUtil;
import easy.util.EasyCommonUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * @author mabin
 * @project EasyChar
 * @date 2023/09/02 11:45
 **/

public class TranslateSettingView {

    private TranslateConfig translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();
    private TranslateService translateService = ApplicationManager.getApplication().getService(TranslateService.class);

    // 全局单词映射设置
    private JBList<Entry<String, String>> globalWordMapList;
    private JPanel globalWordMappingPanel;

    private JPanel panel;
    private JPanel translatePanel;
    private JLabel translateChannelLabel;
    private JComboBox translateChannelBox;
    private JLabel translateChannelTipLabel;
    private JLabel appIdLabel;
    private JTextField appIdTextField;
    private JLabel appSecretLabel;
    private JPasswordField appSecretTextField;
    private JLabel secretIdLabel;
    private JTextField secretIdTextField;
    private JLabel secretKeyLabel;
    private JPasswordField secretKeyTextField;
    private JLabel accessKeyIdLabel;
    private JTextField accessKeyIdTextField;
    private JLabel accessKeySecretLabel;
    private JPasswordField accessKeySecretTextField;
    private JPanel supportPanel;
    private JButton clearButton;
    private JButton reviewButton;
    private JButton payButton;
    private JButton startButton;
    private JButton resetButton;
    private JLabel tencentSecretIdLabel;
    private JLabel tencentSecretKeyLabel;
    private JTextField tencentSecretIdTextField;
    private JPasswordField tencentSecretKeyTextField;
    private JLabel volcanoSecretIdLabel;
    private JLabel volcanoSecretKeyLabel;
    private JTextField volcanoSecretIdTextField;
    private JPasswordField volcanoSecretKeyTextField;
    private JLabel xfAppIdLabel;
    private JTextField xfAppIdTextField;
    private JLabel xfApiSecretLabel;
    private JPasswordField xfApiSecretTextField;
    private JLabel xfApiKeyLabel;
    private JTextField xfApiKeyTextField;
    private JLabel googleSecretKeyLabel;
    private JPasswordField googleSecretKeyTextField;
    private JLabel microsoftKeyLabel;
    private JPasswordField microsoftKeyTextField;
    private JLabel niuApiKeyLabel;
    private JPasswordField niuApiKeyTextField;
    private JLabel caiyunTokenLabel;
    private JPasswordField caiyunTokenTextField;
    private JLabel hwProjectIdLabel;
    private JTextField hwProjectIdTextField;
    private JLabel hwAppIdLabel;
    private JTextField hwAppIdTextField;
    private JLabel hwAppSecretLabel;
    private JPasswordField hwAppSecretTextField;
    private JTextField thsAppIdTextField;
    private JLabel thsAppIdLabel;
    private JPasswordField thsAppSecretTextField;
    private JLabel thsAppSecretLabel;
    private JLabel openModelLabel;
    private JComboBox openModelComboBox;
    private JLabel tyKeyLabel;
    private JPasswordField tyKeyTextField;
    private JLabel tyModelLabel;
    private JComboBox tyModelComboBox;
    private JLabel baiduDomainLabel;
    private JCheckBox baiduDomainCheckBox;
    private JComboBox baiduDomainComboBox;
    private JLabel aliyunDomainLabel;
    private JCheckBox aliyunDomainCheckBox;
    private JComboBox aliyunDomainComboBox;
    private JLabel youdaoDomainLabel;
    private JCheckBox youdaoDomainCheckBox;
    private JComboBox youdaoDomainComboBox;
    private JLabel kimiModelLabel;
    private JLabel kimiKeyLabel;
    private JComboBox kimiModelComboBox;
    private JPasswordField kimiKeyPasswordField;
    private JLabel wenxinModelLabel;
    private JComboBox wenxinModelComboBox;
    private JLabel wenxinApiKeyLabel;
    private JTextField wenxinApiKeyTextField;
    private JLabel wenxinApiSecretLabel;
    private JPasswordField wenxinApiSecretPasswordField;
    private JTextField customApiUrlTextField;
    private JLabel customApiUrlLabel;
    private JLabel customApiMaxCharLengthLabel;
    private JTextField customApiMaxCharLengthTextField;
    private JLabel customApiMaxCharLengthTipLabel;
    private JLabel customSupportLanguageLabel;
    private JTextField customSupportLanguageTextField;
    private JLabel customSupportLanguageTipLabel;
    private JLabel customApiExampleLabel;
    private JTextArea customApiExampleTextArea;
    private JLabel libreServerUrlLabel;
    private JComboBox libreServerUrlComboBox;
    private JLabel previewSecretLabel;


    private String secretPlainText;

    /**
     * 在{@link #createUIComponents()}之后调用
     */
    public TranslateSettingView() {
        setTranslateVisible(translateChannelBox.getSelectedItem());
        refreshGlobalWordMap();
        // 设置监听器
        resetButton.addActionListener(event -> {
            int result = Messages.showYesNoDialog("确认删除所有配置数据?", "重置数据", Messages.getWarningIcon());
            if (result == MessageConstants.YES) {
                translateConfig.reset();
                refresh();
            }
        });
        clearButton.addActionListener(event -> {
            int result = Messages.showYesNoDialog("确认清空所有翻译缓存?", "清空缓存", Messages.getWarningIcon());
            if (result == MessageConstants.YES) {
                translateService.clearCache();
            }
        });
        startButton.addActionListener(event -> EasyCommonUtil.confirmOpenLink(Constants.GITEE_URL));
        reviewButton.addActionListener(event -> EasyCommonUtil.confirmOpenLink(Constants.JETBRAINS_URL));
        payButton.addActionListener(event -> new SupportView().show());

        openModelComboBox.addItemListener(e -> setOpenModelVisible(((JComboBox<?>) e.getSource()).getSelectedItem()));
        translateChannelBox.addItemListener(e -> {
            Object selectedItem = ((JComboBox<?>) e.getSource()).getSelectedItem();
            setTranslateVisible(selectedItem);
            translateChannelTipLabel.setToolTipText(TranslateEnum.getTips(String.valueOf(selectedItem)));
        });
        baiduDomainComboBox.setEnabled(false);
        baiduDomainCheckBox.addItemListener(e -> baiduDomainComboBox.setEnabled(e.getStateChange() == ItemEvent.SELECTED));
        aliyunDomainComboBox.setEnabled(false);
        aliyunDomainCheckBox.addItemListener(e -> aliyunDomainComboBox.setEnabled(e.getStateChange() == ItemEvent.SELECTED));
        youdaoDomainComboBox.setEnabled(false);
        youdaoDomainCheckBox.addItemListener(e -> youdaoDomainComboBox.setEnabled(e.getStateChange() == ItemEvent.SELECTED));
        previewSecretLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String model = String.valueOf(translateChannelBox.getSelectedItem());
                if (Objects.equals(translateChannelBox.getSelectedItem(), TranslateEnum.OPEN_BIG_MODEL.getTranslate())) {
                    model = String.valueOf(openModelComboBox.getSelectedItem());
                }
                if (StringUtils.isNoneBlank(secretPlainText, model)) {
                    if (new OpenModelPreviewKeyView(model, secretPlainText).showAndGet()) {
                        CopyPasteManager.getInstance().setContents(new StringSelection(secretPlainText));
                    }
                }
            }
        });

        // 设置温馨提示
        EasyCommonUtil.customLabelTipText(translateChannelTipLabel, TranslateEnum.getTips(String.valueOf(translateChannelBox.getSelectedItem())));
        EasyCommonUtil.customLabelTipText(customApiMaxCharLengthTipLabel, "每次请求最大字符数, 太大会导致接口响应变慢, 可以尝试调整该选项来优化速度!");
        EasyCommonUtil.customLabelTipText(customSupportLanguageTipLabel, String.format("语言代码默目前只支持: %s,%s", TranslateLanguageEnum.EN.lang, TranslateLanguageEnum.ZH_CN.lang));
    }

    /**
     * 设置翻译开源模型
     *
     * @param selectedItem
     * @return void
     * @author mabin
     * @date 2023/11/28 11:47
     */
    private void setOpenModelVisible(Object selectedItem) {
        if (Objects.equals(OpenModelTranslateEnum.TONG_YI.getModel(), selectedItem)) {
            secretPlainText = translateConfig.getTyKey();
            tyModelLabel.setVisible(true);
            tyModelComboBox.setVisible(true);
            tyKeyLabel.setVisible(true);
            tyKeyTextField.setVisible(true);
            kimiModelLabel.setVisible(false);
            kimiModelComboBox.setVisible(false);
            kimiKeyLabel.setVisible(false);
            kimiKeyPasswordField.setVisible(false);
            wenxinModelLabel.setVisible(false);
            wenxinModelComboBox.setVisible(false);
            wenxinApiKeyLabel.setVisible(false);
            wenxinApiKeyTextField.setVisible(false);
            wenxinApiSecretLabel.setVisible(false);
            wenxinApiSecretPasswordField.setVisible(false);
        } else if (Objects.equals(OpenModelTranslateEnum.KIMI.getModel(), selectedItem)) {
            secretPlainText = translateConfig.getKimiKey();
            tyModelLabel.setVisible(false);
            tyModelComboBox.setVisible(false);
            tyKeyLabel.setVisible(false);
            tyKeyTextField.setVisible(false);
            kimiModelLabel.setVisible(true);
            kimiModelComboBox.setVisible(true);
            kimiKeyLabel.setVisible(true);
            kimiKeyPasswordField.setVisible(true);
            wenxinModelLabel.setVisible(false);
            wenxinModelComboBox.setVisible(false);
            wenxinApiKeyLabel.setVisible(false);
            wenxinApiKeyTextField.setVisible(false);
            wenxinApiSecretLabel.setVisible(false);
            wenxinApiSecretPasswordField.setVisible(false);
        } else if (Objects.equals(OpenModelTranslateEnum.WEN_XIN.getModel(), selectedItem)) {
            secretPlainText = translateConfig.getWenxinApiSecret();
            tyModelLabel.setVisible(false);
            tyModelComboBox.setVisible(false);
            tyKeyLabel.setVisible(false);
            tyKeyTextField.setVisible(false);
            kimiModelLabel.setVisible(false);
            kimiModelComboBox.setVisible(false);
            kimiKeyLabel.setVisible(false);
            kimiKeyPasswordField.setVisible(false);
            wenxinModelLabel.setVisible(true);
            wenxinModelComboBox.setVisible(true);
            wenxinApiKeyLabel.setVisible(true);
            wenxinApiKeyTextField.setVisible(true);
            wenxinApiSecretLabel.setVisible(true);
            wenxinApiSecretPasswordField.setVisible(true);
        } else {
            secretPlainText = null;
            tyModelLabel.setVisible(false);
            tyModelComboBox.setVisible(false);
            tyKeyLabel.setVisible(false);
            tyKeyTextField.setVisible(false);
            kimiModelLabel.setVisible(false);
            kimiModelComboBox.setVisible(false);
            kimiKeyLabel.setVisible(false);
            kimiKeyPasswordField.setVisible(false);
            wenxinModelLabel.setVisible(false);
            wenxinModelComboBox.setVisible(false);
            wenxinApiKeyLabel.setVisible(false);
            wenxinApiKeyTextField.setVisible(false);
            wenxinApiSecretLabel.setVisible(false);
            wenxinApiSecretPasswordField.setVisible(false);
        }
    }

    /**
     * 设置通用表单隐藏项
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/11/28 17:22
     */
    private void setCommonVisible() {
        tyModelLabel.setVisible(false);
        tyModelComboBox.setVisible(false);
        tyKeyLabel.setVisible(false);
        tyKeyTextField.setVisible(false);
        kimiModelLabel.setVisible(false);
        kimiModelComboBox.setVisible(false);
        kimiKeyLabel.setVisible(false);
        kimiKeyPasswordField.setVisible(false);
        wenxinModelLabel.setVisible(false);
        wenxinModelComboBox.setVisible(false);
        wenxinApiKeyLabel.setVisible(false);
        wenxinApiKeyTextField.setVisible(false);
        wenxinApiSecretLabel.setVisible(false);
        wenxinApiSecretPasswordField.setVisible(false);
    }

    private void createUIComponents() {
        translateConfig = ApplicationManager.getApplication().getService(TranslateConfigComponent.class).getState();
        // 设置全局单词映射页面
        globalWordMapList = new JBList<>(new CollectionListModel<>(Lists.newArrayList()));
        globalWordMapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        globalWordMapList.setCellRenderer(new ListCellRendererWrapper<>() {
            @Override
            public void customize(JList list, Entry<String, String> value, int index, boolean selected, boolean hasFocus) {
                setText(value.getKey() + " -> " + value.getValue());
            }
        });
        globalWordMapList.setEmptyText(BundleUtil.getI18n("global.word.mapping.text"));
        globalWordMapList.setSelectedIndex(Constants.NUM.ZERO);
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(globalWordMapList);
        toolbarDecorator.setAddAction(button -> {
            WordMapAddView wordMapAddView = new WordMapAddView();
            if (wordMapAddView.showAndGet()) {
                Entry<String, String> entry = wordMapAddView.getMapping();
                translateConfig.getGlobalWordMap().put(entry.getKey(), entry.getValue());
                refreshGlobalWordMap();
            }
        });
        toolbarDecorator.disableUpDownActions();
        toolbarDecorator.setRemoveAction(anActionButton -> {
            Map<String, String> typeMap = translateConfig.getGlobalWordMap();
            typeMap.remove(globalWordMapList.getSelectedValue().getKey());
            refreshGlobalWordMap();
        });
        globalWordMappingPanel = toolbarDecorator.createPanel();
    }


    /**
     * 设置翻译渠道
     *
     * @param selectedItem
     * @return void
     * @author mabin
     * @date 2023/9/3 15:29
     **/
    private void setTranslateVisible(Object selectedItem) {
        if (Objects.equals(TranslateEnum.BAIDU.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getAppSecret();
            setCommonVisible();
            appIdLabel.setVisible(true);
            appIdTextField.setVisible(true);
            appSecretLabel.setVisible(true);
            appSecretTextField.setVisible(true);
            baiduDomainLabel.setVisible(true);
            baiduDomainCheckBox.setVisible(true);
            baiduDomainComboBox.setVisible(true);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.ALIYUN.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getAccessKeySecret();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(true);
            accessKeyIdTextField.setVisible(true);
            accessKeySecretLabel.setVisible(true);
            accessKeySecretTextField.setVisible(true);
            aliyunDomainLabel.setVisible(true);
            aliyunDomainCheckBox.setVisible(true);
            aliyunDomainComboBox.setVisible(true);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.YOUDAO.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getSecretKey();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(true);
            secretIdTextField.setVisible(true);
            secretKeyLabel.setVisible(true);
            secretKeyTextField.setVisible(true);
            youdaoDomainLabel.setVisible(true);
            youdaoDomainCheckBox.setVisible(true);
            youdaoDomainComboBox.setVisible(true);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.TENCENT.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getTencentSecretKey();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(true);
            tencentSecretIdTextField.setVisible(true);
            tencentSecretKeyLabel.setVisible(true);
            tencentSecretKeyTextField.setVisible(true);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.VOLCANO.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getVolcanoSecretKey();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(true);
            volcanoSecretKeyLabel.setVisible(true);
            volcanoSecretIdTextField.setVisible(true);
            volcanoSecretKeyTextField.setVisible(true);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.XFYUN.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getXfApiKey();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(true);
            xfAppIdTextField.setVisible(true);
            xfApiSecretLabel.setVisible(true);
            xfApiSecretTextField.setVisible(true);
            xfApiKeyLabel.setVisible(true);
            xfApiKeyTextField.setVisible(true);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.GOOGLE.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getGoogleSecretKey();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(true);
            googleSecretKeyTextField.setVisible(true);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.MICROSOFT.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getMicrosoftKey();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(true);
            microsoftKeyTextField.setVisible(true);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.NIU.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getNiuApiKey();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(true);
            niuApiKeyTextField.setVisible(true);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.CAIYUN.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getCaiyunToken();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(true);
            caiyunTokenTextField.setVisible(true);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.HUAWEI.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getHwAppSecret();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(true);
            hwProjectIdTextField.setVisible(true);
            hwAppIdLabel.setVisible(true);
            hwAppIdTextField.setVisible(true);
            hwAppSecretLabel.setVisible(true);
            hwAppSecretTextField.setVisible(true);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.THS_SOFT.getTranslate(), selectedItem)) {
            secretPlainText = translateConfig.getThsAppSecret();
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(true);
            thsAppIdTextField.setVisible(true);
            thsAppSecretLabel.setVisible(true);
            thsAppSecretTextField.setVisible(true);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.CUSTOM.getTranslate(), selectedItem)) {
            secretPlainText = null;
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(true);
            customApiUrlTextField.setVisible(true);
            customApiMaxCharLengthLabel.setVisible(true);
            customApiMaxCharLengthTextField.setVisible(true);
            customApiMaxCharLengthTipLabel.setVisible(true);
            customSupportLanguageLabel.setVisible(true);
            customSupportLanguageTextField.setVisible(true);
            customSupportLanguageTipLabel.setVisible(true);
            customApiExampleLabel.setVisible(true);
            customApiExampleTextArea.setVisible(true);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        } else if (Objects.equals(TranslateEnum.LIBRE.getTranslate(), selectedItem)) {
            secretPlainText = null;
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(true);
            libreServerUrlComboBox.setVisible(true);
        } else if (Objects.equals(TranslateEnum.OPEN_BIG_MODEL.getTranslate(), selectedItem)) {
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(true);
            openModelComboBox.setVisible(true);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
            // 开元大模型UI设置
            setOpenModelVisible(openModelComboBox.getSelectedItem());
        } else {
            secretPlainText = null;
            setCommonVisible();
            appIdLabel.setVisible(false);
            appIdTextField.setVisible(false);
            appSecretLabel.setVisible(false);
            appSecretTextField.setVisible(false);
            baiduDomainLabel.setVisible(false);
            baiduDomainCheckBox.setVisible(false);
            baiduDomainComboBox.setVisible(false);
            secretIdLabel.setVisible(false);
            secretIdTextField.setVisible(false);
            secretKeyLabel.setVisible(false);
            secretKeyTextField.setVisible(false);
            youdaoDomainLabel.setVisible(false);
            youdaoDomainCheckBox.setVisible(false);
            youdaoDomainComboBox.setVisible(false);
            accessKeyIdLabel.setVisible(false);
            accessKeyIdTextField.setVisible(false);
            accessKeySecretLabel.setVisible(false);
            accessKeySecretTextField.setVisible(false);
            aliyunDomainLabel.setVisible(false);
            aliyunDomainCheckBox.setVisible(false);
            aliyunDomainComboBox.setVisible(false);
            tencentSecretIdLabel.setVisible(false);
            tencentSecretIdTextField.setVisible(false);
            tencentSecretKeyLabel.setVisible(false);
            tencentSecretKeyTextField.setVisible(false);
            volcanoSecretIdLabel.setVisible(false);
            volcanoSecretKeyLabel.setVisible(false);
            volcanoSecretIdTextField.setVisible(false);
            volcanoSecretKeyTextField.setVisible(false);
            xfAppIdLabel.setVisible(false);
            xfAppIdTextField.setVisible(false);
            xfApiSecretLabel.setVisible(false);
            xfApiSecretTextField.setVisible(false);
            xfApiKeyLabel.setVisible(false);
            xfApiKeyTextField.setVisible(false);
            googleSecretKeyLabel.setVisible(false);
            googleSecretKeyTextField.setVisible(false);
            microsoftKeyLabel.setVisible(false);
            microsoftKeyTextField.setVisible(false);
            niuApiKeyLabel.setVisible(false);
            niuApiKeyTextField.setVisible(false);
            caiyunTokenLabel.setVisible(false);
            caiyunTokenTextField.setVisible(false);
            hwProjectIdLabel.setVisible(false);
            hwProjectIdTextField.setVisible(false);
            hwAppIdLabel.setVisible(false);
            hwAppIdTextField.setVisible(false);
            hwAppSecretLabel.setVisible(false);
            hwAppSecretTextField.setVisible(false);
            thsAppIdLabel.setVisible(false);
            thsAppIdTextField.setVisible(false);
            thsAppSecretLabel.setVisible(false);
            thsAppSecretTextField.setVisible(false);
            openModelLabel.setVisible(false);
            openModelComboBox.setVisible(false);
            customApiUrlLabel.setVisible(false);
            customApiUrlTextField.setVisible(false);
            customApiMaxCharLengthLabel.setVisible(false);
            customApiMaxCharLengthTextField.setVisible(false);
            customApiMaxCharLengthTipLabel.setVisible(false);
            customSupportLanguageLabel.setVisible(false);
            customSupportLanguageTextField.setVisible(false);
            customSupportLanguageTipLabel.setVisible(false);
            customApiExampleLabel.setVisible(false);
            customApiExampleTextArea.setVisible(false);
            libreServerUrlLabel.setVisible(false);
            libreServerUrlComboBox.setVisible(false);
        }
    }

    /**
     * 刷新翻译渠道设置
     *
     * @param
     * @return void
     * @author mabin
     * @date 2023/9/3 15:31
     **/
    public void refresh() {
        refreshGlobalWordMap();
        setTranslateChannelBox(translateConfig.getTranslateChannel());
        setAppIdTextField(translateConfig.getAppId());
        setAppSecretTextField(translateConfig.getAppSecret());
        setBaiduDomainCheckBox(translateConfig.getBaiduDomainCheckBox());
        setBaiduDomainComboBox(translateConfig.getBaiduDomainComboBox());
        setSecretIdTextField(translateConfig.getSecretId());
        setSecretKeyTextField(translateConfig.getSecretKey());
        setYoudaoDomainCheckBox(translateConfig.getYoudaoDomainCheckBox());
        setYoudaoDomainComboBox(translateConfig.getYoudaoDomainComboBox());
        setAccessKeyIdTextField(translateConfig.getAccessKeyId());
        setAccessKeySecretTextField(translateConfig.getAccessKeySecret());
        setAliyunDomainCheckBox(translateConfig.getAliyunDomainCheckBox());
        setAliyunDomainComboBox(translateConfig.getAliyunDomainComboBox());
        setTencentSecretIdTextField(translateConfig.getTencentSecretId());
        setTencentSecretKeyTextField(translateConfig.getTencentSecretKey());
        setVolcanoSecretIdTextField(translateConfig.getVolcanoSecretId());
        setVolcanoSecretKeyTextField(translateConfig.getVolcanoSecretKey());
        setXfAppIdTextField(translateConfig.getXfAppId());
        setXfApiKeyTextField(translateConfig.getXfApiKey());
        setXfApiSecretTextField(translateConfig.getXfApiSecret());
        setGoogleSecretKeyTextField(translateConfig.getGoogleSecretKey());
        setMicrosoftKeyTextField(translateConfig.getMicrosoftKey());
        setNiuApiKeyTextField(translateConfig.getNiuApiKey());
        setCaiyunTokenTextField(translateConfig.getCaiyunToken());
        setHwProjectIdTextField(translateConfig.getHwProjectId());
        setHwAppIdTextField(translateConfig.getHwAppId());
        setHwAppSecretTextField(translateConfig.getHwAppSecret());
        setThsAppIdTextField(translateConfig.getThsAppId());
        setThsAppSecretTextField(translateConfig.getThsAppSecret());
        setOpenModelComboBox(translateConfig.getOpenModelChannel());
        setTyModelComboBox(translateConfig.getTyModel());
        setTyKeyTextField(translateConfig.getTyKey());
        setKimiModelComboBox(translateConfig.getKimiModel());
        setKimiKeyPasswordField(translateConfig.getKimiKey());
        setWenxinModelComboBox(translateConfig.getWenxinModel());
        setWenxinApiKeyTextField(translateConfig.getWenxinApiKey());
        setWenxinApiSecretPasswordField(translateConfig.getWenxinApiSecret());
        setCustomApiUrlTextField(translateConfig.getCustomApiUrl());
        setCustomApiMaxCharLengthTextField(Integer.toString(translateConfig.getCustomApiMaxCharLength()));
        setCustomSupportLanguageTextField(translateConfig.getCustomSupportLanguage());
        setLibreServerUrlComboBox(translateConfig.getLibreServerUrl());
    }

    private void refreshGlobalWordMap() {
        if (Objects.nonNull(translateConfig) && MapUtils.isNotEmpty(translateConfig.getGlobalWordMap())) {
            globalWordMapList.setModel(new CollectionListModel<>(Lists.newArrayList(translateConfig.getGlobalWordMap().entrySet())));
        }
    }

    public JComponent getComponent() {
        return panel;
    }

    public JComboBox getTranslateChannelBox() {
        return translateChannelBox;
    }

    public void setTranslateChannelBox(String translateChannel) {
        this.translateChannelBox.setSelectedItem(translateChannel);
    }

    public JTextField getAppIdTextField() {
        return appIdTextField;
    }

    public void setAppIdTextField(String appId) {
        this.appIdTextField.setText(appId);
    }

    public JPasswordField getAppSecretTextField() {
        return appSecretTextField;
    }

    public void setAppSecretTextField(String appSecretTextField) {
        this.appSecretTextField.setText(appSecretTextField);
    }

    public JCheckBox getBaiduDomainCheckBox() {
        return baiduDomainCheckBox;
    }

    public void setBaiduDomainCheckBox(Boolean baiduDomainCheckBox) {
        this.baiduDomainCheckBox.setSelected(baiduDomainCheckBox);
    }

    public JComboBox getBaiduDomainComboBox() {
        return baiduDomainComboBox;
    }

    public void setBaiduDomainComboBox(String baiduDomainComboBox) {
        this.baiduDomainComboBox.setSelectedItem(baiduDomainComboBox);
    }

    public JTextField getSecretIdTextField() {
        return secretIdTextField;
    }

    public void setSecretIdTextField(String secretId) {
        this.secretIdTextField.setText(secretId);
    }

    public JPasswordField getSecretKeyTextField() {
        return secretKeyTextField;
    }

    public void setSecretKeyTextField(String secretKey) {
        this.secretKeyTextField.setText(secretKey);
    }

    public JTextField getAccessKeyIdTextField() {
        return accessKeyIdTextField;
    }

    public void setAccessKeyIdTextField(String accessKeyId) {
        this.accessKeyIdTextField.setText(accessKeyId);
    }


    public JPasswordField getAccessKeySecretTextField() {
        return accessKeySecretTextField;
    }

    public void setAccessKeySecretTextField(String accessKeySecret) {
        this.accessKeySecretTextField.setText(accessKeySecret);
    }

    public JTextField getTencentSecretIdTextField() {
        return tencentSecretIdTextField;
    }

    public void setTencentSecretIdTextField(String tencentSecretIdTextField) {
        this.tencentSecretIdTextField.setText(tencentSecretIdTextField);
    }

    public JPasswordField getTencentSecretKeyTextField() {
        return tencentSecretKeyTextField;
    }

    public void setTencentSecretKeyTextField(String tencentSecretKeyTextField) {
        this.tencentSecretKeyTextField.setText(tencentSecretKeyTextField);
    }

    public JTextField getVolcanoSecretIdTextField() {
        return volcanoSecretIdTextField;
    }

    public void setVolcanoSecretIdTextField(String volcanoSecretIdTextField) {
        this.volcanoSecretIdTextField.setText(volcanoSecretIdTextField);
    }

    public JPasswordField getVolcanoSecretKeyTextField() {
        return volcanoSecretKeyTextField;
    }

    public void setVolcanoSecretKeyTextField(String volcanoSecretKeyTextField) {
        this.volcanoSecretKeyTextField.setText(volcanoSecretKeyTextField);
    }

    public JTextField getXfAppIdTextField() {
        return xfAppIdTextField;
    }

    public void setXfAppIdTextField(String xfAppIdTextField) {
        this.xfAppIdTextField.setText(xfAppIdTextField);
    }

    public JPasswordField getXfApiSecretTextField() {
        return xfApiSecretTextField;
    }

    public void setXfApiSecretTextField(String xfApiSecretTextField) {
        this.xfApiSecretTextField.setText(xfApiSecretTextField);
    }

    public JTextField getXfApiKeyTextField() {
        return xfApiKeyTextField;
    }

    public void setXfApiKeyTextField(String xfApiKeyTextField) {
        this.xfApiKeyTextField.setText(xfApiKeyTextField);
    }

    public JPasswordField getGoogleSecretKeyTextField() {
        return googleSecretKeyTextField;
    }

    public void setGoogleSecretKeyTextField(String googleSecretKeyTextField) {
        this.googleSecretKeyTextField.setText(googleSecretKeyTextField);
    }

    public JLabel getMicrosoftKeyLabel() {
        return microsoftKeyLabel;
    }

    public JPasswordField getMicrosoftKeyTextField() {
        return microsoftKeyTextField;
    }

    public void setMicrosoftKeyTextField(String microsoftKeyTextField) {
        this.microsoftKeyTextField.setText(microsoftKeyTextField);
    }

    public JPasswordField getNiuApiKeyTextField() {
        return niuApiKeyTextField;
    }

    public void setNiuApiKeyTextField(String niuApiKeyTextField) {
        this.niuApiKeyTextField.setText(niuApiKeyTextField);
    }

    public JPasswordField getCaiyunTokenTextField() {
        return caiyunTokenTextField;
    }

    public void setCaiyunTokenTextField(String caiyunTokenTextField) {
        this.caiyunTokenTextField.setText(caiyunTokenTextField);
    }

    public JTextField getHwProjectIdTextField() {
        return hwProjectIdTextField;
    }

    public void setHwProjectIdTextField(String hwProjectIdTextField) {
        this.hwProjectIdTextField.setText(hwProjectIdTextField);
    }

    public JTextField getHwAppIdTextField() {
        return hwAppIdTextField;
    }

    public void setHwAppIdTextField(String hwAppIdTextField) {
        this.hwAppIdTextField.setText(hwAppIdTextField);
    }

    public JPasswordField getHwAppSecretTextField() {
        return hwAppSecretTextField;
    }

    public void setHwAppSecretTextField(String hwAppSecretTextField) {
        this.hwAppSecretTextField.setText(hwAppSecretTextField);
    }

    public JTextField getThsAppIdTextField() {
        return thsAppIdTextField;
    }

    public void setThsAppIdTextField(String thsAppIdTextField) {
        this.thsAppIdTextField.setText(thsAppIdTextField);
    }

    public JPasswordField getThsAppSecretTextField() {
        return thsAppSecretTextField;
    }

    public void setThsAppSecretTextField(String thsAppSecretTextField) {
        this.thsAppSecretTextField.setText(thsAppSecretTextField);
    }

    public JComboBox getOpenModelComboBox() {
        return openModelComboBox;
    }

    public void setOpenModelComboBox(String modelComboBox) {
        this.openModelComboBox.setSelectedItem(modelComboBox);
    }

    public JComboBox getTyModelComboBox() {
        return tyModelComboBox;
    }

    public void setTyModelComboBox(String tyModelComboBox) {
        this.tyModelComboBox.setSelectedItem(tyModelComboBox);
    }

    public JPasswordField getTyKeyTextField() {
        return tyKeyTextField;
    }

    public void setTyKeyTextField(String tyKeyTextField) {
        this.tyKeyTextField.setText(tyKeyTextField);
    }

    public JCheckBox getAliyunDomainCheckBox() {
        return aliyunDomainCheckBox;
    }

    public void setAliyunDomainCheckBox(Boolean aliyunDomainCheckBox) {
        this.aliyunDomainCheckBox.setSelected(aliyunDomainCheckBox);
    }

    public JComboBox getAliyunDomainComboBox() {
        return aliyunDomainComboBox;
    }

    public void setAliyunDomainComboBox(String aliyunDomainComboBox) {
        this.aliyunDomainComboBox.setSelectedItem(aliyunDomainComboBox);
    }

    public JCheckBox getYoudaoDomainCheckBox() {
        return youdaoDomainCheckBox;
    }

    public void setYoudaoDomainCheckBox(Boolean youdaoDomainCheckBox) {
        this.youdaoDomainCheckBox.setSelected(youdaoDomainCheckBox);
    }

    public JComboBox getYoudaoDomainComboBox() {
        return youdaoDomainComboBox;
    }

    public void setYoudaoDomainComboBox(String youdaoDomainComboBox) {
        this.youdaoDomainComboBox.setSelectedItem(youdaoDomainComboBox);
    }

    public JComboBox getKimiModelComboBox() {
        return kimiModelComboBox;
    }

    public void setKimiModelComboBox(String kimiModelComboBox) {
        this.kimiModelComboBox.setSelectedItem(kimiModelComboBox);
    }

    public JPasswordField getKimiKeyPasswordField() {
        return kimiKeyPasswordField;
    }

    public void setKimiKeyPasswordField(String kimiKeyPasswordField) {
        this.kimiKeyPasswordField.setText(kimiKeyPasswordField);
    }

    public JComboBox getWenxinModelComboBox() {
        return wenxinModelComboBox;
    }

    public void setWenxinModelComboBox(String wenxinModelComboBox) {
        this.wenxinModelComboBox.setSelectedItem(wenxinModelComboBox);
    }

    public JTextField getWenxinApiKeyTextField() {
        return wenxinApiKeyTextField;
    }

    public void setWenxinApiKeyTextField(String wenxinApiKeyTextField) {
        this.wenxinApiKeyTextField.setText(wenxinApiKeyTextField);
    }

    public JPasswordField getWenxinApiSecretPasswordField() {
        return wenxinApiSecretPasswordField;
    }

    public void setWenxinApiSecretPasswordField(String wenxinApiSecretPasswordField) {
        this.wenxinApiSecretPasswordField.setText(wenxinApiSecretPasswordField);
    }

    public JTextField getCustomApiUrlTextField() {
        return customApiUrlTextField;
    }

    public void setCustomApiUrlTextField(String customApiUrlTextField) {
        this.customApiUrlTextField.setText(customApiUrlTextField);
    }

    public JTextField getCustomApiMaxCharLengthTextField() {
        return customApiMaxCharLengthTextField;
    }

    public void setCustomApiMaxCharLengthTextField(String customApiMaxCharLengthTextField) {
        this.customApiMaxCharLengthTextField.setText(customApiMaxCharLengthTextField);
    }

    public void setCustomApiMaxCharLengthTipLabel(JLabel customApiMaxCharLengthTipLabel) {
        this.customApiMaxCharLengthTipLabel = customApiMaxCharLengthTipLabel;
    }

    public JTextField getCustomSupportLanguageTextField() {
        return customSupportLanguageTextField;
    }

    public void setCustomSupportLanguageTextField(String customSupportLanguageTextField) {
        this.customSupportLanguageTextField.setText(customSupportLanguageTextField);
    }

    public JComboBox getLibreServerUrlComboBox() {
        return libreServerUrlComboBox;
    }

    public void setLibreServerUrlComboBox(String libreServerUrlComboBox) {
        this.libreServerUrlComboBox.setSelectedItem(libreServerUrlComboBox);
    }

}
