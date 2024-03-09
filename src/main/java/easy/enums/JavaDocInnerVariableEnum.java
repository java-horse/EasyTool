package easy.enums;

import easy.base.Constants;

public enum JavaDocInnerVariableEnum {
    DOC("$DOC$", "JavaDoc文本注释信息", "doc"),
    PARAMS("$PARAMS$", "遍历传入参数并添加注释", "params"),
    RETURN("$RETURN$", "返回值类型", "return"),
    THROWS("$THROWS$", "异常类型并注释", "throws"),
    SEE("$SEE$", "引用传入参数类型和返回值类型", "see"),
    AUTHOR("$AUTHOR$", "作者信息，可在通用配置里修改作者信息 (默认取系统用户名)", "author"),
    DATE("$DATE$", "日期信息，格式可在通用配置中修改 (默认格式: " + Constants.JAVA_DOC.DEFAULT_DATE_FORMAT + ")", "date"),
    SINCE("$SINCE$", "起始版本，默认：1.0.0", "since"),
    VERSION("$VERSION$", "起始版本，默认：1.0.0", "version"),
    PROJECT("$PROJECT$", "当前项目名称", "project"),
    PACKAGE("$PACKAGE$", "当前包路径", "package");

    public final String name;
    public final String value;
    public final String key;

    JavaDocInnerVariableEnum(String name, String value, String key) {
        this.name = name;
        this.value = value;
        this.key = key;
    }

}
