package easy.restful.scanner;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import easy.restful.annotation.SpringHttpMethodAnnotation;
import easy.restful.api.ApiService;
import easy.restful.api.HttpMethod;
import easy.restful.util.ServiceStub;
import easy.restful.util.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.idea.stubindex.KotlinAnnotationsIndex;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.*;

import java.util.*;

public class KotlinUtil {

    public final Project project;
    public final Module module;

    private KotlinUtil(@NotNull Module module) {
        project = module.getProject();
        this.module = module;
    }

    public static KotlinUtil create(@NotNull Module module) {
        return new KotlinUtil(module);
    }

    @NotNull
    public static List<ApiService> getKotlinRequests(@NotNull Project project, @NotNull Module module, @NotNull Qualified[] qualified) {
        List<ApiService> ktApiServices = new ArrayList<>();
        KotlinUtil kotlinUtil = create(module);
        List<KtClass> kotlinClasses = kotlinUtil.getRestfulKotlinClasses(qualified, Storage.scanServiceWithLibrary(project));
        for (KtClass kotlinClass : kotlinClasses) {
            ServiceStub clsStub = null;
            for (KtAnnotationEntry annotationEntry : kotlinClass.getAnnotationEntries()) {
                if ((clsStub = getServiceStub(annotationEntry)) != null) {
                    break;
                }
            }

            List<ServiceStub> stubs = new ArrayList<>();
            for (KtNamedFunction function : kotlinUtil.getFunctionsOnKtClass(kotlinClass)) {
                for (KtAnnotationEntry annotationEntry : function.getAnnotationEntries()) {
                    ServiceStub serviceStub = getServiceStub(annotationEntry);
                    if (serviceStub == null) {
                        continue;
                    }
                    serviceStub.setPsiElement(function);
                    stubs.add(serviceStub);
                }
            }

            List<String> parentPaths = new ArrayList<>();
            List<HttpMethod> parentMethods = new ArrayList<>();
            List<ApiService> children = new ArrayList<>();
            if (clsStub != null) {
                parentPaths.addAll(clsStub.getPaths());
                parentMethods.addAll(clsStub.getMethods());
            }
            for (ServiceStub stub : stubs) {
                for (HttpMethod method : stub.getMethods()) {
                    for (String path : stub.getPaths()) {
                        children.add(new ApiService(method, path, stub.getPsiElement()));
                    }
                }
            }

            if (parentPaths.isEmpty()) {
                ktApiServices.addAll(children);
            } else {
                parentPaths.forEach(parentPath -> children.forEach(childrenRequest -> {
                    if (childrenRequest.getMethod() != null && childrenRequest.getMethod() != HttpMethod.REQUEST) {
                        ApiService apiService = childrenRequest.copyWithParent(
                                new ApiService(null, parentPath, null)
                        );
                        ktApiServices.add(apiService);
                    } else {
                        for (HttpMethod parentMethod : parentMethods) {
                            ApiService apiService = childrenRequest.copyWithParent(
                                    new ApiService(parentMethod, parentPath, null)
                            );
                            ktApiServices.add(apiService);
                        }
                    }
                }));
            }
        }
        return ktApiServices;
    }

