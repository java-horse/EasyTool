package easy.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaSdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.MessageConstants;
import com.intellij.openapi.ui.Messages;
import easy.base.Constants;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.util.Date;
import java.util.Objects;

/**
 * 项目通用处理
 *
 * @project: EasyTool
 * @package: easy.util
 * @author: mabin
 * @date: 2023/11/28 10:50:42
 */
public class EasyCommonUtil {

    private static final Logger log = Logger.getInstance(EasyCommonUtil.class);

    private static final Long ONE_HOUR = 3600L;
    private static final Long THREE_HOUR = 10800L;
    private static final Long ONE_DAY = ONE_HOUR * 24;
    private static final Long ONE_WEEK = ONE_HOUR * 24 * 7;
    private static final Long ONE_MONTH = ONE_DAY * 31;
    private static final Long ONE_YEAR = ONE_MONTH * 12;

    private EasyCommonUtil() {
    }

    /**
     * 浏览器打开
     *
     * @param url
     * @return void
     * @author mabin
     * @date 2023/11/28 10:54
     */
    public static void openLink(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        if (Objects.nonNull(desktop) && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(url));
            } catch (Exception e) {
                log.error("open link exception: " + url, e);
            }
        }
    }

    /**
     * 二次确认浏览器打开
     *
     * @param url
     * @return void
     * @author mabin
     * @date 2023/11/28 11:08
     */
    public static void confirmOpenLink(String url) {
        if (StringUtils.isBlank(url)) {
            return;
        }
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        int result = Messages.showYesNoDialog("About to leave【" + applicationInfo.getFullApplicationName() + "】whether to continue the visit\n" + url + "\n",
                Constants.PLUGIN_NAME, "Continue", "Cancel", Messages.getWarningIcon());
        if (result == MessageConstants.YES) {
            openLink(url);
        }
    }

    /**
     * 打开插件设置弹窗
     *
     * @return
     */
    public static AnAction getPluginSettingAction() {
        return getPluginSettingAction(null);
    }

    /**
     * 打开插件设置弹窗
     *
     * @param project
     * @return
     */
    public static AnAction getPluginSettingAction(Project project) {
        return new NotificationAction("⚙️ " + Constants.PLUGIN_NAME) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, Constants.PLUGIN_NAME);
            }
        };
    }

    /**
     * 时间格式
     *
     * @param time 时间
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/31 17:39
     */
    public static String format(Long time) {
        if (Objects.isNull(time) || time == 0) {
            return StringUtils.EMPTY;
        }
        long currentSeconds = DateUtil.currentSeconds();
        if (time > currentSeconds) {
            return StringUtils.EMPTY;
        }
        long diffTime = currentSeconds - time;
        if (diffTime < 60) {
            return diffTime + " seconds ago";
        } else if (diffTime < ONE_HOUR) {
            Long minutes = diffTime / 60;
            return minutes + " minutes ago";
        } else if (diffTime < THREE_HOUR) {
            Long hour = diffTime / ONE_HOUR;
            return hour + " hours ago";
        } else if (diffTime < ONE_DAY) {
            DateTime endOfDay = DateUtil.endOfDay(new Date(time * 1000));
            long endDay = endOfDay.toTimestamp().getTime() / 1000;
            String day = currentSeconds < endDay ? "today" : "yesterday";
            DateTime dateTime = DateTime.of(time * 1000);
            int hour = dateTime.hour(true);
            int minute = dateTime.minute();
            String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
            String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
            return day + " " + hourStr + ":" + minuteStr;
        } else if (diffTime < ONE_WEEK) {
            Long day = diffTime / ONE_DAY;
            return day + " days ago";
        } else if (diffTime < ONE_MONTH) {
            Long week = diffTime / ONE_WEEK;
            return week + " weeks ago";
        } else if (diffTime < ONE_YEAR) {
            Long month = diffTime / ONE_MONTH;
            return month + " months ago";
        }
        Long year = diffTime / ONE_YEAR;
        return year + " years ago";
    }

    /**
     * 获取项目jdk版本
     *
     * @param project 项目
     * @return {@link java.lang.String}
     * @author mabin
     * @date 2024/05/14 10:22
     */
    public static JavaSdkVersion getProjectJdkVersion(Project project) {
        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        Sdk sdk = rootManager.getProjectSdk();
        JavaSdk javaSdk = JavaSdkImpl.getInstance();
        if (Objects.isNull(sdk) || StringUtils.isBlank(sdk.getHomePath()) || !javaSdk.isValidSdkHome(sdk.getHomePath())) {
            return null;
        }
        return javaSdk.getVersion(sdk);
    }

    /**
     * 自定义背景提示文本
     *
     * @param component 组件
     * @param text      文本
     * @author mabin
     * @date 2024/06/12 10:27
     */
    public static void customBackgroundText(JComponent component, String text) {
        if (component instanceof JTextArea textArea) {
            textArea.setDisabledTextColor(Color.GRAY);
            textArea.setText(text);
            textArea.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (StringUtils.equals(textArea.getText(), text)) {
                        textArea.setText(StringUtils.EMPTY);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (StringUtils.isBlank(textArea.getText())) {
                        textArea.setText(text);
                    }
                }
            });
        } else if (component instanceof JTextField textField) {
            textField.setDisabledTextColor(Color.GRAY);
            textField.setText(text);
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (StringUtils.equals(textField.getText(), text)) {
                        textField.setText(StringUtils.EMPTY);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (StringUtils.isBlank(textField.getText())) {
                        textField.setText(text);
                    }
                }
            });
        }
    }


    /**
     * 自定义标签提示文本
     *
     * @param label   标签
     * @param tipText 提示文本
     * @author mabin
     * @date 2024/07/02 09:53
     */
    public static void customLabelTipText(@NotNull JLabel label, String tipText) {
        if (Objects.isNull(tipText) || tipText.isBlank()) {
            return;
        }
        label.setIcon(AllIcons.General.ContextHelp);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setToolTipText(tipText);
    }

    /**
     * 添加表单元格复制侦听器
     *
     * @param jTable j表
     * @author mabin
     * @date 2024/07/03 10:07
     */
    public static void addTableCellCopyListener(JTable jTable) {
        jTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = jTable.getSelectedRow();
                int selectedColumn = jTable.getSelectedColumn();
                if (selectedRow != -1 && selectedColumn != -1) {
                    String cellValue = (String) jTable.getValueAt(selectedRow, selectedColumn);
                    CopyPasteManager.getInstance().setContents(new StringSelection(cellValue));
                }
            }
        });
    }

}
