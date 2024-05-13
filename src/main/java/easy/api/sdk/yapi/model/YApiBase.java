package easy.api.sdk.yapi.model;

import java.io.Serial;
import java.io.Serializable;

public class YApiBase implements Serializable {
    @Serial
    private static final long serialVersionUID = -6534421449827881815L;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
