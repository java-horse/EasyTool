package easy.api.model.common;

public enum RequestBodyType {

    form("application/x-www-form-urlencoded"),
    form_data("multipart/form-data"),
    json("application/json"),
    raw("raw");

    private final String contentType;

    public String getContentType() {
        return contentType;
    }

    RequestBodyType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isFormOrFormData() {
        return form.equals(this) || form_data.equals(this);
    }

}
