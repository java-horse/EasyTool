package easy.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiType;
import easy.base.Constants;
import easy.enums.BaseTypeEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Swagger注释处理
 *
 * @project: EasyChar
 * @package: easy.util
 * @author: mabin
 * @date: 2023/11/04 10:23:07
 */
public class SwaggerCommentUtil {

    private static final Logger log = Logger.getInstance(SwaggerCommentUtil.class);

    private SwaggerCommentUtil() {
    }

    /**
     * 获取数据类型
     *
     * @param dataType
     * @param psiType
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/4 10:34
     */
    public static String getDataType(String dataType, PsiType psiType) {
        String baseType = BaseTypeEnum.findBaseType(dataType);
        if (StringUtils.isNotBlank(baseType)) {
            return baseType;
        }
        if (Boolean.TRUE.equals(BaseTypeEnum.isBaseType(dataType))) {
            return dataType;
        }
        String multipartFileText = "org.springframework.web.multipart.MultipartFile";
        String javaFileText = "java.io.File";
        if (StringUtils.equalsAny(psiType.getCanonicalText(), multipartFileText, javaFileText)) {
            return "file";
        }
        for (PsiType superType : psiType.getSuperTypes()) {
            if (StringUtils.equalsAny(superType.getCanonicalText(), multipartFileText, javaFileText)) {
                return "file";
            }
        }
        return psiType.getPresentableText();
    }


    /**
     * 获取注解说明
     *
     * @param allComment 所有注释
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/4 10:33
     */
    public static String getCommentDesc(String allComment) {
        if (StringUtils.isBlank(allComment)) {
            return StringUtils.EMPTY;
        }
        String[] commentArr = allComment.split(StringUtils.LF);
        if (ArrayUtils.isEmpty(commentArr)) {
            return StringUtils.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String comment : commentArr) {
            String row = StringUtils.deleteWhitespace(comment);
            if (StringUtils.isBlank(row) || StringUtils.startsWith(row, "/**")) {
                continue;
            }
            if (StringUtils.startsWithIgnoreCase(row, "*@desc")
                    && !StringUtils.startsWithIgnoreCase(row, "*@describe")
                    && !StringUtils.startsWithIgnoreCase(row, "*@description")) {
                appendComment(comment, stringBuilder, 5);
            } else if (StringUtils.startsWithIgnoreCase(row, "*@description")) {
                appendComment(comment, stringBuilder, 12);
            } else if (StringUtils.startsWithIgnoreCase(row, "*@describe")) {
                appendComment(comment, stringBuilder, 9);
            } else if (StringUtils.startsWith(row, "*@") || StringUtils.startsWith(row, "*/")) {
                continue;
            }
            int descIndex = StringUtils.ordinalIndexOf(comment, "*", 1);
            if (descIndex == -1) {
                descIndex = StringUtils.ordinalIndexOf(comment, "//", 1);
                descIndex += 1;
            }
            String desc = comment.substring(descIndex + 1);
            stringBuilder.append(desc);
        }
        return StringUtils.trim(stringBuilder.toString());
    }

    /**
     * 获取方法注释参数
     *
     * @param allComment
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author mabin
     * @date 2023/11/4 10:35
     */
    public static Map<String, String> getCommentMethodParam(String allComment) {
        Map<String, String> resultMap = new HashMap<>(4);
        if (StringUtils.isBlank(allComment)) {
            return resultMap;
        }
        String[] commentArr = allComment.split(StringUtils.LF);
        if (ArrayUtils.isEmpty(commentArr)) {
            return resultMap;
        }
        for (String comment : commentArr) {
            String row = StringUtils.deleteWhitespace(comment);
            if (StringUtils.isEmpty(row) || StringUtils.startsWith(row, "/**")) {
                continue;
            }
            if (StringUtils.startsWithIgnoreCase(row, "*@param")) {
                int paramIndex = StringUtils.ordinalIndexOf(comment, "m", 1);
                comment = comment.substring(paramIndex + 1).trim().replaceAll("\\s+", " ");
                String[] s = comment.split(" ");
                if (s.length < 2) {
                    continue;
                }
                StringBuilder paramDesc = new StringBuilder();
                for (int i = 1; i < s.length; i++) {
                    paramDesc.append(s[i]);
                    if (i == s.length - 1) {
                        break;
                    }
                    paramDesc.append(" ");
                }
                resultMap.put(s[0], paramDesc.toString());
            }
        }
        return resultMap;
    }

    /**
     * 追加注释
     *
     * @param string
     * @param stringBuilder
     * @param index
     * @return void
     * @author mabin
     * @date 2023/11/4 10:37
     */
    private static void appendComment(String string, StringBuilder stringBuilder, int index) {
        String lowerCaseStr = string.toLowerCase();
        int descIndex = StringUtils.ordinalIndexOf(lowerCaseStr, Constants.AT, 1);
        descIndex += index;
        String desc = string.substring(descIndex);
        stringBuilder.append(desc);
    }


}
