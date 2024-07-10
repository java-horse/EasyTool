package easy.settings.doc.template;

import com.intellij.openapi.ide.CopyPasteManager;
import easy.config.doc.JavaDocConfig;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.util.Vector;

public abstract class AbstractJavaDocTemplateSettingView {
    protected static Vector<String> customNames;
    protected static Vector<String> innerNames;

    static {
        customNames = new Vector<>(5);
        customNames.add("变量");
        customNames.add("类型");
        customNames.add("自定义值");
        innerNames = new Vector<>(4);
        innerNames.add("变量");
        innerNames.add("说明");
    }

    protected JavaDocConfig config;

    protected AbstractJavaDocTemplateSettingView(JavaDocConfig config) {
        this.config = config;
    }

    public abstract JComponent getComponent();

}
