package easy.config.doc;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
import easy.enums.JavaDocCommentCoverEnum;
import easy.enums.JavaDocMethodReturnTypeEnum;
import easy.enums.JavaDocPropertyCommentModelEnum;
import easy.enums.JavaDocPropertyCommentTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(name = Constants.PLUGIN_NAME + "JavaDocConfig", storages = {@Storage(Constants.PLUGIN_NAME + "JavaDocConfig.xml")})
public class JavaDocConfigComponent implements PersistentStateComponent<JavaDocConfig> {

    private JavaDocConfig javaDocConfig;

    @Override
    public @Nullable JavaDocConfig getState() {
        if (Objects.isNull(javaDocConfig)) {
            javaDocConfig = new JavaDocConfig();
            javaDocConfig.setAuthor(Constants.JAVA_DOC.DEFAULT_AUTHOR);
            javaDocConfig.setDateFormat(Constants.JAVA_DOC.DEFAULT_DATE_FORMAT);
            javaDocConfig.setMethodReturnType(JavaDocMethodReturnTypeEnum.LINK.getType());
            javaDocConfig.setPropertyCommentType(JavaDocPropertyCommentTypeEnum.ORDINARY.getType());
            javaDocConfig.setPropertyCommentModel(JavaDocPropertyCommentModelEnum.JAVA_DOC.getModel());
            javaDocConfig.setAutoGenerateDocClass(Boolean.FALSE);
            javaDocConfig.setAutoGenerateDocMethod(Boolean.FALSE);
            javaDocConfig.setAutoGenerateDocField(Boolean.FALSE);
            javaDocConfig.setCoverModel(JavaDocCommentCoverEnum.MERGE.getModel());
            javaDocConfig.setCoverHintPrompt(Boolean.FALSE);
            javaDocConfig.setClassBatchEnable(Boolean.TRUE);
            javaDocConfig.setMethodBatchEnable(Boolean.TRUE);
            javaDocConfig.setFieldBatchEnable(Boolean.TRUE);
            javaDocConfig.setInnerClassBatchEnable(Boolean.FALSE);
            javaDocConfig.setGetAndSetMethodBatchEnable(Boolean.FALSE);
            javaDocConfig.setConstructorMethodBatchEnable(Boolean.FALSE);
        } else {
            javaDocConfig.setAuthor(StringUtils.isBlank(javaDocConfig.getAuthor()) ? Constants.JAVA_DOC.DEFAULT_AUTHOR : javaDocConfig.getAuthor());
            javaDocConfig.setDateFormat(StringUtils.isBlank(javaDocConfig.getDateFormat()) ? Constants.JAVA_DOC.DEFAULT_DATE_FORMAT : javaDocConfig.getDateFormat());
            javaDocConfig.setMethodReturnType(StringUtils.isBlank(javaDocConfig.getMethodReturnType()) ? JavaDocMethodReturnTypeEnum.LINK.getType() : javaDocConfig.getMethodReturnType());
            javaDocConfig.setPropertyCommentType(StringUtils.isBlank(javaDocConfig.getPropertyCommentType()) ? JavaDocPropertyCommentTypeEnum.ORDINARY.getType() : javaDocConfig.getPropertyCommentType());
            javaDocConfig.setPropertyCommentModel(StringUtils.isBlank(javaDocConfig.getPropertyCommentModel()) ? JavaDocPropertyCommentModelEnum.JAVA_DOC.getModel() : javaDocConfig.getPropertyCommentModel());
            javaDocConfig.setAutoGenerateDocClass(Objects.isNull(javaDocConfig.getAutoGenerateDocClass()) ? Boolean.FALSE : javaDocConfig.getAutoGenerateDocClass());
            javaDocConfig.setAutoGenerateDocMethod(Objects.isNull(javaDocConfig.getAutoGenerateDocMethod()) ? Boolean.FALSE : javaDocConfig.getAutoGenerateDocMethod());
            javaDocConfig.setAutoGenerateDocField(Objects.isNull(javaDocConfig.getAutoGenerateDocField()) ? Boolean.FALSE : javaDocConfig.getAutoGenerateDocField());
            javaDocConfig.setCoverModel(Objects.isNull(javaDocConfig.getCoverModel()) ? JavaDocCommentCoverEnum.MERGE.getModel() : javaDocConfig.getCoverModel());
            javaDocConfig.setCoverHintPrompt(Objects.isNull(javaDocConfig.getCoverHintPrompt()) ? Boolean.FALSE : javaDocConfig.getCoverHintPrompt());
            javaDocConfig.setClassBatchEnable(Objects.isNull(javaDocConfig.getClassBatchEnable()) ? Boolean.TRUE : javaDocConfig.getClassBatchEnable());
            javaDocConfig.setMethodBatchEnable(Objects.isNull(javaDocConfig.getMethodBatchEnable()) ? Boolean.TRUE : javaDocConfig.getMethodBatchEnable());
            javaDocConfig.setFieldBatchEnable(Objects.isNull(javaDocConfig.getFieldBatchEnable()) ? Boolean.TRUE : javaDocConfig.getFieldBatchEnable());
            javaDocConfig.setInnerClassBatchEnable(Objects.isNull(javaDocConfig.getInnerClassBatchEnable()) ? Boolean.FALSE : javaDocConfig.getInnerClassBatchEnable());
            javaDocConfig.setGetAndSetMethodBatchEnable(Objects.isNull(javaDocConfig.getGetAndSetMethodBatchEnable()) ? Boolean.FALSE : javaDocConfig.getGetAndSetMethodBatchEnable());
            javaDocConfig.setConstructorMethodBatchEnable(Objects.isNull(javaDocConfig.getConstructorMethodBatchEnable()) ? Boolean.FALSE : javaDocConfig.getConstructorMethodBatchEnable());
        }
        return javaDocConfig;
    }

    @Override
    public void loadState(@NotNull JavaDocConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
