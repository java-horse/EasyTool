package easy.restful.scanner.framework;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.impl.scopes.ModuleWithDependenciesScope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import easy.enums.RestfulSearchAnnotationTypeEnum;
import easy.restful.annotation.JaxrsHttpMethodAnnotation;
import easy.restful.api.ApiService;
import easy.restful.api.HttpMethod;
import easy.restful.scanner.IJavaFramework;
import easy.restful.scanner.ScannerApiHelper;
import easy.util.RestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Jaxrs implements IJavaFramework {

    private static final String JAXRS_SERVER = "jaxrs:server";

    private static final Jaxrs INSTANCE = new Jaxrs();

    private Jaxrs() {
        // private
    }

    public static Jaxrs getInstance() {
        return INSTANCE;
    }

    @Override
    public Collection<ApiService> getService(@NotNull Project project, @NotNull Module module) {
        List<ApiService> apiServices = new ArrayList<>();
        for (PsiClassBean psiClassBean : scanHasPathFiles(project, module, null)) {
            apiServices.addAll(getRequestsFromClass(psiClassBean.rootPath, psiClassBean.psiClass));
        }
        return apiServices;
    }

    @Override
    public Collection<ApiService> getService(@NotNull PsiClass psiClass) {
        Project project = psiClass.getProject();
        GlobalSearchScope scope = psiClass.getResolveScope();
        ModuleWithDependenciesScope dependenciesScope = scope instanceof ModuleWithDependenciesScope ?
                ((ModuleWithDependenciesScope) scope) : null;
        if (dependenciesScope != null) {
            List<ApiService> apiServices = new ArrayList<>();
            for (PsiClassBean psiClassBean : scanHasPathFiles(project, dependenciesScope.getModule(), psiClass)) {
                apiServices.addAll(getRequestsFromClass(psiClassBean.rootPath, psiClassBean.psiClass));
            }
            return apiServices;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasRestful(@Nullable PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        if (psiClass.hasAnnotation(RestfulSearchAnnotationTypeEnum.JAVAX_PATH.getQualifiedName())) {
            return true;
        }
        GlobalSearchScope scope = psiClass.getResolveScope();
        ModuleWithDependenciesScope dependenciesScope = scope instanceof ModuleWithDependenciesScope ?
                ((ModuleWithDependenciesScope) scope) : null;
        if (dependenciesScope != null) {
            Project project = psiClass.getProject();
            Module module = dependenciesScope.getModule();
            XmlFile xmlFile = findConfigXmlFile(project, module);
            if (xmlFile == null || xmlFile.getRootTag() == null) {
                return false;
            }
            XmlTag[] tags = xmlFile.getRootTag().getSubTags();
            String qualifiedName = psiClass.getQualifiedName();
            if (qualifiedName == null) {
                return false;
            }
            for (XmlTag xmlTag : tags) {
                if (JAXRS_SERVER.equals(xmlTag.getName())
                        && qualifiedName.equals(xmlTag.getAttributeValue("serviceClass"))) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    private List<PsiClassBean> scanHasPathFiles(@NotNull Project project, @NotNull Module module, @Nullable PsiClass find) {
        Set<PsiClassBean> classSets = new HashSet<>();

        XmlFile applicationContext = findConfigXmlFile(project, module);
        if (applicationContext != null) {
            classSets.addAll(parseApplicationContextXml(project, module, applicationContext));
            if (find != null) {
                for (PsiClassBean next : classSets) {
                    if (next.psiClass.equals(find)) {
                        return Collections.singletonList(next);
                    }
                }
            }
        }
        Collection<PsiAnnotation> pathList = JavaAnnotationIndex.getInstance().get(
                RestfulSearchAnnotationTypeEnum.JAVAX_PATH.getName(),
                project,
                ScannerApiHelper.getModuleScope(module)
        );
        for (PsiAnnotation psiAnnotation : pathList) {
            PsiElement psiElement = psiAnnotation.getParent().getParent();

            if (!(psiElement instanceof PsiClass)) {
                continue;
            }

            PsiClass psiClass = (PsiClass) psiElement;
            PsiClassBean classBean = new PsiClassBean(getRootPathOfClass(psiClass), psiClass);
            if (find != null) {
                if (psiClass.equals(find)) {
                    return Collections.singletonList(classBean);
                }
            }
            classSets.add(classBean);
        }
        return new ArrayList<>(classSets);
    }

    @NotNull
    private List<ApiService> getRequestsFromClass(@Nullable String rootPath, @NotNull PsiClass psiClass) {
        String rootPathOfClass = getRootPathOfClass(psiClass);
        if (rootPathOfClass != null) {
            rootPath = rootPathOfClass;
        }
        List<ApiService> childrenApiServices = new ArrayList<>();
        PsiMethod[] psiMethods = psiClass.getMethods();
        for (PsiMethod psiMethod : psiMethods) {
            String path = "/";
            List<HttpMethod> methods = new ArrayList<>();
            PsiAnnotation[] annotations = RestUtil.getMethodAnnotations(psiMethod).toArray(new PsiAnnotation[0]);
            for (PsiAnnotation annotation : annotations) {
                RestfulSearchAnnotationTypeEnum searchAnnotationTypeEnum = RestfulSearchAnnotationTypeEnum.getByQualifiedName(annotation.getQualifiedName());
                if (searchAnnotationTypeEnum != null) {
                    List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
                    Object value = RestUtil.getAttributeValue(attributes.get(0).getAttributeValue());
                    if (value != null) {
                        path = (String) value;
                    }
                }
                JaxrsHttpMethodAnnotation jaxrs = JaxrsHttpMethodAnnotation.getByQualifiedName(annotation.getQualifiedName());
                if (jaxrs != null) {
                    methods.add(jaxrs.getMethod());
                }
            }
            for (HttpMethod method : methods) {
                String tempPath = path;
                if (!tempPath.startsWith("/")) {
                    tempPath = "/" + tempPath;
                }
                if (rootPath != null) {
                    tempPath = rootPath + tempPath;
                    if (!tempPath.startsWith("/")) {
                        tempPath = "/" + tempPath;
                    }
                    tempPath = tempPath.replaceAll("//", "/");
                }
                ApiService apiService = new ApiService(method, tempPath, psiMethod);
                childrenApiServices.add(apiService);
            }
        }
        return childrenApiServices;
    }

    @Nullable
    private String getRootPathOfClass(@NotNull PsiClass psiClass) {
        PsiAnnotation psiAnnotation = RestUtil.getClassAnnotation(
                psiClass,
                RestfulSearchAnnotationTypeEnum.JAVAX_PATH.getQualifiedName()
        );
        if (psiAnnotation != null) {
            return (String) RestUtil.getAttributeValue(psiAnnotation.getAttributes().get(0).getAttributeValue());
        }
        return null;
    }

    /**
     * 查找xml配置文件
     *
     * @param project project
     * @param module  module
     * @return xmlFile
     */
    @Nullable
    private XmlFile findConfigXmlFile(@NotNull Project project, @NotNull Module module) {
        Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName("applicationContext.xml", module.getModuleScope());
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof XmlFile psiFile1) {
                return psiFile1;
            }
        }
        return null;
    }

    @NotNull
    private List<PsiClassBean> parseApplicationContextXml(@NotNull Project project, @NotNull Module module,
                                                          @NotNull XmlFile applicationContext) {
        List<PsiClassBean> list = new ArrayList<>();
        if (applicationContext.getRootTag() == null) {
            return Collections.emptyList();
        }
        for (XmlTag xmlTag : applicationContext.getRootTag().getSubTags()) {
            if (!"jaxrs:server".equals(xmlTag.getName())) {
                continue;
            }
            String rootPath = xmlTag.getAttributeValue("address");
            String serviceClass = xmlTag.getAttributeValue("serviceClass");
            if (serviceClass == null) {
                continue;
            }
            serviceClass = serviceClass.substring(serviceClass.lastIndexOf(".") + 1);
            Collection<PsiClass> psiClasses = JavaShortClassNameIndex.getInstance().get(serviceClass, project, module.getModuleScope());
            for (PsiClass psiClass : psiClasses) {
                list.add(new PsiClassBean(rootPath, psiClass));
            }
        }
        return list;
    }

    @Override
    public boolean isRestfulProject(@NotNull final Project project, @NotNull final Module module) {
        try {
            JavaAnnotationIndex instance = JavaAnnotationIndex.getInstance();
            Collection<PsiAnnotation> collection = instance.get(RestfulSearchAnnotationTypeEnum.JAVAX_PATH.getName(), project, module.getModuleScope());
            if (collection != null && !collection.isEmpty()) {
                for (PsiAnnotation annotation : collection) {
                    if (annotation == null) {
                        continue;
                    }
                    if (RestfulSearchAnnotationTypeEnum.JAVAX_PATH.getQualifiedName().equals(annotation.getQualifiedName())) {
                        return true;
                    }
                }
            }
            XmlFile xmlFile = findConfigXmlFile(project, module);
            if (xmlFile == null || xmlFile.getRootTag() == null) {
                return false;
            }
            XmlTag[] tags = xmlFile.getRootTag().findSubTags("jaxrs:server");
            if (tags.length > 0) {
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    private static class PsiClassBean {

        private String rootPath;

        private PsiClass psiClass;

        public PsiClassBean(String rootPath, PsiClass psiClass) {
            this.rootPath = rootPath;
            this.psiClass = psiClass;
        }

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

        public PsiClass getPsiClass() {
            return psiClass;
        }

        public void setPsiClass(PsiClass psiClass) {
            this.psiClass = psiClass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PsiClassBean that = (PsiClassBean) o;
            String name = psiClass.getName();
            if (name == null) {
                return false;
            }
            return name.equals(that.psiClass.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(psiClass.getName());
        }
    }
}
