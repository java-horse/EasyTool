package easy.handler;

import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import easy.enums.ProjectConfigFileNameEnum;
import easy.enums.RequestAnnotationEnum;
import easy.enums.SpringAnnotationEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.Collection;
import java.util.Map;
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

    private static final String KEY_ACTIVE = "spring.profiles.active";

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
        PsiAnnotation controllerAnnotation = modifierList.findAnnotation(SpringAnnotationEnum.CONTROLLER_ANNOTATION.getName());
        if (Objects.isNull(controllerAnnotation)) {
            controllerAnnotation = modifierList.findAnnotation(SpringAnnotationEnum.REST_CONTROLLER_ANNOTATION.getName());
            if (Objects.isNull(controllerAnnotation)) {
                controllerAnnotation = modifierList.findAnnotation(SpringAnnotationEnum.FEIGN_CLIENT_ANNOTATION.getName());
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
     * 删除引号、没有/开头自动添加
     *
     * @param url
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/7 13:59
     */
    private String removeQuotation(String url) {
        String replaceUrl = StringUtils.replace(url, "\"", StringUtils.EMPTY);
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
        Module module = ModuleUtil.findModuleForFile(psiElement.getContainingFile());
        return "http://localhost:" + getProjectServerPort(project, module) + doCopyFullUrl(psiElement);
    }


    /**
     * 获取项目服务器端口
     *
     * @param project 项目
     * @param module  模块
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/26 13:56
     */
    public String getProjectServerPort(Project project, Module module) {
        ProjectConfigFileNameEnum[] configFileNameEnums = ProjectConfigFileNameEnum.values();
        for (ProjectConfigFileNameEnum configFileNameEnum : configFileNameEnums) {
            System.out.println(configFileNameEnum.getName() + configFileNameEnum.getSuffix());
            String port = getPortByConfigFile(project, module, configFileNameEnum.getName(), configFileNameEnum.getPortKey(), configFileNameEnum.getSuffix(), false);
            System.out.println("port=" + port);
            if (StringUtils.isNotBlank(port)) {
                return port;
            }
        }
        return "8080";
    }

    /**
     * 通过配置文件获取端口
     * 配置文件检索优先级: properties->yml
     *
     * @param project 项目
     * @param module  模块
     * @param name    名字
     * @param portKey 港口的关键
     * @param suffix  后缀
     * @param last    最后的
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/26 13:55
     */
    private String getPortByConfigFile(Project project, Module module, String name, String portKey, String suffix, boolean last) {
        Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(name + suffix, GlobalSearchScope.moduleScope(module));
        if (virtualFiles.isEmpty()) {
            return null;
        }
        if (StringUtils.endsWith(suffix, ProjectConfigFileNameEnum.APPLICATION_PROPERTIES.getSuffix())) {
            PropertiesFile propertiesFile = virtualFiles.stream().map(virtualFile -> PsiManager.getInstance(project).findFile(virtualFile))
                    .filter(psiFile -> psiFile != null && StringUtils.endsWith(psiFile.getParent().getName(), "resources")
                            && psiFile instanceof PropertiesFile)
                    .map(psiFile -> (PropertiesFile) psiFile).findFirst().orElse(null);
            if (propertiesFile == null) {
                return null;
            }
            Map<String, String> propertiesFileNamesMap = propertiesFile.getNamesMap();
            String active = propertiesFileNamesMap.get(KEY_ACTIVE);
            if (StringUtils.isNotBlank(active) && !last) {
                String lastFileName = String.format("%s-%s", name, active);
                String port = getPortByConfigFile(project, module, lastFileName, portKey, suffix, true);
                if (StringUtils.isNotBlank(port)) {
                    return port;
                }
            }
            return propertiesFileNamesMap.get(portKey);
        } else if (StringUtils.endsWithAny(suffix, ProjectConfigFileNameEnum.APPLICATION_YAML.getSuffix(), ProjectConfigFileNameEnum.APPLICATION_YML.getSuffix())) {
            YAMLFile yamlFile = virtualFiles.stream().map(virtualFile -> PsiManager.getInstance(project).findFile(virtualFile))
                    .filter(psiFile -> psiFile != null && StringUtils.endsWith(psiFile.getParent().getName(), "resources")
                            && psiFile instanceof YAMLFile)
                    .map(psiFile -> (YAMLFile) psiFile).findFirst().orElse(null);
            if (yamlFile == null) {
                return null;
            }
            String active = getYamlValue(yamlFile, KEY_ACTIVE);
            System.out.println("active=" + active);
            if (StringUtils.isNotBlank(active) && !last) {
                String lastFileName = String.format("%s-%s", name, active);
                String port = getPortByConfigFile(project, module, lastFileName, portKey, suffix, true);
                if (StringUtils.isNotBlank(port)) {
                    return port;
                }
            }
            return getYamlValue(yamlFile, portKey);
        }
        return null;
    }

    /**
     * 获取yaml执行key值
     *
     * @param yamlFile yaml文件
     * @param key      关键
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/26 14:13
     */
    private String getYamlValue(YAMLFile yamlFile, String key) {
        YAMLKeyValue qualifiedKeyInFile = YAMLUtil.getQualifiedKeyInFile(yamlFile, key.split("\\."));
        if (qualifiedKeyInFile != null) {
            return qualifiedKeyInFile.getValueText();
        }
        return null;
    }

}
