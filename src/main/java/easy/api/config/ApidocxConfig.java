package easy.api.config;

import cn.hutool.core.convert.Convert;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import easy.util.JsonUtil;
import easy.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 加载EasyTool的默认ApiDoc配置文件
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.config
 * @date 2024/05/11 14:32
 */
public class ApidocxConfig {

    /**
     * 严格模式: 未指定分类、接口名不处理
     */
    private boolean strict = true;

    /**
     * 路径前缀
     */
    private String path;

    /**
     * yapi项目id
     */
    private String yapiProjectId;

    /**
     * rap2项目id
     */
    private String rap2ProjectId;

    /**
     * eolink项目hashKey
     */
    private String eolinkProjectId;

    /**
     * showdoc项目id
     */
    private String showdocProjectId;

    /**
     * apifox项目id
     */
    private String apifoxProjectId;

    /**
     * YApi服务地址: 用于统一登录场景
     */
    private String yapiUrl;

    /**
     * YApi项目token: 用于统一登录场景
     */
    private String yapiProjectToken;

    /**
     * 返回值包装类
     */
    private String returnWrapType;

    /**
     * 返回值解包装类
     */
    private List<String> returnUnwrapTypes;

    /**
     * 参数忽略类
     */
    private List<String> parameterIgnoreTypes;

    /**
     * 自定义bean配置
     */
    private Map<String, BeanCustom> beans;

    /**
     * 智能mock规则
     */
    private List<MockRule> mockRules;

    /**
     * 自定义注解值，简化@RequestBody注解
     */
    private RequestBodyParamType requestBodyParamType;

    /**
     * 时间格式: 查询参数和表单
     */
    private String dateTimeFormatMvc;

    /**
     * 时间格式: Json
     */
    private String dateTimeFormatJson;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 时间格式
     */
    private String timeFormat;

    private static final Pattern BEANS_PATTERN = Pattern.compile("^beans\\[(.+)]$");

    /**
     * 请求正文参数类型
     *
     * @author mabin
     * @project EasyTool
     * @package easy.api.config.ApidocxConfig
     * @date 2024/05/11 14:29
     */
    public static class RequestBodyParamType {
        /**
         * 注解类型
         */
        private String annotation;

        /**
         * 注解属性
         */
        private String property;

        public RequestBodyParamType(String type) {
            String[] splits = type.split("#");
            this.annotation = splits[0];
            if (splits.length > 1) {
                this.property = splits[1];
            } else {
                this.property = "value";
            }
        }

        public String getAnnotation() {
            return annotation;
        }

        public void setAnnotation(String annotation) {
            this.annotation = annotation;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }


    /**
     * 解析配置
     *
     * @param properties 属性
     * @return {@link easy.api.config.ApidocxConfig}
     * @author mabin
     * @date 2024/05/11 15:59
     */
    public static ApidocxConfig fromProperties(Properties properties) {
        String strict = properties.getProperty("strict", StringUtils.EMPTY);
        String path = properties.getProperty("path", null);
        String returnWrapType = properties.getProperty("returnWrapType", StringUtils.EMPTY);
        String returnUnwrapTypes = properties.getProperty("returnUnwrapTypes", StringUtils.EMPTY);
        String parameterIgnoreTypes = properties.getProperty("parameterIgnoreTypes", StringUtils.EMPTY);
        String mockRules = properties.getProperty("mockRules");
        String dateTimeFormatMvc = properties.getProperty("dateTimeFormatMvc", StringUtils.EMPTY);
        String dateTimeFormatJson = properties.getProperty("dateTimeFormatJson", StringUtils.EMPTY);
        String dateFormat = properties.getProperty("dateFormat", StringUtils.EMPTY);
        String timeFormat = properties.getProperty("timeFormat", StringUtils.EMPTY);
        String requestBodyParamType = properties.getProperty("requestBodyParamType", StringUtils.EMPTY);

        ApidocxConfig config = new ApidocxConfig();
        config.strict = Convert.toBool(strict, Boolean.FALSE);
        config.path = path;
        config.returnWrapType = StringUtils.trim(returnWrapType);
        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        config.returnUnwrapTypes = splitter.splitToList(returnUnwrapTypes);
        config.parameterIgnoreTypes = splitter.splitToList(parameterIgnoreTypes);
        config.dateTimeFormatMvc = dateTimeFormatMvc;
        config.dateTimeFormatJson = dateTimeFormatJson;
        config.dateFormat = dateFormat;
        config.timeFormat = timeFormat;
        if (StringUtils.isNotBlank(requestBodyParamType)) {
            config.requestBodyParamType = new RequestBodyParamType(requestBodyParamType);
        }
        // 解析自定义bean配置: beans[xxx].json=xxx
        Map<String, BeanCustom> beans = Maps.newHashMap();
        config.setBeans(beans);
        for (String p : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(p);
            if (StringUtils.isBlank(propertyValue)) {
                continue;
            }
            Matcher matcher = BEANS_PATTERN.matcher(p);
            if (!matcher.matches()) {
                continue;
            }
            beans.put(matcher.group(1), JsonUtil.fromJson(propertyValue, BeanCustom.class));
        }
        // 智能mock规则
        if (StringUtils.isNotBlank(mockRules)) {
            config.mockRules = JsonUtil.fromJson(mockRules, new TypeToken<List<MockRule>>() {
            }.getType());
        }
        return config;
    }

