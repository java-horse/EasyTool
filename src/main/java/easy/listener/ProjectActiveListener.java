package easy.listener;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * 插件运行（项目打开）监听器
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/01 14:26
 **/

public class ProjectActiveListener implements ProjectManagerListener {

    private static final Logger log = Logger.getInstance(ProjectActiveListener.class);

    /**
     * project open listener
     *
     * @param project
     * @return void
     * @author mabin
     * @date 2023/9/1 14:27
     **/
    @Override
    public void projectOpened(@NotNull Project project) {

    }


}
