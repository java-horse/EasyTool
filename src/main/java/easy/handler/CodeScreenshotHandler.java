package easy.handler;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import com.intellij.codeInsight.hint.EditorFragmentComponent;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.fileChooser.FileSaverDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.scale.JBUIScale;
import easy.base.Constants;
import easy.config.screenshot.CodeScreenshotConfig;
import easy.config.screenshot.CodeScreenshotConfigComponent;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Objects;

public class CodeScreenshotHandler {

    private static final Logger log = Logger.getInstance(CodeScreenshotHandler.class);

    private CodeScreenshotHandler() {
    }

    private static final JBColor green = new JBColor(new Color(43, 204, 31), new Color(43, 204, 31));
    private static final JBColor yellow = new JBColor(new Color(204, 159, 31), new Color(204, 159, 31));
    private static final JBColor red = new JBColor(new Color(204, 50, 31), new Color(204, 50, 31));

    /**
     * 创建图像
     *
     * @param editor 编辑
     * @return {@link java.awt.image.BufferedImage}
     * @author mabin
     * @date 2024/06/04 14:46
     */
    public static BufferedImage createImage(@NotNull Editor editor) {
        TextRange range = getRange(editor);
        Document document = editor.getDocument();
        EditorState state = EditorState.from(editor);
        try {
            editor.getSelectionModel().setSelection(0, 0);
            editor.getCaretModel().moveToOffset(range.getStartOffset() == 0 ? document.getLineEndOffset(document.getLineCount() - 1) : 0);
            if (editor instanceof EditorEx editorEx) {
                editorEx.setCaretEnabled(false);
            }
            editor.getSettings().setCaretRowShown(false);

            CodeScreenshotConfig config = ServiceHelper.getService(CodeScreenshotConfigComponent.class).getState();
            if (Objects.isNull(config)) {
                return null;
            }

            JComponent contentComponent = editor.getContentComponent();
            Graphics2D contentGraphics = (Graphics2D) contentComponent.getGraphics();
            AffineTransform currentTransform = contentGraphics.getTransform();
            AffineTransform newTransform = new AffineTransform(currentTransform);
            Double scale = config.getScale();
            newTransform.scale(scale, scale);
            paint(contentComponent, newTransform, 1, 1, JBColor.BLACK, config, 0);
            Rectangle2D r = getSelectionRectangle(range, document.getText(range), config, editor);
            newTransform.translate(-r.getX(), -r.getY());
            return paint(contentComponent, newTransform, (int) (r.getWidth() * scale), (int) (r.getHeight() * scale),
                    EditorFragmentComponent.getBackgroundColor(editor, false), config, (int) (config.getInnerPadding() * scale));
        } catch (Exception e) {
            log.error(String.format("%s generate code screenshot handle exception", Constants.PLUGIN_NAME), e);
        } finally {
            state.restore(editor);
        }
        return null;
    }

