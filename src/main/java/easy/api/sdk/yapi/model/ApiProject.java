package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.io.Serializable;

/**
 * YApi项目信息
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi.model
 * @date 2024/05/10 15:36
 */
public class ApiProject extends YApiBase implements Serializable {
    @Serial
    private static final long serialVersionUID = -8971493916524598720L;

    @SerializedName(("_id"))
    private Integer id;

    @SerializedName("name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
