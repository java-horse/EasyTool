package easy.config.doc;

import javax.swing.*;

public class JavaDocConfig {

    private String author;
    private String dateFormat;
    private String methodReturnType;
    private String propertyCommentType;

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

}
