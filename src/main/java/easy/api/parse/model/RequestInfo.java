package easy.api.parse.model;

import easy.api.model.common.Property;
import easy.api.model.common.RequestBodyType;

import java.util.List;

/**
 * 请求参数信息
 */
public class RequestInfo {

    private List<Property> parameters;
    private RequestBodyType requestBodyType;
    private Property requestBody;
    private List<Property> requestBodyForm;

    public List<Property> getParameters() {
        return parameters;
    }

    public void setParameters(List<Property> parameters) {
        this.parameters = parameters;
    }

    public RequestBodyType getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(RequestBodyType requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public Property getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Property requestBody) {
        this.requestBody = requestBody;
    }

    public List<Property> getRequestBodyForm() {
        return requestBodyForm;
    }

    public void setRequestBodyForm(List<Property> requestBodyForm) {
        this.requestBodyForm = requestBodyForm;
    }
}