    /**
     * 保存图像
     *
     * @param image   图像
     * @param project 项目
     * @param config
     * @author mabin
     * @date 2024/06/05 09:32
     */
    public static void saveImage(BufferedImage image, Project project, CodeScreenshotConfig config) {
        FileSaverDescriptor fsd = new FileSaverDescriptor(String.format("%s Code Screenshot Save", Constants.PLUGIN_NAME),
                "Select a location to save the code screenshot", config.getCustomFileNameSuffix());
        FileSaverDialog saveFileDialog = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project);
        String filePrefixName = StringUtils.isBlank(config.getCustomFileName()) ? "screenshot_" : config.getCustomFileName();
        String fileFormatName = StringUtils.EMPTY;
        try {
            fileFormatName = StringUtils.isNotBlank(config.getCustomFileNameFormat())
                    ? DateUtil.format(new Date(), config.getCustomFileNameFormat())
                    : DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        } catch (Exception ignored) {
        }
        VirtualFileWrapper virtualFileWrapper = saveFileDialog.save(filePrefixName + fileFormatName + StrPool.DOT + config.getCustomFileNameSuffix());
        if (Objects.isNull(virtualFileWrapper)) {
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(virtualFileWrapper.getFile())) {
            ImageIO.write(image, config.getCustomFileNameSuffix(), outputStream);
        } catch (Throwable t) {
            NotifyUtil.notify("Code screenshot file save error, please try again later!", NotificationType.ERROR);
        }

    }

    /**
     * 获取范围
     *
     * @param editor 编辑
     * @return {@link com.intellij.openapi.util.TextRange}
     * @author mabin
     * @date 2024/06/04 14:47
     */
    private static TextRange getRange(Editor editor) {
        SelectionModel selectionModel = editor.getSelectionModel();
        return new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
    }

    /**
     * 包括点
     *
     * @param r r
     * @param p p
     * @author mabin
     * @date 2024/06/04 14:47
     */
    private static void includePoint(Rectangle2D r, Point2D p) {
        if (r.isEmpty()) {
            r.setFrame(p, new Dimension(1, 1));
        } else {
            r.add(p);
        }
    }

    /**
     * 生成图像
     *
     * @param contentComponent 内容组件
     * @param at               在
     * @param width            宽度
     * @param height           高度
     * @param backgroundColor
     * @param config           配置
     * @param innerPadding
     * @return {@link java.awt.image.BufferedImage}
     * @author mabin
     * @date 2024/06/04 14:57
     */
    private static BufferedImage paint(JComponent contentComponent, AffineTransform at, int width, int height, Color backgroundColor, CodeScreenshotConfig config, int innerPadding) {
        int outerPaddingHoriMapped = (int) (config.getOuterPadding() * config.getScale());
        int outerPaddingVertMapped = (int) (config.getOuterPadding() * config.getScale());
        double indicatorDimensions = 10 * config.getScale();
        double windowControlsPadding = 6 * config.getScale();
        double scale = JBUIScale.sysScale(contentComponent);
        int paddingX = innerPadding + outerPaddingHoriMapped;
        int paddingY = innerPadding + outerPaddingVertMapped;
        double windowControlsHeightWithPadding = windowControlsPadding + indicatorDimensions + windowControlsPadding;
        double preferredPaddingTopWithIndicators = Math.max(windowControlsHeightWithPadding, innerPadding);
        BufferedImage img = new BufferedImage((int) (outerPaddingHoriMapped + innerPadding + width * scale + innerPadding + outerPaddingHoriMapped),
                (int) (outerPaddingVertMapped + (config.getShowWindowIcons() ? preferredPaddingTopWithIndicators : innerPadding) + height * scale + innerPadding + outerPaddingVertMapped),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new JBColor(new Color(config.getBackgroundColor(), true), new Color(config.getBackgroundColor(), true)));
        g.fillRect(0, 0, imgWidth, imgHeight);
        g.setPaint(backgroundColor);
        int windowRoundness = (int) (config.getWindowRoundness() * config.getScale());
        g.fillRoundRect(outerPaddingHoriMapped, outerPaddingVertMapped, imgWidth - outerPaddingHoriMapped * 2, imgHeight - outerPaddingVertMapped * 2, windowRoundness, windowRoundness);
        double xOffset = 0;
        if (config.getShowWindowIcons()) {
            for (JBColor jbColor : new JBColor[]{red, yellow, green}) {
                g.setPaint(jbColor);
                g.fillOval((int) (outerPaddingHoriMapped + windowControlsPadding + xOffset),
                        (int) (outerPaddingVertMapped + windowControlsPadding),
                        (int) indicatorDimensions, (int) indicatorDimensions);
                xOffset += indicatorDimensions + windowControlsPadding;
            }
        }
        g.translate(paddingX, config.getShowWindowIcons() ? outerPaddingVertMapped + preferredPaddingTopWithIndicators : paddingY);
        g.clipRect(0, 0, scaledWidth, scaledHeight);
        g.transform(at);
        contentComponent.paint(g);
        return img;
    }

    /**
     * 获取选择矩形
     *
     * @param range  范围
     * @param text   文本
     * @param config 配置
     * @return {@link java.awt.geom.Rectangle2D}
     * @author mabin
     * @date 2024/06/04 14:57
     */
    @NotNull
    private static Rectangle2D getSelectionRectangle(TextRange range, String text, CodeScreenshotConfig config, Editor editor) {
        int start = range.getStartOffset();
        int end = range.getEndOffset();
        Rectangle2D r = new Rectangle2D.Double();
        for (int i = start; i < end; i++) {
            if (config.getRemoveIndentation() && Character.isWhitespace(text.charAt(i - start))) {
                continue;
            }
            Point point = editor.offsetToXY(i, false, false);
            includePoint(r, point);
            includePoint(r, new Point2D.Double(point.getX(), point.getY() + editor.getLineHeight()));
            point = editor.offsetToXY(i + 1, false, true);
            includePoint(r, point);
            includePoint(r, new Point2D.Double(point.getX(), point.getY() + editor.getLineHeight()));
        }
        for (Inlay<?> inlay : editor.getInlayModel().getInlineElementsInRange(start, end)) {
            Rectangle bounds = inlay.getBounds();
            if (bounds != null) {
                r.add(bounds);
            }
        }
        return r;
    }

    static class EditorState {
        private final TextRange range;
        private final int offset;
        private final boolean caretRow;

        EditorState(TextRange range, int offset, boolean caretRow) {
            this.range = range;
            this.offset = offset;
            this.caretRow = caretRow;
        }

        @NotNull
        static EditorState from(Editor editor) {
            return new EditorState(getRange(editor), editor.getCaretModel().getOffset(), editor.getSettings().isCaretRowShown());
        }

        void restore(Editor editor) {
            editor.getSettings().setCaretRowShown(caretRow);
            SelectionModel selectionModel = editor.getSelectionModel();
            CaretModel caretModel = editor.getCaretModel();
            if (editor instanceof EditorEx editorEx) {
                editorEx.setCaretEnabled(true);
            }
            caretModel.moveToOffset(offset);
            selectionModel.setSelection(range.getStartOffset(), range.getEndOffset());
        }
    }

}
