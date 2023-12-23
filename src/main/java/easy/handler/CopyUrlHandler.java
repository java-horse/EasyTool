package easy.handler;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import easy.base.Constants;
import easy.enums.RequestAnnotationEnum;
import easy.util.NetUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 复制全量的请求地址URL处理
 *
 * @project: EasyChar
 * @package: easy.handler
 * @author: mabin
 * @date: 2023/11/07 11:52:21
 */
public class CopyUrlHandler {

    private static final Logger log = Logger.getInstance(CopyUrlHandler.class);

    /**
     * 复制URL
     *
     * @param psiElement
     * @return void
     * @author mabin
     * @date 2023/11/7 13:38
     */
    public String doCopyFullUrl(PsiElement psiElement) {
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
        StringBuilder url = new StringBuilder();
        if (Objects.nonNull(psiClass)) {
            String classUrl = getClassRestUrl(psiClass);
            if (StringUtils.isNotBlank(classUrl)) {
                url.append(removeQuotation(classUrl));
            }
        }
        PsiAnnotation psiAnnotation = PsiTreeUtil.getParentOfType(psiElement, PsiAnnotation.class);
        if (Objects.nonNull(psiAnnotation) && Objects.nonNull(RequestAnnotationEnum.getEnumByQualifiedName(psiAnnotation.getQualifiedName()))) {
            PsiElement element = psiAnnotation.getParent();
            if (Objects.nonNull(element) && element.getParent() instanceof PsiMethod) {
                String methodUrl = getUrl(psiAnnotation);
                if (StringUtils.isNotBlank(methodUrl)) {
                    url.append(removeQuotation(methodUrl));
                }
            }
        }
        return url.toString();
    }

    /**
     * 获取Controller类的接口路径
     *
     * @param psiClass
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/7 13:41
     */
    private String getClassRestUrl(PsiClass psiClass) {
        if (Objects.isNull(psiClass)) {
            return StringUtils.EMPTY;
        }
        PsiModifierList modifierList = psiClass.getModifierList();
        if (Objects.isNull(modifierList)) {
            return StringUtils.EMPTY;
        }
        PsiAnnotation controllerAnnotation = modifierList.findAnnotation(Constants.SPRING_ANNOTATION.CONTROLLER_ANNOTATION);
        if (Objects.isNull(controllerAnnotation)) {
            controllerAnnotation = modifierList.findAnnotation(Constants.SPRING_ANNOTATION.REST_CONTROLLER_ANNOTATION);
            if (Objects.isNull(controllerAnnotation)) {
                controllerAnnotation = modifierList.findAnnotation(Constants.SPRING_ANNOTATION.FEIGN_CLIENT_ANNOTATION);
            }
        }
        if (Objects.isNull(controllerAnnotation)) {
            return StringUtils.EMPTY;
        }
        PsiAnnotation requestAnnotation = modifierList.findAnnotation(RequestAnnotationEnum.REQUEST_MAPPING.getQualifiedName());
        if (Objects.isNull(requestAnnotation)) {
            requestAnnotation = modifierList.findAnnotation(RequestAnnotationEnum.FEIGN_CLIENT.getQualifiedName());
        }
        if (Objects.isNull(requestAnnotation)) {
            return StringUtils.EMPTY;
        }
        return getUrl(requestAnnotation);
    }

    /**
     * 获取方法的接口路径
     *
     * @param psiAnnotation
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/7 13:58
     */
    private String getUrl(PsiAnnotation psiAnnotation) {
        String url = StringUtils.EMPTY;
        if (Objects.isNull(psiAnnotation)) {
            return url;
        }
        PsiAnnotationMemberValue pathMember = psiAnnotation.findAttributeValue("path");
        if (Objects.nonNull(pathMember) && Objects.nonNull(pathMember.getNode())) {
            url = pathMember.getNode().getText();
        } else {
            PsiAnnotationMemberValue valueMember = psiAnnotation.findAttributeValue("value");
            if (Objects.nonNull(valueMember) && Objects.nonNull(valueMember.getNode())) {
                url = valueMember.getNode().getText();
            }
        }
        if (StringUtils.isNotBlank(url) && StringUtils.contains(url, ",")) {
            url = StringUtils.split(url, ",")[0];
        }
        return url;
    }

    /**
     * 删除引号、大括号，没有/开头自动添加
     *
     * @param url
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/7 13:59
     */
    private String removeQuotation(String url) {
        String replaceUrl = StringUtils.replace(url, "\"", StringUtils.EMPTY)
                .replace("{", StringUtils.EMPTY)
                .replace("}", StringUtils.EMPTY);
        return StringUtils.startsWith(replaceUrl, "/") ? replaceUrl : replaceUrl + "/";
    }

    /**
     * 复制Http Url
     *
     * @param project
     * @param psiElement
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/23 11:46
     */
    public String doCopyHttpUrl(Project project, PsiElement psiElement) {
        if (Objects.isNull(project) || Objects.isNull(psiElement)) {
            return StringUtils.EMPTY;
        }
        String port = NetUtil.getPortByConfigFile(project, psiElement.getContainingFile());
        return "http://localhost:" + (StringUtils.isBlank(port) ? "8080" : port) + doCopyFullUrl(psiElement);
    }


}
