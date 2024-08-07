package easy.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
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
import easy.enums.FontStyleEnum;
import easy.util.MessageUtil;
import easy.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
     * @param config 配置
     * @return {@link java.awt.image.BufferedImage}
     * @author mabin
     * @date 2024/06/04 14:46
     */
    public static @Nullable BufferedImage createImage(@NotNull Editor editor, CodeScreenshotConfig config) {
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
    public static void saveImage(BufferedImage image, Project project, @NotNull CodeScreenshotConfig config) {
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
            ImgUtil.write(image, config.getCustomFileNameSuffix(), outputStream);
            MessageUtil.showInfoMessage("Code Screenshot Save Success");
        } catch (Throwable t) {
            NotifyUtil.notify("Code screenshot file save error, please try again later!", NotificationType.ERROR);
        }
    }

    /**
     * 添加图像水印
     *
     * @param targetImg 目标img
     * @param config    配置
     * @author mabin
     * @date 2024/06/11 16:38
     */
    public static @Nullable BufferedImage addImageWaterMark(BufferedImage targetImg, CodeScreenshotConfig config) {
        try {
            int width = targetImg.getWidth();
            int height = targetImg.getHeight();
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(targetImg, 0, 0, width, height, null);
            Integer fontSize = Convert.toInt(config.getWaterMarkFontSize());
            // 设置颜色
            g.setColor(new JBColor(config.getWaterMarkFontColor(), config.getWaterMarkFontColor()));
            // 设置透明度
            float alpha = BigDecimal.valueOf(config.getFontWaterMarkTransparency()).divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP).floatValue();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 设置字体
            g.setFont(new Font(config.getWaterMarkFontFamily(), FontStyleEnum.getFontStyle(config.getWaterMarkFontStyle()), fontSize));
            // 设置水印
            if (config.getAutoFullScreenWatermark()) {
                // 设置旋转角度
                g.rotate(Math.toRadians(config.getFontWaterMarkRotate()));
                // 全屏自适应平铺水印
                int lineSpacing = fontSize * 2;
                int xWidth = getWaterMarkWidth(config.getWaterMarkFontText(), fontSize);
                int xCanNum = width / xWidth + 1;
                int yCanNum = height / fontSize + 1;
                for (int i = 1; i <= yCanNum; i += config.getFontWaterMarkSparsity()) {
                    int y = fontSize * i + lineSpacing * i;
                    for (int j = 0; j < xCanNum; j += config.getFontWaterMarkSparsity()) {
                        g.drawString(config.getWaterMarkFontText(), xWidth * j + lineSpacing * j, y - (fontSize + lineSpacing) * j);
                    }
                }
            } else {
                // 右下角水印
                g.drawString(config.getWaterMarkFontText(), width - (config.getWaterMarkFontText().length() + 1) * fontSize, height - fontSize * 2);
            }
            // 释放资源
            g.dispose();
            return bufferedImage;
        } catch (Exception e) {
            log.error(String.format("%s generate code screenshot add water mark handle exception", Constants.PLUGIN_NAME), e);
        }
        return null;
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


    /**
     * 获取水印文字占用宽度
     *
     * @param markText markText
     * @param fontSize 字体大小
     * @return int
     * @author mabin
     * @date 2024/06/14 13:59
     */
    private static int getWaterMarkWidth(String markText, int fontSize) {
        char[] chars = markText.toCharArray();
        int fontSize2 = fontSize / 2;
        int width = 0;
        for (char c : chars) {
            width += String.valueOf(c).getBytes().length != 1 ? fontSize : fontSize2;
        }
        return width;
    }


}
