package easy.provider;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.ui.awt.RelativePoint;
import easy.base.Constants;
import easy.enums.ExtraPackageNameEnum;
import easy.icons.EasyIcons;
import easy.util.CronUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

/**
 * cron表达式图标显示监听处理
 *
 * @project: EasyTool
 * @package: easy.provider
 * @author: mabin
 * @date: 2024/01/20 15:34:30
 */
public class CronMarkerProvider implements LineMarkerProvider {

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof PsiIdentifier && element.getParent() instanceof PsiMethod psiMethod
                && (shouldShowMarker(psiMethod))) {
            return new LineMarkerInfo<>(
                    element,
                    element.getTextRange(),
                    EasyIcons.ICON.CLOCK,
                    null,
                    new CronNavigationHandler(),
                    GutterIconRenderer.Alignment.RIGHT);
        }
        return null;
    }

    static class CronNavigationHandler implements GutterIconNavigationHandler<PsiElement> {
        @Override
        public void navigate(MouseEvent e, PsiElement elt) {
            PsiElement psiElement = elt.getParent();
            if (!(psiElement instanceof PsiMethod psiMethod)) {
                return;
            }
            PsiAnnotation annotation = psiMethod.getAnnotation(ExtraPackageNameEnum.SCHEDULED.getName());
            if (Objects.isNull(annotation)) {
                return;
            }
            PsiAnnotationMemberValue cronValue = annotation.findAttributeValue(Constants.ANNOTATION_ATTR.CRON);
            if (Objects.isNull(cronValue)) {
                return;
            }
            String cron = StringUtils.replace(cronValue.getText(), "\"", StringUtils.EMPTY);
            if (StringUtils.isBlank(cron)) {
                return;
            }
            Editor editor = CommonDataKeys.EDITOR.getData(DataManager.getInstance().getDataContext());
            if (Objects.isNull(editor)) {
                return;
            }
            JLabel label = new JLabel();
            MessageType messageType = MessageType.INFO;
            if (!CronUtil.isCron(cron)) {
                label.setText("Invalid cron expression");
                messageType = MessageType.ERROR;
            } else {
                List<String> cronList = CronUtil.nextExecutionTime(cron, Constants.NUM.TEN);
                if (CollectionUtils.isEmpty(cronList)) {
                    label.setText("Unable to preview time");
                    messageType = MessageType.WARNING;
                } else {
                    String title = "最近" + Constants.NUM.TEN + "次执行时间";
                    label.setText(title + StringUtils.LF + StringUtils.join(cronList, StringUtils.LF));
                }
            }
            Point position = new Point(0, editor.visualPositionToXY(editor.offsetToVisualPosition(elt.getTextOffset())).y);
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(label.getText(), messageType, null)
                    .setFadeoutTime(5000)
                    .createBalloon()
                    .show(new RelativePoint(editor.getContentComponent(), position), Balloon.Position.atRight);
        }
    }

    /**
     * 是否显示图标
     *
     * @param psiMethod
     * @return boolean
     * @author mabin
     * @date 2024/1/20 15:43
     */
    private boolean shouldShowMarker(@NotNull PsiMethod psiMethod) {
        PsiAnnotation annotation = psiMethod.getAnnotation(ExtraPackageNameEnum.SCHEDULED.getName());
        if (Objects.isNull(annotation)) {
            return false;
        }
        PsiAnnotationMemberValue cronValue = annotation.findAttributeValue(Constants.ANNOTATION_ATTR.CRON);
        return Objects.nonNull(cronValue) && StringUtils.isNotBlank(cronValue.getText());
    }

}
