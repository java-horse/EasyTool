package easy.api.parse.parser;

import com.google.common.collect.Sets;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import easy.api.config.ApiDocConfig;
import easy.api.model.common.DataTypes;
import easy.api.model.common.Property;
import easy.api.parse.util.PsiAnnotationUtils;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;

import java.util.Set;

/**
 * 时间相关类型解析
 */
public class DateParser {

    private final ApiDocConfig settings;

    public DateParser(ApiDocConfig settings) {
        this.settings = settings;
    }

    /**
     * 处理请求参数为时间的场景
     */
    public boolean handle(Property property, PsiParameter parameter) {
        if (!isDateType(parameter.getType())) {
            return false;
        }
        // 1: @DateTimeFormat优先
        String dateFormat = PsiAnnotationUtils.getStringAttributeValue(parameter, ExtraPackageNameEnum.DATE_TIME_FORMAT.getName(), Constants.ANNOTATION_ATTR.PATTERN);
        if (dateFormat != null) {
            property.setType(DataTypes.STRING);
            property.setDateFormat(dateFormat);
            return true;
        }

        // 2: 日期或时间类型
        if (isTimeOnlyType(parameter.getType())) {
            property.setType(DataTypes.STRING);
            property.setDateFormat(settings.getTimeFormat());
            return true;
        }
        if (isDateOnlyType(parameter.getType())) {
            property.setType(DataTypes.STRING);
            property.setDateFormat(settings.getDateFormat());
            return true;
        }

        // 3: 全局配置
        String dateTimeFormatMvc = settings.getDateTimeFormatMvc();
        if (DataTypes.INTEGER.equals(dateTimeFormatMvc)) {
            property.setType(DataTypes.INTEGER);
            property.setDateFormat(null);
        } else {
            property.setType(DataTypes.STRING);
            property.setDateFormat(dateTimeFormatMvc);
        }
        return true;
    }

    /**
     * 字段处理
     */
    public boolean handle(Property property, PsiField field) {
        if (!isDateType(field.getType())) {
            return false;
        }
        // 1: @DateTimeFormat优先
        String dateFormat = PsiAnnotationUtils
                .getStringAttributeValue(field, ExtraPackageNameEnum.DATE_TIME_FORMAT.getName(), "pattern");
        if (dateFormat != null) {
            property.setType(DataTypes.STRING);
            property.setDateFormat(dateFormat);
            return true;
        }

        // 2: @JsonFormat优先
        String jsonFormat = PsiAnnotationUtils.getFieldAnnotationStringAttributeValue(field, ExtraPackageNameEnum.JSON_FORMAT.getName(), "pattern", true);
        if (jsonFormat != null) {
            property.setType(DataTypes.STRING);
            property.setDateFormat(jsonFormat);
            return true;
        }

        // 3: 日期或时间类型
        if (isTimeOnlyType(field.getType())) {
            property.setType(DataTypes.STRING);
            property.setDateFormat(settings.getTimeFormat());
            return true;
        }
        if (isDateOnlyType(field.getType())) {
            property.setType(DataTypes.STRING);
            property.setDateFormat(settings.getDateFormat());
            return true;
        }

        // 4: 全局配置
        String dateTimeFormatMvc = settings.getDateTimeFormatJson();
        if (DataTypes.INTEGER.equals(dateTimeFormatMvc)) {
            property.setType(DataTypes.INTEGER);
            property.setDateFormat(null);
        } else {
            property.setType(DataTypes.STRING);
            property.setDateFormat(dateTimeFormatMvc);
        }
        return true;
    }

    public static boolean isDateType(PsiType type) {
        Set<String> dateTypes = Sets.newHashSet(
                "java.util.Date",
                "java.sql.Date", "java.sql.Timestamp",
                "java.time.LocalDate", "java.time.LocalDateTime", "java.time.LocalTime"
        );
        return dateTypes.contains(type.getCanonicalText());
    }

    public static boolean isDateOnlyType(PsiType type) {
        Set<String> dateTypes = Sets.newHashSet("java.time.LocalDate");
        return dateTypes.contains(type.getCanonicalText());
    }

    public static boolean isTimeOnlyType(PsiType type) {
        Set<String> dateTypes = Sets.newHashSet("java.time.LocalTime");
        return dateTypes.contains(type.getCanonicalText());
    }
}
