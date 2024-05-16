package easy.api.parse.parser;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import easy.api.model.common.HttpMethod;
import easy.api.parse.model.PathInfo;
import easy.api.parse.util.PathUtils;
import easy.api.parse.util.PsiAnnotationUtils;
import easy.base.Constants;
import easy.enums.RequestAnnotationEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 路径请求相关工具解析类
 */
public class PathParser {

    private static final Map<HttpMethod, String> MAPPINGS = new LinkedHashMap<>();

    static {
        MAPPINGS.put(HttpMethod.GET, RequestAnnotationEnum.GET_MAPPING.getQualifiedName());
        MAPPINGS.put(HttpMethod.POST, RequestAnnotationEnum.POST_MAPPING.getQualifiedName());
        MAPPINGS.put(HttpMethod.PUT, RequestAnnotationEnum.PUT_MAPPING.getQualifiedName());
        MAPPINGS.put(HttpMethod.DELETE, RequestAnnotationEnum.DELETE_MAPPING.getQualifiedName());
        MAPPINGS.put(HttpMethod.PATCH, RequestAnnotationEnum.PATCH_MAPPING.getQualifiedName());
    }

    /**
     * 解析请求映射信息
     */
    public static PathInfo parse(PsiMethod method) {
        PathInfo pathInfo = null;
        PsiAnnotation requestMapping = PsiAnnotationUtils.getAnnotation(method, RequestAnnotationEnum.REQUEST_MAPPING.getQualifiedName());
        if (requestMapping != null) {
            pathInfo = parseRequestMappingAnnotation(requestMapping);
        } else {
            for (Entry<HttpMethod, String> entry : MAPPINGS.entrySet()) {
                HttpMethod httpMethod = entry.getKey();
                String mappingAnnotation = entry.getValue();
                PsiAnnotation annotation = PsiAnnotationUtils.getAnnotation(method, mappingAnnotation);
                if (annotation != null) {
                    pathInfo = parseXxxMappingAnnotation(httpMethod, annotation);
                    break;
                }
            }
        }
        return pathInfo;
    }

    /**
     * 解析@RequestMapping的信息
     */
    public static PathInfo parseRequestMappingAnnotation(PsiAnnotation annotation) {
        List<String> paths = getPaths(annotation);
        List<HttpMethod> methods = getMethods(annotation);
        if (methods.isEmpty()) {
            methods.add(HttpMethod.GET);
        }

        PathInfo mapping = new PathInfo();
        mapping.setMethod(methods.get(0));
        mapping.setPaths(paths);
        return mapping;
    }

    /**
     * 解析其他注解信息，例如: @GetMapping, @PostMapping, ....
     */
    private static PathInfo parseXxxMappingAnnotation(HttpMethod method, PsiAnnotation annotation) {
        PathInfo info = new PathInfo();
        info.setPaths(getPaths(annotation));
        info.setMethod(method);
        return info;
    }

    /**
     * 从注解获取方法信息
     */
    private static List<HttpMethod> getMethods(PsiAnnotation annotation) {
        return PsiAnnotationUtils.getStringArrayAttribute(annotation, Constants.ANNOTATION_ATTR.METHOD).stream()
                .filter(StringUtils::isNotEmpty)
                .map(HttpMethod::of).collect(Collectors.toList());
    }

    /**
     * 从注解获取Path部分信息
     */
    private static List<String> getPaths(PsiAnnotation annotation) {
        List<String> paths = PsiAnnotationUtils.getStringArrayAttribute(annotation, Constants.ANNOTATION_ATTR.PATH);
        if (paths.isEmpty()) {
            paths = PsiAnnotationUtils.getStringArrayAttribute(annotation, Constants.ANNOTATION_ATTR.VALUE);
        }
        if (paths.isEmpty()) {
            paths.add(StringUtils.EMPTY);
        }
        // 清除路径变量正则部分
        paths = paths.stream().map(PathUtils::clearPathPattern).collect(Collectors.toList());
        return paths;
    }

}
