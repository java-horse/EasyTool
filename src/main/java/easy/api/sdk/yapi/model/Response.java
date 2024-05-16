package easy.api.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;


/**
 * Yapi通用响应结果
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk
 * @date 2024/05/10 15:26
 */
public class Response<T> {

    /**
     * 状态码
     */
    @SerializedName("errcode")
    private Integer errorCode;

    /**
     * 状态信息
     */
    @SerializedName("errmsg")
    private String errorMessage;

    /**
     * 返回结果
     */
    private T data;

    private Response() {
    }

    private Response(Integer code, String message) {
        this.errorCode = code;
        this.errorMessage = message;
    }

    public static <T> Response<T> fail() {
        return new Response<>(500, "Fail");
    }

    /**
     * 是否成功
     *
     * @return boolean
     * @author mabin
     * @date 2024/05/10 15:27
     */
    public boolean isSuccess() {
        return errorCode != null && errorCode == 0;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
