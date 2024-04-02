package easy.handler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import easy.translate.TranslateService;

public class ApiDocHandler {

    private static final Logger log = Logger.getInstance(ApiDocHandler.class);
    private final TranslateService translateService = ServiceHelper.getService(TranslateService.class);

    private final Project project;
    private final PsiFile psiFile;
    private final PsiClass psiClass;
    private final String selectionText;

    public ApiDocHandler(Project project, PsiFile psiFile, PsiClass psiClass, String selectionText) {
        this.project = project;
        this.psiFile = psiFile;
        this.psiClass = psiClass;
        this.selectionText = selectionText;
    }


}
