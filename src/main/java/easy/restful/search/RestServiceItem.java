package easy.restful.search;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.enums.SwaggerAnnotationEnum;
import easy.restful.api.HttpMethod;
import easy.restful.icons.Icons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class RestServiceItem implements NavigationItem {

    private final PsiElement psiElement;
    private PsiMethod psiMethod;
    private Module module;
    private HttpMethod method;
    private String url;
    private Navigatable navigationElement;

    public RestServiceItem(PsiElement psiElement, HttpMethod method, String urlPath) {
        this.psiElement = psiElement;
        if (psiElement instanceof PsiMethod) {
            this.psiMethod = (PsiMethod) psiElement;
        }
        if (method != null) {
            this.method = method;
        }
        this.url = urlPath;
        if (psiElement instanceof Navigatable) {
            navigationElement = (Navigatable) psiElement;
        }
    }

    @Nullable
    @Override
    public String getName() {
        return this.url;
    }

    @Nullable
    @Override
    @Contract(" -> new")
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getUrl();
            }

            @Override
            public String getLocationString() {
                String location = null;
                if (psiElement instanceof PsiMethod psiMethod) {
                    PsiClass psiClass = psiMethod.getContainingClass();
                    if (psiClass != null) {
                        location = psiClass.getName();
                    }
                    location += "#" + psiMethod.getName();
                    String methodComment = getMethodComment(psiMethod);
                    if (StringUtils.isNotBlank(methodComment)) {
                        location += "#" + methodComment;
                    }
                    location = "Java: (" + location + ")";
                }
                if (psiElement != null) {
                    location += " in " + psiElement.getResolveScope().getDisplayName();
                }
                return location;
            }

            @NotNull
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.getMethodIcon(method);
            }
        };
    }

    /**
     * 获取方法注释
     * 优先规则：swagger的ApiOperation中的value属性 -> 普通JavaDoc注释
     *
     * @param psiMethod psi方法
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/06 17:59
     */
    private String getMethodComment(PsiMethod psiMethod) {
        PsiAnnotation psiAnnotation = psiMethod.getModifierList().findAnnotation(SwaggerAnnotationEnum.API_OPERATION.getClassPackage());
        if (Objects.nonNull(psiAnnotation)) {
            PsiAnnotationMemberValue psiAnnotationAttributeValue = psiAnnotation.findAttributeValue("value");
            if (Objects.nonNull(psiAnnotationAttributeValue)) {
                return StringUtils.replace(psiAnnotationAttributeValue.getText(), "\"", StringUtils.EMPTY);
            }
        }
        // 获取JavaDoc中第一行非空注释元素即可
        PsiDocComment docComment = psiMethod.getDocComment();
        if (Objects.nonNull(docComment)) {
            PsiElement[] descriptionElements = docComment.getDescriptionElements();
            for (PsiElement descriptionElement : descriptionElements) {
                String text = descriptionElement.getText().trim();
                if (StringUtils.isNotEmpty(text)) {
                    return text;
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationElement != null) {
            navigationElement.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return navigationElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    /**
     * 匹配
     */
    public boolean matches(String queryText) {
        if ("/".equals(queryText)) {
            return true;
        }

        MinusculeMatcher matcher = NameUtil.buildMatcher("*" + queryText, NameUtil.MatchingCaseSensitivity.NONE);
        return matcher.matches(this.url);
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public PsiMethod getPsiMethod() {
        return psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }
}