    /**
     * 根据Kotlin的注解获取ServiceStub
     *
     * @param ktAnnotationEntry Kotlin注解
     * @return ServiceStub
     * @see ServiceStub
     */
    @Nullable
    public static ServiceStub getServiceStub(@NotNull KtAnnotationEntry ktAnnotationEntry) {
        Name shortName = ktAnnotationEntry.getShortName();
        if (shortName == null) {
            return null;
        }
        String annotationName = shortName.asString();
        if (annotationName.contains(".")) {
            // 去掉 package
            annotationName = annotationName.substring(annotationName.lastIndexOf(".") + 1);
        }
        SpringHttpMethodAnnotation springHttpMethodAnnotation;
        if ((springHttpMethodAnnotation = SpringHttpMethodAnnotation.getByShortName(annotationName)) == null) {
            return null;
        }

        ServiceStub serviceStub = new ServiceStub(ktAnnotationEntry);
        serviceStub.method(springHttpMethodAnnotation.getMethod());

        List<? extends ValueArgument> valueArguments = ktAnnotationEntry.getValueArguments();
        if (valueArguments.isEmpty()) {
            // 未设置注解的 value：@GetMapping
            serviceStub.path(null);
        } else {
            for (ValueArgument valueArgument : valueArguments) {
                if (!(valueArgument instanceof KtValueArgument)) {
                    continue;
                }
                KtValueArgument ktValueArgument = (KtValueArgument) valueArgument;
                if (ktValueArgument.isNamed()) {
                    KtValueArgumentName argumentName = ktValueArgument.getArgumentName();
                    if (argumentName == null) {
                        continue;
                    }
                    String name = argumentName.getAsName().asString();
                    if ("method".equals(name)) {
                        KtExpression argumentExpression = ktValueArgument.getArgumentExpression();
                        if (argumentExpression instanceof KtCollectionLiteralExpression) {
                            KtCollectionLiteralExpression expression = (KtCollectionLiteralExpression) argumentExpression;
                            for (PsiElement child : expression.getChildren()) {
                                if (!(child instanceof KtDotQualifiedExpression)) {
                                    continue;
                                }
                                KtDotQualifiedExpression qualifiedExpression = (KtDotQualifiedExpression) child;
                                if (qualifiedExpression.getChildren().length != 2) {
                                    continue;
                                }
                                if (!(qualifiedExpression.getChildren()[0] instanceof KtNameReferenceExpression)
                                        || !(qualifiedExpression.getChildren()[1] instanceof KtNameReferenceExpression)) {
                                    continue;
                                }
                                KtNameReferenceExpression referenceExpression = (KtNameReferenceExpression) qualifiedExpression.getChildren()[1];
                                // 获取到当前注解的 HttpMethods 的其中一个(GET|POST|...)
                                final String currHttpMethod = referenceExpression.getText();
                                serviceStub.method(HttpMethod.parse(currHttpMethod));
                            }
                        }
                    }
                    if (!("path".equals(name) || "value".equals(name))) {
                        continue;
                    }
                }
                KtExpression expression = ktValueArgument.getArgumentExpression();
                if (expression instanceof KtStringTemplateExpression) {
                    KtStringTemplateExpression stringTemplateExpression = (KtStringTemplateExpression) expression;
                    final String currPath = stringTemplateExpression.getChildren()[0].getText();
                    // 当前 paths 的其中一个（@RequestMapping(path=["path1", "path2"])）
                    serviceStub.path(currPath);
                    continue;
                }
                if (!(expression instanceof KtCollectionLiteralExpression)) {
                    continue;
                }
                KtCollectionLiteralExpression collectionLiteralExpression = (KtCollectionLiteralExpression) expression;
                if (collectionLiteralExpression.getChildren().length == 0) {
                    // 未设置注解的 value：@GetMapping
                    serviceStub.path(null);
                } else {
                    for (PsiElement psiElement : collectionLiteralExpression.getChildren()) {
                        if (!(psiElement instanceof KtStringTemplateExpression)) {
                            continue;
                        }
                        KtStringTemplateExpression stringTemplateExpression = (KtStringTemplateExpression) psiElement;
                        final String currPath = stringTemplateExpression.getChildren()[0].getText();
                        // 当前 paths 的其中一个（@RequestMapping(path=["path1", "path2"])）
                        serviceStub.path(currPath);
                    }
                }
            }
        }

        return serviceStub;
    }

    public List<KtClass> getRestfulKotlinClasses(@NotNull Qualified[] qualified, boolean withLib) {
        List<KtClass> classes = new ArrayList<>();

        for (Qualified item : qualified) {
            String name = withLib ? item.getQualifiedName() : item.getName();
            List<KtClass> list = findKtClassByAnnotationName(name, withLib);
            if (list.isEmpty()) {
                continue;
            }
            classes.addAll(list);
        }

        return classes;
    }

    public List<KtNamedFunction> getFunctionsOnKtClass(@NotNull KtClass ktClass) {
        List<KtNamedFunction> functions = new ArrayList<>();
        for (KtDeclaration declaration : ktClass.getDeclarations()) {
            if (!(declaration instanceof KtNamedFunction)) {
                continue;
            }
            functions.add(((KtNamedFunction) declaration));
        }
        return functions;
    }

    @NotNull
    public List<KtClass> findKtClassByAnnotationName(@NotNull String name, boolean withLib) {
        List<KtClass> classes = new ArrayList<>();
        for (KtAnnotationEntry entry : findKtAnnotationEntryByName(name, withLib)) {
            PsiElement context = entry.getContext();
            if (context == null) {
                continue;
            }
            context = context.getContext();
            if (!(context instanceof KtClass)) {
                continue;
            }
            classes.add(((KtClass) context));
        }
        return classes;
    }

    @NotNull
    private Set<KtAnnotationEntry> findKtAnnotationEntryByName(@NotNull String name, boolean withLib) {
        String temp = name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : name;
        Set<KtAnnotationEntry> collection = new HashSet<>(KotlinAnnotationsIndex.getInstance().get(
                temp,
                project,
                module.getModuleScope()
        ));
        if (withLib) {
            collection.addAll(KotlinAnnotationsIndex.getInstance().get(
                    name,
                    project,
                    module.getModuleWithLibrariesScope())
            );
            collection.addAll(KotlinAnnotationsIndex.getInstance().get(
                    temp,
                    project,
                    module.getModuleWithLibrariesScope())
            );
        }
        return collection;
    }

    public interface Qualified {

        /**
         * 不包含jar包时扫描的名称
         *
         * @return name
         */
        String getName();

        /**
         * 包含jar包时扫描的名称
         *
         * @return qualifiedName
         */
        String getQualifiedName();
    }
}
