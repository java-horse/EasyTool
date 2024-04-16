package easy.api.data;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;

import java.util.Objects;


/**
 * ApiDoc全局数据上下文
 *
 * @author mabin
 * @project EasyTool
 * @package easy.api.data
 * @date 2024/04/13 14:21
 */
public class ApiDocDataContent {

    /**
     * 存放本次动作的一些全局上下文数据
     */
    private static final ThreadLocal<ApiDocData> API_DOC_DATA_THREAD_LOCAL = new ThreadLocal<>();

    public static void remove() {
        API_DOC_DATA_THREAD_LOCAL.remove();
    }


    /**
     * 向全局上下文中 新增|获取 数据内容
     *
     * @param apiDocData API文档数据
     * @author mabin
     * @date 2024/04/13 14:23
     */
    public static void setData(ApiDocData apiDocData) {
        API_DOC_DATA_THREAD_LOCAL.set(apiDocData);
    }


    /**
     * 从全局上下文中获取ApiDoc数据对象
     *
     * @return {@link easy.api.data.ApiDocData }
     * @author mabin
     * @date 2024/04/13 14:23
     */
    public static ApiDocData getApiDocData() {
        ApiDocData apiDocData = API_DOC_DATA_THREAD_LOCAL.get();
        return Objects.isNull(apiDocData) ? new ApiDocData() : apiDocData;
    }


    /**
     * 从全局上下文中获取Project对象
     *
     * @return {@link com.intellij.openapi.project.Project }
     * @author mabin
     * @date 2024/04/13 14:23
     */
    public static Project getProject() {
        ApiDocData apiDocData = getApiDocData();
        if (Objects.nonNull(apiDocData)) {
            return apiDocData.getEvent().getProject();
        }
        return null;
    }


    /**
     * 获取当前事件选中的文本内容
     *
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/04/13 14:22
     */
    public static String getSelectedText() {
        Editor editor = getApiDocData().getEvent().getRequiredData(CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        return selectionModel.getSelectedText();
    }

}
