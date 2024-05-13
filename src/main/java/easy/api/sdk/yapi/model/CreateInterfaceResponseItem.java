package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;

public class CreateInterfaceResponseItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -4844279121206865095L;

    @SerializedName("_id")
    private Integer id;

    @SerializedName("res_body")
    private String resBody;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getResBody() {
        return resBody;
    }

    public void setResBody(String resBody) {
        this.resBody = resBody;
    }
}
