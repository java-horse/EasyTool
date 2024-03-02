package easy.doc.variable.impl;

import cn.hutool.core.date.DateUtil;
import com.intellij.notification.NotificationType;
import com.intellij.psi.PsiElement;
import easy.base.Constants;
import easy.util.EasyCommonUtil;
import easy.util.NotificationUtil;

import java.util.Date;

public class DateVariableGeneratorImpl extends AbstractVariableGeneratorImpl {
    @Override
    public String generate(PsiElement element) {
        String dateFormat = getConfig().getDateFormat();
        try {
            return DateUtil.format(new Date(), dateFormat);
        } catch (Exception e) {
            NotificationUtil.notify("您配置的日期格式【" + dateFormat + "】错误, 请及时修改!", NotificationType.ERROR, EasyCommonUtil.getPluginSettingAction());
            return DateUtil.format(new Date(), Constants.JAVA_DOC.DEFAULT_DATE_FORMAT);
        }
    }

}