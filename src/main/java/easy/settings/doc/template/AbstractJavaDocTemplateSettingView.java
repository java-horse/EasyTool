package easy.settings.doc.template;

import easy.config.doc.JavaDocConfig;

import javax.swing.*;
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
        innerNames.add("含义");
    }

    protected JavaDocConfig config;

    public AbstractJavaDocTemplateSettingView(JavaDocConfig config) {
        this.config = config;
    }

    public abstract JComponent getComponent();

}
