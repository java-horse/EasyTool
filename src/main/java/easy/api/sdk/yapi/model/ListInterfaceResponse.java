package easy.api.sdk.yapi.model;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * YApi接口列表响应参数
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.sdk.yapi.model
 * @date 2024/05/11 09:55
 */
public class ListInterfaceResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -7916769105021023474L;

    private Integer count;

    private Integer total;

    private List<ApiInterfaceVO> list;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<ApiInterfaceVO> getList() {
        return list;
    }

    public void setList(List<ApiInterfaceVO> list) {
        this.list = list;
    }
}
