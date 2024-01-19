package easy.action.convert;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import easy.icons.EasyIcons;
import easy.json.str2Json.Str2JsonWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Str转Json的Action
 *
 * @project: EasyTool
 * @package: easy.action.json
 * @author: mabin
 * @date: 2024/01/19 11:47:14
 */
public class Str2JsonAction extends AnAction {

    public Str2JsonAction() {
        super("Str2Json", "Str2Json", EasyIcons.ICON.JSON);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(project) || Objects.isNull(editor)) {
            return;
        }
        Str2JsonWrapper str2JsonWrapper = new Str2JsonWrapper(project);
        str2JsonWrapper.pack();
        str2JsonWrapper.setSize(800, 450);
        str2JsonWrapper.showAndGet();
    }


}
