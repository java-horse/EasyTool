package easy.doc.variable.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import easy.helper.ServiceHelper;
import easy.translate.TranslateService;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class ParamsVariableGeneratorImpl extends AbstractVariableGeneratorImpl {
    private final TranslateService translateService = ServiceHelper.getService(TranslateService.class);

    @Override
    public String generate(PsiElement element) {
        if (!(element instanceof PsiMethod)) {
            return StringUtils.EMPTY;
        }

        List<String> paramNameList = Arrays.stream(((PsiMethod) element).getParameterList().getParameters()).map(PsiParameter::getName).collect(Collectors.toList());
        if (paramNameList.isEmpty()) {
            return StringUtils.EMPTY;
        }

        List<ParamGroup> paramGroupList = new ArrayList<>();
        PsiDocComment docComment = ((PsiMethodImpl) element).getDocComment();
        Map<String, PsiDocTag> psiDocTagMap = new HashMap<>();
        if (docComment != null) {
            PsiDocTag[] paramsDocArray = docComment.findTagsByName("param");
            psiDocTagMap = Arrays.stream(paramsDocArray).collect(Collectors.toMap(q -> q.getDataElements()[0].getText(), tag -> tag));
        }

        for (String paramName : paramNameList) {
            PsiDocTag psiDocTag = psiDocTagMap.get(paramName);
            if (psiDocTag == null) {
                // 不存在则插入一个需要翻译的
                paramGroupList.add(new ParamGroup(paramName, translateService.translate(paramName)));
                continue;
            }
            PsiElement eleParamDesc = psiDocTag.getDataElements()[1];
            String desc = eleParamDesc.getText();
            if (StringUtils.isNotEmpty(desc)) {
                // 如果已经存在注释则直接返回
                paramGroupList.add(new ParamGroup(paramName, desc));
            } else {
                // 不存在注释则翻译
                paramGroupList.add(new ParamGroup(paramName, translateService.translate(paramName)));
            }
        }
        List<String> perLine = Lists.newArrayList();
        for (int i = 0; i < paramGroupList.size(); i++) {
            ParamGroup paramGroup = paramGroupList.get(i);
            if (i == 0) {
                perLine.add("@param " + paramGroup.getParam() + StringUtils.SPACE + paramGroup.getDesc());
            } else {
                perLine.add("* @param " + paramGroup.getParam() + StringUtils.SPACE + paramGroup.getDesc());
            }
        }
        return String.join(StringUtils.LF, perLine);
    }

    /**
     * 参数名注释组合
     */
    static class ParamGroup {
        private String param;
        private String desc;

        public ParamGroup(String param, String desc) {
            this.param = param;
            this.desc = desc;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
