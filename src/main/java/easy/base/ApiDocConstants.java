package easy.base;

public class ApiDocConstants {

    public static final String VALUE = "value";

    /**
     * 修饰符属性
     *
     * @author mabin
     * @project EasyTool
     * @package easy.base.ApiDocConstants
     * @date 2024/04/13 13:45
     */
    public interface ModifierProperty {
        String STATIC = "static";
        String FINAL = "final";
        String VOID = "void";
    }

    public interface ExtInfo {
        String IS_ATTR = "isAttr";
        /**
         * 标识当前对象还未解析完成
         */
        String IS_EARLY = "isEarly";

        String GENERICS_TYPE = "genericsType";
        String PARAM_TYPE = "codeParamType";

        String REFERENCE_DESC_ID = "referenceDescId";
        String ROOT = "root";
    }

    public interface Comment {

        /**
         * 请求参数的注释tag
         * TODO 后期可改成从配置中取
         */
        String PARAM = "param";
        /**
         * 方法返回值注释tag
         */
        String RETURN = "return";

        /**
         * psi中描述注释内容的类型
         */
        String PSI_COMMENT_DATA = "DOC_COMMENT_DATA";
        String PSI_COMMENT_TAG_VALUE = "DOC_TAG_VALUE_ELEMENT";
        String PSI_PARAMETER_REF = "DOC_PARAMETER_REF";


        String COMMENT_START_1 = "/*";
        String COMMENT_START_2 = "//";
        String COMMENT_END_1 = "*/";

        String COMMENT_X = "*";
    }

}