    /**
     * 获取bean自定义设置
     *
     * @param type 类型
     * @return {@link easy.api.config.BeanCustom}
     * @author mabin
     * @date 2024/05/11 15:59
     */
    public BeanCustom getBeanCustomSettings(String type) {
        BeanCustom custom = null;
        if (this.beans != null) {
            custom = this.beans.get(type);
        }
        if (custom != null) {
            if (custom.getIncludes() == null) {
                custom.setIncludes(Collections.emptyNavigableSet());
            }
            if (custom.getExcludes() == null) {
                custom.setExcludes(Collections.emptyNavigableSet());
            }
            if (custom.getFields() == null) {
                custom.setFields(Maps.newHashMapWithExpectedSize(0));
            }
        }
        return custom;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getYapiProjectId() {
        return yapiProjectId;
    }

    public void setYapiProjectId(String yapiProjectId) {
        this.yapiProjectId = yapiProjectId;
    }

    public String getRap2ProjectId() {
        return rap2ProjectId;
    }

    public void setRap2ProjectId(String rap2ProjectId) {
        this.rap2ProjectId = rap2ProjectId;
    }

    public String getEolinkProjectId() {
        return eolinkProjectId;
    }

    public void setEolinkProjectId(String eolinkProjectId) {
        this.eolinkProjectId = eolinkProjectId;
    }

    public String getShowdocProjectId() {
        return showdocProjectId;
    }

    public void setShowdocProjectId(String showdocProjectId) {
        this.showdocProjectId = showdocProjectId;
    }

    public String getApifoxProjectId() {
        return apifoxProjectId;
    }

    public void setApifoxProjectId(String apifoxProjectId) {
        this.apifoxProjectId = apifoxProjectId;
    }

    public String getYapiUrl() {
        return yapiUrl;
    }

    public void setYapiUrl(String yapiUrl) {
        this.yapiUrl = yapiUrl;
    }

    public String getYapiProjectToken() {
        return yapiProjectToken;
    }

    public void setYapiProjectToken(String yapiProjectToken) {
        this.yapiProjectToken = yapiProjectToken;
    }

    public String getReturnWrapType() {
        return returnWrapType;
    }

    public void setReturnWrapType(String returnWrapType) {
        this.returnWrapType = returnWrapType;
    }

    public List<String> getReturnUnwrapTypes() {
        return returnUnwrapTypes;
    }

    public void setReturnUnwrapTypes(List<String> returnUnwrapTypes) {
        this.returnUnwrapTypes = returnUnwrapTypes;
    }

    public List<String> getParameterIgnoreTypes() {
        return parameterIgnoreTypes;
    }

    public void setParameterIgnoreTypes(List<String> parameterIgnoreTypes) {
        this.parameterIgnoreTypes = parameterIgnoreTypes;
    }

    public Map<String, BeanCustom> getBeans() {
        return beans;
    }

    public void setBeans(Map<String, BeanCustom> beans) {
        this.beans = beans;
    }

    public List<MockRule> getMockRules() {
        return mockRules;
    }

    public void setMockRules(List<MockRule> mockRules) {
        this.mockRules = mockRules;
    }

    public RequestBodyParamType getRequestBodyParamType() {
        return requestBodyParamType;
    }

    public void setRequestBodyParamType(RequestBodyParamType requestBodyParamType) {
        this.requestBodyParamType = requestBodyParamType;
    }

    public String getDateTimeFormatMvc() {
        return dateTimeFormatMvc;
    }

    public void setDateTimeFormatMvc(String dateTimeFormatMvc) {
        this.dateTimeFormatMvc = dateTimeFormatMvc;
    }

    public String getDateTimeFormatJson() {
        return dateTimeFormatJson;
    }

    public void setDateTimeFormatJson(String dateTimeFormatJson) {
        this.dateTimeFormatJson = dateTimeFormatJson;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }
}
