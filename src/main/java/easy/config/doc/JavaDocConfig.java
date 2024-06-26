package easy.config.doc;

public class JavaDocConfig {

    private String author;
    private String dateFormat;
    private String methodReturnType;
    private String propertyCommentType;
    private String propertyCommentModel;

    // 类、方法、属性的JavaDoc模板配置持久化
    private JavaDocTemplateConfig javaDocClassTemplateConfig = new JavaDocTemplateConfig();
    private JavaDocTemplateConfig javaDocMethodTemplateConfig = new JavaDocTemplateConfig();
    private JavaDocTemplateConfig javaDocFieldTemplateConfig = new JavaDocTemplateConfig();

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    public String getPropertyCommentType() {
        return propertyCommentType;
    }

    public void setPropertyCommentType(String propertyCommentType) {
        this.propertyCommentType = propertyCommentType;
    }

    public String getPropertyCommentModel() {
        return propertyCommentModel;
    }

    public void setPropertyCommentModel(String propertyCommentModel) {
        this.propertyCommentModel = propertyCommentModel;
    }

    public JavaDocTemplateConfig getJavaDocClassTemplateConfig() {
        if (javaDocClassTemplateConfig == null) {
            javaDocClassTemplateConfig = new JavaDocTemplateConfig();
        }
        return javaDocClassTemplateConfig;
    }

    public void setJavaDocClassTemplateConfig(JavaDocTemplateConfig javaDocClassTemplateConfig) {
        this.javaDocClassTemplateConfig = javaDocClassTemplateConfig;
    }

    public JavaDocTemplateConfig getJavaDocMethodTemplateConfig() {
        if (javaDocMethodTemplateConfig == null) {
            javaDocMethodTemplateConfig = new JavaDocTemplateConfig();
        }
        return javaDocMethodTemplateConfig;
    }

    public void setJavaDocMethodTemplateConfig(JavaDocTemplateConfig javaDocMethodTemplateConfig) {
        this.javaDocMethodTemplateConfig = javaDocMethodTemplateConfig;
    }

    public JavaDocTemplateConfig getJavaDocFieldTemplateConfig() {
        if (javaDocFieldTemplateConfig == null) {
            javaDocFieldTemplateConfig = new JavaDocTemplateConfig();
        }
        return javaDocFieldTemplateConfig;
    }

    public void setJavaDocFieldTemplateConfig(JavaDocTemplateConfig javaDocFieldTemplateConfig) {
        this.javaDocFieldTemplateConfig = javaDocFieldTemplateConfig;
    }

}
