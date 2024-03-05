package easy.config.doc;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import easy.base.Constants;
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
            javaDocConfig.setPropertyCommentType(JavaDocPropertyCommentTypeEnum.SINGLE.getType());
            javaDocConfig.setPropertyCommentModel(JavaDocPropertyCommentModelEnum.JAVA_DOC.getModel());
        } else {
            javaDocConfig.setAuthor(StringUtils.isBlank(javaDocConfig.getAuthor()) ? Constants.JAVA_DOC.DEFAULT_AUTHOR : javaDocConfig.getAuthor());
            javaDocConfig.setDateFormat(StringUtils.isBlank(javaDocConfig.getDateFormat()) ? Constants.JAVA_DOC.DEFAULT_DATE_FORMAT : javaDocConfig.getDateFormat());
            javaDocConfig.setMethodReturnType(StringUtils.isBlank(javaDocConfig.getMethodReturnType()) ? JavaDocMethodReturnTypeEnum.LINK.getType() : javaDocConfig.getMethodReturnType());
            javaDocConfig.setPropertyCommentType(StringUtils.isBlank(javaDocConfig.getPropertyCommentType()) ? JavaDocPropertyCommentTypeEnum.SINGLE.getType() : javaDocConfig.getPropertyCommentType());
            javaDocConfig.setPropertyCommentModel(StringUtils.isBlank(javaDocConfig.getPropertyCommentModel()) ? JavaDocPropertyCommentModelEnum.JAVA_DOC.getModel() : javaDocConfig.getPropertyCommentModel());
        }
        return javaDocConfig;
    }

    @Override
    public void loadState(@NotNull JavaDocConfig state) {
        XmlSerializerUtil.copyBean(state, Objects.requireNonNull(getState()));
    }

}
