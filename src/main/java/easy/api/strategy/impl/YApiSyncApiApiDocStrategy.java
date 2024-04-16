package easy.api.strategy.impl;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.ArrayUtil;
import easy.api.context.ApiDocContext;
import easy.api.entity.YApiCategoryInfo;
import easy.api.entity.YApiDocInfo;
import easy.api.entity.YApiProjectInfo;
import easy.api.service.YApiService;
import easy.api.strategy.SyncApiDocStrategy;
import easy.config.api.ApiDocConfig;
import easy.enums.JavaClassTypeEnum;
import easy.enums.RequestAnnotationEnum;
import easy.handler.ServiceHelper;
import easy.util.ApiDocUtil;
import easy.util.PsiClassUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class YApiSyncApiApiDocStrategy extends DefaultSyncApiDocStrategy implements SyncApiDocStrategy {
    private static final Logger log = Logger.getInstance(YApiSyncApiApiDocStrategy.class);
    private final YApiService yApiService = ServiceHelper.getService(YApiService.class);

    /**
     * 同步API文档
     *
     * @param apiDocConfig
     * @param apiDocContext
     * @param psiClass
     * @author mabin
     * @date 2024/04/02 16:52
     */
    @Override
    public void syncApiDoc(ApiDocConfig apiDocConfig, ApiDocContext apiDocContext, PsiClass psiClass) {
        // 获取当前操作的方法
        PsiMethod targetMethod = PsiClassUtil.getTargetMethod(apiDocContext.getTargetElement());
        // 获取当前类所有方法
        PsiMethod[] psiMethods = psiClass.getMethods();
        if (ArrayUtil.isEmpty(psiMethods)) {
            return;
        }
        List<YApiDocInfo> yApiDocInfoList = new ArrayList<>();
        // 获取当前类的真实类型
        JavaClassTypeEnum javaClassTypeEnum = JavaClassTypeEnum.get(psiClass);
        // 遍历当前类的所有方法
        for (PsiMethod psiMethod : psiMethods) {
            // 过滤不符合条件的方法(不需要解析的方法)
            if (!ApiDocUtil.isValidMethod(javaClassTypeEnum, psiMethod)) {
                continue;
            }
            // 判断是否当前操作方法
            if (Objects.isNull(targetMethod) || targetMethod.equals(psiMethod)) {
                YApiDocInfo yApiDocInfo = new YApiDocInfo();
                // 设置token
                yApiDocInfo.setToken(apiDocConfig.getYapiToken());
                // 设置项目Id(projectId)
                YApiProjectInfo yApiProjectInfo = yApiService.getProjectInfo(apiDocConfig.getYapiServer(), apiDocConfig.getYapiToken());
                if (Objects.isNull(yApiProjectInfo)) {
                    continue;
                }
                yApiDocInfo.setProjectId(yApiProjectInfo.getProjectId());
                // 设置分类ID(catId): 根据分类名称比对匹配获取
                String cateGoryName = genApiCategory(psiClass);
                List<YApiCategoryInfo> yApiCategoryInfoList = yApiService.listCategoryInfo(apiDocConfig.getYapiServer(), apiDocConfig.getYapiToken(), yApiDocInfo.getProjectId());
                if (CollectionUtils.isNotEmpty(yApiCategoryInfoList)) {
                    yApiCategoryInfoList.forEach(category -> {
                        if (StringUtils.equals(cateGoryName, category.getName())) {
                            yApiDocInfo.setCatId(category.getCategoryId());
                        }
                    });
                }
                // 设置标题(获取标题顺序: Swagger -> JavaDoc -> className#methodName)
                yApiDocInfo.setTitle(genApiTitle(psiMethod, psiClass));
                // 设置路径Path (类路径+方法路径)
                yApiDocInfo.setPath(genApiPath(psiClass, psiMethod));
                // 设置请求方式(GET/POST/PUT/DELETE/PATCH)
                yApiDocInfo.setMethod(genApiMethod(psiMethod));

            }
        }


    }

}
