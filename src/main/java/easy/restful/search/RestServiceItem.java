package easy.restful.search;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import easy.config.common.CommonConfig;
import easy.config.common.CommonConfigComponent;
import easy.enums.SwaggerAnnotationEnum;
import easy.helper.ServiceHelper;
import easy.restful.api.HttpMethod;
import easy.restful.icons.Icons;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class RestServiceItem implements NavigationItem {

    private final CommonConfig commonConfig = ServiceHelper.getService(CommonConfigComponent.class).getState();

    private final PsiElement psiElement;
    private PsiMethod psiMethod;
    private Module module;
    private HttpMethod method;
    private String url;
    private Navigatable navigationElement;
    private String accessTime;

    public RestServiceItem(PsiElement psiElement, HttpMethod method, String urlPath) {
        this.psiElement = psiElement;
        if (psiElement instanceof PsiMethod) {
            this.psiMethod = (PsiMethod) psiElement;
        }
        if (method != null) {
            this.method = method;
        }
        String methodComment = null;
        if (Objects.nonNull(commonConfig) && Boolean.TRUE.equals(commonConfig.getRestfulDisplayApiCommentCheckBox())) {
            methodComment = getMethodComment(psiMethod);
        }
        this.url = urlPath + (StringUtils.isNotBlank(methodComment) ? (" #" + methodComment) : StringUtils.EMPTY);
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
                    location = "(" + location + ")";
                }
                if (psiElement != null) {
                    location += " in " + psiElement.getResolveScope().getDisplayName();
                }
                if (StringUtils.isNotEmpty(accessTime)) {
                    location += " (" + accessTime + ")";
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
        try {
            return ApplicationManager.getApplication().runReadAction((ThrowableComputable<String, Throwable>) () -> {
                // 获取Swagger的ApiOperation注解中的value属性
                for (PsiAnnotation psiAnnotation : psiMethod.getAnnotations()) {
                    if (!StringUtils.equals(psiAnnotation.getQualifiedName(), SwaggerAnnotationEnum.API_OPERATION.getClassPackage())) {
                        continue;
                    }
                    PsiAnnotationMemberValue psiAnnotationAttributeValue = psiAnnotation.findAttributeValue("value");
                    if (Objects.isNull(psiAnnotationAttributeValue)) {
                        continue;
                    }
                    String valueText = psiAnnotationAttributeValue.getText();
                    return StringUtils.contains(valueText, "\"") ? StringUtils.replace(valueText, "\"",
                            StringUtils.EMPTY).trim() : StringUtils.trim(valueText);
                }
                // 获取JavaDoc中第一行非空注释元素即可
                PsiDocComment docComment = psiMethod.getDocComment();
                if (Objects.nonNull(docComment)) {
                    for (PsiElement descriptionElement : docComment.getDescriptionElements()) {
                        String text = descriptionElement.getText().trim();
                        if (StringUtils.isNotEmpty(text)) {
                            return text;
                        }
                    }
                }
                return StringUtils.EMPTY;
            });
        } catch (Throwable e) {
            return StringUtils.EMPTY;
        }
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

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public PsiElement getPsiElement() {
        return psiElement;
    }
}