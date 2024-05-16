package easy.api.process.yapi;

import cn.hutool.http.Header;
import com.google.common.collect.Sets;
import easy.api.model.common.Api;
import easy.api.model.common.ParameterIn;
import easy.api.model.common.Property;
import easy.api.model.common.RequestBodyType;
import easy.api.model.yapi.ApiInterfaceStatus;
import easy.api.sdk.yapi.model.ApiInterface;
import easy.api.sdk.yapi.model.ApiParameter;
import easy.api.sdk.yapi.model.ApiProperty;
import easy.api.sdk.yapi.model.ApiProperty.Mock;
import easy.base.Constants;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Yapi数据转换
 */
public class YapiDataConvector {

    public static ApiInterface convert(Integer projectId, Api api) {
        ApiInterface yapi = new ApiInterface();
        yapi.setProjectId(projectId);
        yapi.setTitle(StringUtils.isNotEmpty(api.getSummary()) ? api.getSummary() : api.getPath());
        yapi.setPath(api.getPath());
        yapi.setMethod(api.getMethod().name());
        yapi.setTag(api.getTags());
        yapi.setDesc(api.getDescription());
        yapi.setMenu(api.getCategory());
        yapi.setStatus(ApiInterfaceStatus.undone.name());
        yapi.setReqHeaders(resolveParameter(api, ParameterIn.header));
        yapi.setReqQuery(resolveParameter(api, ParameterIn.query));
        yapi.setReqBodyType(resolveReqBodyType(api));
        yapi.setReqBodyForm(resolveReqBodyForm(api));
        yapi.setReqBodyOther(resolveReqBody(api));
        yapi.setReqBodyIsJsonSchema(api.getRequestBodyType() == RequestBodyType.json);
        yapi.setReqParams(resolveParameter(api, ParameterIn.path));
        yapi.setResBody(resolveResBody(api));
        return yapi;
    }

    /**
     * 解析请求体类型
     */
    private static String resolveReqBodyType(Api api) {
        RequestBodyType type = api.getRequestBodyType();
        if (type == RequestBodyType.form_data) {
            type = RequestBodyType.form;
        }
        return type != null ? type.name() : null;
    }

    /**
     * 解析请求参数
     */
    private static List<ApiParameter> resolveParameter(Api api, ParameterIn in) {
        if (api.getParameters() == null) {
            return Collections.emptyList();
        }

        List<Property> parameters = api.getParametersByIn(in);
        List<ApiParameter> data = parameters.stream().map(p -> {
            ApiParameter parameter = new ApiParameter();
            parameter.setName(p.getName());
            parameter.setDesc(p.getDescription());
            parameter.setExample(p.getExample());
            parameter.setRequired(p.getRequired() ? "1" : "0");
            parameter.setValue(p.getDefaultValue());
            if (in == ParameterIn.query) {
                parameter.setExample(p.getDefaultValue());
            }
            return parameter;
        }).collect(Collectors.toList());

        // 请求头
        if (in == ParameterIn.header && api.getRequestBodyType() != null) {
            ApiParameter contentType = new ApiParameter();
            contentType.setName(Header.CONTENT_TYPE.getValue());
            contentType.setValue(api.getRequestBodyType().getContentType());
            data.add(contentType);
        }
        return data;
    }


    /**
     * 解析请求体表单
     */
    private static List<ApiParameter> resolveReqBodyForm(Api api) {
        List<Property> items = api.getRequestBodyForm();
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(p -> {
            ApiParameter parameter = new ApiParameter();
            parameter.setName(p.getName());
            parameter.setType("file".equals(p.getType()) ? "file" : "text");
            parameter.setDesc(p.getDescription());
            parameter.setRequired(p.getRequired() ? "1" : "0");
            parameter.setExample(p.getExample());
            if (StringUtils.isNotEmpty(p.getDefaultValue())) {
                parameter.setExample(p.getDefaultValue());
            }
            parameter.setDesc(p.getDescription());
            return parameter;
        }).collect(Collectors.toList());
    }

    /**
     * 解析请求体
     */
    private static String resolveReqBody(Api api) {
        Property request = api.getRequestBody();
        if (request == null) {
            return "";
        }
        return JsonUtil.toJson(copyProperty(request));
    }

    /**
     * 解析响应体
     */
    private static String resolveResBody(Api api) {
        Property responses = api.getResponses();
        if (responses == null) {
            return "";
        }
        return JsonUtil.toJson(copyProperty(responses));
    }

    /**
     * 复制Property为YapiProperty结构，包括子树
     */
    private static ApiProperty copyProperty(Property property) {
        ApiProperty apiProperty = new ApiProperty();
        apiProperty.setType(property.getType());
        apiProperty.setDescription(property.getDescription());
        apiProperty.setDefaultValue(property.getDefaultValue());

        if (StringUtils.isNotEmpty(property.getMock())) {
            apiProperty.setMock(new Mock(property.getMock()));
        }
        // 必填
        Set<String> required = Sets.newLinkedHashSet();
        if (property.getProperties() != null) {
            for (Entry<String, Property> entry : property.getProperties().entrySet()) {
                if (entry.getValue().getRequired()) {
                    required.add(entry.getKey());
                }
            }
        }
        apiProperty.setRequired(required);
        // 数组
        if (property.getItems() != null) {
            apiProperty.setUniqueItems(property.getUniqueItems());
            apiProperty.setMinItems(property.getMinLength());
            apiProperty.setMaxItems(property.getMaxLength());
            apiProperty.setItems(copyProperty(property.getItems()));
        }
        if (property.isStringType()) {
            apiProperty.setMinLength(property.getMinLength());
            apiProperty.setMaxLength(property.getMaxLength());
        }
        // 对象
        if (property.getProperties() != null) {
            Map<String, ApiProperty> yapiProperties = new LinkedHashMap<>();
            for (Entry<String, Property> entry : property.getProperties().entrySet()) {
                String key = entry.getKey();
                Property value = entry.getValue();
                if (value.getRequired()) {
                    required.add(key);
                }
                yapiProperties.put(key, copyProperty(value));
            }
            apiProperty.setProperties(yapiProperties);
        }

        return apiProperty;
    }


}
