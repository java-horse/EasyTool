package easy.restful.scanner.framework;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import easy.enums.RestfulSearchAnnotationTypeEnum;
import easy.restful.annotation.RoseHttpMethodAnnotation;
import easy.restful.api.ApiService;
import easy.restful.api.HttpMethod;
import easy.restful.scanner.IJavaFramework;
import easy.restful.scanner.ScannerApiHelper;
import easy.util.RestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Rose implements IJavaFramework {

    private static final Rose INSTANCE = new Rose();

    private Rose() {
    }

    public static Rose getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isRestfulProject(@NotNull final Project project, @NotNull final Module module) {
        try {
            JavaAnnotationIndex instance = JavaAnnotationIndex.getInstance();
            Set<PsiAnnotation> annotations = new HashSet<>(instance.get(RestfulSearchAnnotationTypeEnum.ROSE_PATH.getName(), project, module.getModuleScope()));
            if (!annotations.isEmpty()) {
                for (PsiAnnotation annotation : annotations) {
                    if (annotation == null) {
                        continue;
                    }
                    if (RestfulSearchAnnotationTypeEnum.ROSE_PATH.getQualifiedName().equals(annotation.getQualifiedName())) {
                        return true;
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public Collection<ApiService> getService(@NotNull Project project, @NotNull Module module) {
        List<ApiService> moduleList = new ArrayList<>(0);
        List<PsiClass> controllers = getAllControllerClass(project, module);
        if (controllers.isEmpty()) {
            return moduleList;
        }
        for (PsiClass controllerClass : controllers) {
            moduleList.addAll(getService(controllerClass));
        }
        return moduleList;
    }

    @Override
    public Collection<ApiService> getService(@NotNull PsiClass psiClass) {
        List<ApiService> requests = new ArrayList<>();
        List<ApiService> parentRequests = new ArrayList<>();
        List<ApiService> childrenRequests = new ArrayList<>();

        PsiAnnotation psiAnnotation = RestUtil.getClassAnnotation(
                psiClass,
                RoseHttpMethodAnnotation.PATH.getQualifiedName(),
                RoseHttpMethodAnnotation.PATH.getShortName()
        );
        if (psiAnnotation != null) {
            parentRequests = getRequests(psiAnnotation, null);
        }

        PsiMethod[] psiMethods = psiClass.getAllMethods();
        for (PsiMethod psiMethod : psiMethods) {
            childrenRequests.addAll(getRequests(psiMethod));
        }
        if (parentRequests.isEmpty()) {
            requests.addAll(childrenRequests);
        } else {
            parentRequests.forEach(parentRequest -> childrenRequests.forEach(childrenRequest -> {
                requests.add(childrenRequest.copyWithParent(parentRequest));
            }));
        }
        return requests;
    }

    @Override
    public boolean hasRestful(@Nullable PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        return psiClass.hasAnnotation(RestfulSearchAnnotationTypeEnum.ROSE_PATH.getQualifiedName());
    }

    /**
     * 获取所有的控制器类
     *
     * @param project project
     * @param module  module
     * @return Collection<PsiClass>
     */
    @NotNull
    private List<PsiClass> getAllControllerClass(@NotNull Project project, @NotNull Module module) {
        List<PsiClass> allControllerClass = new ArrayList<>();
        GlobalSearchScope moduleScope = ScannerApiHelper.getModuleScope(module);
        Collection<PsiAnnotation> pathList = JavaAnnotationIndex.getInstance().get(RestfulSearchAnnotationTypeEnum.ROSE_PATH.getName(), project, moduleScope);
        for (PsiAnnotation psiAnnotation : pathList) {
            PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
            PsiElement psiElement = psiModifierList.getParent();

            if (!(psiElement instanceof PsiClass psiClass)) {
                continue;
            }
            allControllerClass.add(psiClass);
        }
        return allControllerClass;
    }

    /**
     * 获取注解中的参数，生成RequestBean
     *
     * @param annotation annotation
     * @return list
     * @see Rose#getRequests(PsiMethod)
     */
    @NotNull
    private List<ApiService> getRequests(@NotNull PsiAnnotation annotation, @Nullable PsiMethod psiMethod) {
        RoseHttpMethodAnnotation rose = RoseHttpMethodAnnotation.getByQualifiedName(
                annotation.getQualifiedName()
        );
        if (annotation.getResolveScope().isSearchInLibraries()) {
            rose = RoseHttpMethodAnnotation.getByShortName(annotation.getQualifiedName());
        }
        Set<HttpMethod> methods = new HashSet<>();
        List<String> paths = new ArrayList<>();
        if (rose != null) {
            methods.add(rose.getMethod());
        }

        // 是否为隐式的path（未定义value或者path）
        boolean hasImplicitPath = true;
        List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
        for (JvmAnnotationAttribute attribute : attributes) {
            String name = attribute.getAttributeName();

            if (methods.contains(HttpMethod.REQUEST) && "method".equals(name)) {
                // method可能为数组
                Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
                if (value instanceof String) {
                    methods.add(HttpMethod.parse(value));
                } else if (value instanceof List) {
                    //noinspection unchecked,rawtypes
                    List<String> list = (List) value;
                    for (String item : list) {
                        if (item != null) {
                            item = item.substring(item.lastIndexOf(".") + 1);
                            methods.add(HttpMethod.parse(item));
                        }
                    }
                }
            }

            boolean flag = false;
            for (String path : new String[]{"value", "path"}) {
                if (path.equals(name)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                continue;
            }
            Object value = RestUtil.getAttributeValue(attribute.getAttributeValue());
            if (value instanceof String) {
                paths.add(ScannerApiHelper.formatPath(value));
            } else if (value instanceof List) {
                List<Object> list = (List) value;
                list.forEach(item -> paths.add(ScannerApiHelper.formatPath(item)));
            } else {
                throw new IllegalArgumentException(String.format(
                        "Scan api: %s\n" + "Class: %s",
                        value,
                        value != null ? value.getClass() : null
                ));
            }
            hasImplicitPath = false;
        }
        if (hasImplicitPath && psiMethod != null) {
            paths.add("/");
        }
        List<ApiService> requests = new ArrayList<>(paths.size());
        paths.forEach(path -> {
            for (HttpMethod method : methods) {
                if (method.equals(HttpMethod.REQUEST) && methods.size() > 1) {
                    continue;
                }
                requests.add(new ApiService(
                        method,
                        path,
                        psiMethod
                ));
            }
        });
        return requests;
    }

    /**
     * 获取方法中的参数请求，生成RequestBean
     *
     * @param method Psi方法
     * @return list
     */
    @NotNull
    private List<ApiService> getRequests(@NotNull PsiMethod method) {
        List<ApiService> requests = new ArrayList<>();
        for (PsiAnnotation annotation : RestUtil.getMethodAnnotations(method)) {
            requests.addAll(getRequests(annotation, method));
        }
        return requests;
    }
    
}
