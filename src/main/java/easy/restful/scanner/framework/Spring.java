package easy.restful.scanner.framework;

import com.intellij.lang.jvm.annotation.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import easy.enums.RestfulSearchAnnotationTypeEnum;
import easy.restful.annotation.SpringHttpMethodAnnotation;
import easy.restful.api.ApiService;
import easy.restful.api.HttpMethod;
import easy.restful.scanner.IJavaFramework;
import easy.util.RestUtil;
import easy.util.SystemUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Spring implements IJavaFramework {

    private static final Spring INSTANCE = new Spring();

    private Spring() {
    }

    public static Spring getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isRestfulProject(@NotNull Project project, @NotNull Module module) {
        try {
            JavaAnnotationIndex instance = JavaAnnotationIndex.getInstance();
            Set<PsiAnnotation> annotations = new HashSet<>(instance.get(RestfulSearchAnnotationTypeEnum.CONTROLLER.getName(), project, module.getModuleScope()));
            if (!annotations.isEmpty()) {
                for (PsiAnnotation annotation : annotations) {
                    if (annotation == null) {
                        continue;
                    }
                    if (RestfulSearchAnnotationTypeEnum.CONTROLLER.getQualifiedName().equals(annotation.getQualifiedName())) {
                        return true;
                    }
                }
            }
            annotations.clear();
            annotations.addAll(instance.get(RestfulSearchAnnotationTypeEnum.REST_CONTROLLER.getName(), project, module.getModuleScope()));
            if (!annotations.isEmpty()) {
                for (PsiAnnotation annotation : annotations) {
                    if (annotation == null) {
                        continue;
                    }
                    if (RestfulSearchAnnotationTypeEnum.REST_CONTROLLER.getQualifiedName().equals(annotation.getQualifiedName())) {
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
        List<ApiService> moduleList = new ArrayList<>();
        List<PsiClass> controllers = getAllControllerClass(project, module);
        if (CollectionUtils.isEmpty(controllers)) {
            return moduleList;
        }
        for (PsiClass controllerClass : controllers) {
            moduleList.addAll(getService(controllerClass));
        }
        return moduleList;
    }

    @Override
    public Collection<ApiService> getService(@NotNull PsiClass psiClass) {
        List<ApiService> apiServices = new ArrayList<>();
        List<ApiService> parentApiServices = new ArrayList<>();
        List<ApiService> childrenApiServices = new ArrayList<>();

        PsiAnnotation psiAnnotation = RestUtil.getClassAnnotation(
                psiClass,
                SpringHttpMethodAnnotation.REQUEST_MAPPING.getQualifiedName(),
                SpringHttpMethodAnnotation.REQUEST_MAPPING.getShortName()
        );
        if (psiAnnotation != null) {
            parentApiServices = getRequests(psiAnnotation, null);
        }

        PsiMethod[] psiMethods = psiClass.getAllMethods();
        for (PsiMethod psiMethod : psiMethods) {
            childrenApiServices.addAll(getRequests(psiMethod));
        }
        if (parentApiServices.isEmpty()) {
            apiServices.addAll(childrenApiServices);
        } else {
            parentApiServices.forEach(parentRequest -> childrenApiServices.forEach(childrenRequest -> {
                ApiService apiService = childrenRequest.copyWithParent(parentRequest);
                apiServices.add(apiService);
            }));
        }
        return apiServices;
    }

    @Override
    public boolean hasRestful(@Nullable PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        return psiClass.hasAnnotation(RestfulSearchAnnotationTypeEnum.CONTROLLER.getQualifiedName())
                || psiClass.hasAnnotation(RestfulSearchAnnotationTypeEnum.REST_CONTROLLER.getQualifiedName());
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
        GlobalSearchScope moduleScope = SystemUtil.getModuleScope(module);
        Collection<PsiAnnotation> pathList = JavaAnnotationIndex.getInstance().get(RestfulSearchAnnotationTypeEnum.CONTROLLER.getName(), project, moduleScope);
        pathList.addAll(JavaAnnotationIndex.getInstance().get(RestfulSearchAnnotationTypeEnum.REST_CONTROLLER.getName(), project, moduleScope));
        for (PsiAnnotation psiAnnotation : pathList) {
            PsiElement psiElement = psiAnnotation.getParent().getParent();
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
     * @see Spring#getRequests(PsiMethod)
     */
    @NotNull
    private List<ApiService> getRequests(@NotNull PsiAnnotation annotation, @Nullable PsiMethod psiMethod) {
        SpringHttpMethodAnnotation spring = SpringHttpMethodAnnotation.getByQualifiedName(annotation.getQualifiedName());
        if (annotation.getResolveScope().isSearchInLibraries()) {
            spring = SpringHttpMethodAnnotation.getByShortName(annotation.getQualifiedName());
        }
        Set<HttpMethod> methods = new HashSet<>();
        List<String> paths = new ArrayList<>();
        CustomRefAnnotation refAnnotation = null;
        if (spring == null) {
            refAnnotation = findCustomAnnotation(annotation);
            if (refAnnotation == null) {
                return Collections.emptyList();
            }
            methods.addAll(refAnnotation.getMethods());
        } else {
            methods.add(spring.getMethod());
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
                paths.add(SystemUtil.formatPath(value));
            } else if (value instanceof List) {
                //noinspection unchecked,rawtypes
                List<Object> list = (List) value;
                list.forEach(item -> paths.add(SystemUtil.formatPath(item)));
            } else {
                throw new IllegalArgumentException(String.format(
                        "Scan api: %s\n" +
                                "Class: %s",
                        value,
                        value != null ? value.getClass() : null
                ));
            }
            hasImplicitPath = false;
        }
        if (hasImplicitPath && psiMethod != null) {
            List<String> loopPaths;
            if (refAnnotation != null && !(loopPaths = refAnnotation.getPaths()).isEmpty()) {
                paths.addAll(loopPaths);
            } else {
                paths.add("/");
            }
        }

        List<ApiService> apiServices = new ArrayList<>(paths.size());

        paths.forEach(path -> {
            for (HttpMethod method : methods) {
                if (method.equals(HttpMethod.REQUEST) && methods.size() > 1) {
                    continue;
                }
                apiServices.add(new ApiService(
                        method,
                        path,
                        psiMethod
                ));
            }
        });
        return apiServices;
    }

    /**
     * 获取方法中的参数请求，生成RequestBean
     *
     * @param method Psi方法
     * @return list
     */
    @NotNull
    private List<ApiService> getRequests(@NotNull PsiMethod method) {
        List<ApiService> apiServices = new ArrayList<>();
        for (PsiAnnotation annotation : RestUtil.getMethodAnnotations(method)) {
            apiServices.addAll(getRequests(annotation, method));
        }

        return apiServices;
    }

    @Nullable
    private CustomRefAnnotation findCustomAnnotation(@NotNull PsiAnnotation psiAnnotation) {
        PsiAnnotation qualifiedAnnotation = RestUtil.getQualifiedAnnotation(
                psiAnnotation,
                SpringHttpMethodAnnotation.REQUEST_MAPPING.getQualifiedName()
        );
        if (qualifiedAnnotation == null) {
            return null;
        }
        CustomRefAnnotation otherAnnotation = new CustomRefAnnotation();
        for (JvmAnnotationAttribute attribute : qualifiedAnnotation.getAttributes()) {
            Object methodValues = getAnnotationValue(attribute, "method");
            if (methodValues != null) {
                List<?> methods = methodValues instanceof List ? ((List<?>) methodValues) : Collections.singletonList(methodValues);
                if (methods.isEmpty()) {
                    continue;
                }
                for (Object method : methods) {
                    if (method == null) {
                        continue;
                    }
                    otherAnnotation.addMethods(HttpMethod.parse(method));
                }
                continue;
            }
            Object pathValues = getAnnotationValue(attribute, "path", "value");
            if (pathValues != null) {
                List<?> paths = pathValues instanceof List ? ((List<?>) pathValues) : Collections.singletonList(pathValues);
                if (!paths.isEmpty()) {
                    for (Object path : paths) {
                        if (path == null) {
                            continue;
                        }
                        otherAnnotation.addPath((String) path);
                    }
                }
            }
        }
        return otherAnnotation;
    }

    @Nullable
    private Object getAnnotationValue(@NotNull JvmAnnotationAttribute attribute, @NotNull String... attrNames) {
        String attributeName = attribute.getAttributeName();
        if (attrNames.length < 1) {
            return null;
        }
        boolean matchAttrName = false;
        for (String attrName : attrNames) {
            if (attributeName.equals(attrName)) {
                matchAttrName = true;
                break;
            }
        }
        if (!matchAttrName) {
            return null;
        }
        JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
        return getAttributeValue(attributeValue);
    }

    private Object getAttributeValue(@Nullable JvmAnnotationAttributeValue attributeValue) {
        if (attributeValue == null) {
            return null;
        }
        if (attributeValue instanceof JvmAnnotationConstantValue) {
            Object constantValue = ((JvmAnnotationConstantValue) attributeValue).getConstantValue();
            return constantValue == null ? null : constantValue.toString();
        } else if (attributeValue instanceof JvmAnnotationEnumFieldValue) {
            return ((JvmAnnotationEnumFieldValue) attributeValue).getFieldName();
        } else if (attributeValue instanceof JvmAnnotationArrayValue) {
            List<String> values = new ArrayList<>();
            for (JvmAnnotationAttributeValue value : ((JvmAnnotationArrayValue) attributeValue).getValues()) {
                values.add((String) getAttributeValue(value));
            }
            return values;
        }
        return null;
    }

    private static class CustomRefAnnotation {

        private final List<String> paths;
        private final List<HttpMethod> methods;

        public CustomRefAnnotation() {
            this.paths = new ArrayList<>();
            this.methods = new ArrayList<>();
        }

        public void addPath(@NotNull String... paths) {
            if (paths.length < 1) {
                return;
            }
            this.paths.addAll(Arrays.asList(paths));
        }

        public void addMethods(@NotNull HttpMethod... methods) {
            if (methods.length < 1) {
                return;
            }
            this.methods.addAll(Arrays.asList(methods));
        }

        public List<String> getPaths() {
            return paths;
        }

        public List<HttpMethod> getMethods() {
            return methods;
        }
    }
}
