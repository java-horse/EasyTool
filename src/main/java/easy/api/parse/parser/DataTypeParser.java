package easy.api.parse.parser;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiType;
import easy.api.config.ApiDocConfig;
import easy.api.model.common.DataTypes;
import easy.base.ApiDocConstants;
import easy.api.parse.util.PsiTypeUtils;
import easy.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * 字段类型工具类.
 */
public final class DataTypeParser {
    private final Project project;
    private final Module module;
    private final ApiDocConfig settings;

    public DataTypeParser(Project project, Module module, ApiDocConfig settings) {
        this.project = project;
        this.module = module;
        this.settings = settings;
    }

    /**
     * 获取字段类型
     */
    public String parse(PsiType type) {
        // 数组类型处理
        if (PsiTypeUtils.isArray(type) || PsiTypeUtils.isCollection(type, this.project, this.module)) {
            return DataTypes.ARRAY;
        }
        boolean isEnum = PsiTypeUtils.isEnum(type);
        if (isEnum) {
            return DataTypes.STRING;
        }
        String dataType = getTypeInProperties(type);
        return StringUtils.isEmpty(dataType) ? DataTypes.OBJECT : dataType;
    }

    /**
     * 获取字段类型
     */
    public String parseFormat(PsiType type) {
        // 数组类型处理
        if (PsiTypeUtils.isArray(type) || PsiTypeUtils.isCollection(type, this.project, this.module)) {
            return null;
        }
        boolean isEnum = PsiTypeUtils.isEnum(type);
        if (isEnum) {
            return null;
        }
        return getFormatInProperties(type);
    }

    public static String getTypeInProperties(PsiType type) {
        Properties typeProperties = PropertiesUtil.getProperties(ApiDocConstants.CONFIG_FILE.TYPES);
        return typeProperties.getProperty(type.getCanonicalText());
    }

    public static String getFormatInProperties(PsiType type) {
        Properties typeProperties = PropertiesUtil.getProperties(ApiDocConstants.CONFIG_FILE.TYPES_FORMAT);
        return typeProperties.getProperty(type.getCanonicalText());
    }

}
