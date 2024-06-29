package easy.form.widget;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.form.widget.core.CronDialogView;
import easy.form.widget.core.QrCodeDialogView;
import easy.form.widget.core.UrlEncodeDialogView;
import easy.form.widget.core.YmlConvertDialogView;
import easy.helper.ServiceHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class WidgetCommonView extends DialogWrapper {

    private CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

    private JPanel panel;
    private JTabbedPane tabbedPane;

    public WidgetCommonView() {
        super(ProjectManagerEx.getInstance().getDefaultProject());
        setTitle("Widget Core View");
        tabbedPane.addTab("URL转码", new UrlEncodeDialogView().getContent());
        tabbedPane.addTab("Cron预览", new CronDialogView().getContent());
        tabbedPane.addTab("QR二维码", new QrCodeDialogView().getContent());
        tabbedPane.addTab("YML转换", new YmlConvertDialogView().getContent());
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        Dimension size = panel.getPreferredSize();
        setSize(Math.max(size.width, 800), Math.max(size.height, 500));
        return panel;
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{};
    }


}
