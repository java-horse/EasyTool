package easy.form;

import cn.hutool.core.util.StrUtil;
import com.intellij.ide.ui.laf.darcula.ui.DarculaEditorTextFieldBorder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.EditorTextField;
import easy.base.Constants;
import easy.enums.GitCommitMessageTypeEnum;
import easy.git.commit.GitCommitMessageTemplate;
import easy.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @project: EasyTool
 * @package: easy.form
 * @author: mabin
 * @date: 2024/01/23 17:15:19
 */
public class GitCommitMessageView extends DialogWrapper {

    private static final Logger log = Logger.getInstance(GitCommitMessageView.class);

    private final Project project;
    private JPanel panel;
    private JLabel typeLabel;
    private JComboBox typeComboBox;
    private JLabel scopeLabel;
    private JTextField scopeTextField;
    private JTextField shortDescTextField;
    private JLabel shortDescLabel;
    private JLabel longDescLabel;
    private JScrollPane longDescScrollPane;
    private EditorTextField longDescEditorTextField;
    private JScrollPane breakingScrollPane;
    private EditorTextField breakingEditorTextField;
    private JLabel closedIssueLabel;
    private JTextField closedIssueTextField;
    private JLabel breakingLabel;

    public GitCommitMessageView(@NotNull Project project) {
        super(false);
        this.project = project;
        setTitle(Constants.PLUGIN_NAME + " Commit Message Template");
        createUI();
        init();
    }

    /**
     * 自动渲染页面
     *
     * @param
     * @return void
     * @author mabin
     * @date 2024/1/24 17:19
     */
    private void createUI() {
        longDescScrollPane.setBorder(BorderFactory.createEmptyBorder());
        Dimension dimension = new Dimension(730, 130);
        longDescScrollPane.setPreferredSize(dimension);
        breakingScrollPane.setBorder(BorderFactory.createEmptyBorder());
        breakingScrollPane.setPreferredSize(dimension);
        longDescEditorTextField.setBorder(new DarculaEditorTextFieldBorder());
        longDescEditorTextField.setOneLineMode(false);
        longDescEditorTextField.ensureWillComputePreferredSize();
        longDescEditorTextField.addSettingsProvider(uEditor -> {
            uEditor.setVerticalScrollbarVisible(true);
            uEditor.setHorizontalScrollbarVisible(true);
            uEditor.setBorder(null);
        });
        breakingEditorTextField.setBorder(new DarculaEditorTextFieldBorder());
        breakingEditorTextField.setOneLineMode(false);
        breakingEditorTextField.ensureWillComputePreferredSize();
        breakingEditorTextField.addSettingsProvider(uEditor -> {
            uEditor.setVerticalScrollbarVisible(true);
            uEditor.setHorizontalScrollbarVisible(true);
            uEditor.setBorder(null);
        });
        List<String> descList = GitCommitMessageTypeEnum.allTypeDesc();
        for (String desc : descList) {
            typeComboBox.addItem(desc);
        }
        typeComboBox.setSelectedItem(descList.get(Constants.NUM.ZERO));
        PropertiesComponent projectComponent = PropertiesComponent.getInstance(project);
        String lastCommitMessage = projectComponent.getValue(Constants.Persistence.GIT_COMMIT_MESSAGE.LAST_COMMIT_MESSAGE);
        if (StrUtil.isNotBlank(lastCommitMessage)) {
            GitCommitMessageTemplate commitMessageTemplate = JsonUtil.fromJson(lastCommitMessage, GitCommitMessageTemplate.class);
            if (Objects.nonNull(commitMessageTemplate)) {
                String desc = GitCommitMessageTypeEnum.getDesc(commitMessageTemplate.getType());
                if (StringUtils.isNotBlank(desc)) {
                    typeComboBox.setSelectedItem(desc);
                }
                if (StringUtils.isNotBlank(commitMessageTemplate.getScope())) {
                    scopeTextField.setText(commitMessageTemplate.getScope());
                }
                if (StringUtils.isNotBlank(commitMessageTemplate.getSubject())) {
                    shortDescTextField.setText(commitMessageTemplate.getSubject());
                }
                if (StringUtils.isNotBlank(commitMessageTemplate.getBody())) {
                    longDescEditorTextField.setText(commitMessageTemplate.getBody());
                }
                if (StringUtils.isNotBlank(commitMessageTemplate.getChanges())) {
                    breakingEditorTextField.setText(commitMessageTemplate.getChanges());
                }
                if (StringUtils.isNotBlank(commitMessageTemplate.getCloses())) {
                    closedIssueTextField.setText(commitMessageTemplate.getCloses());
                }
            }
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel;
    }

    /**
     * 组装commit message文本
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2024/1/24 17:37
     */
    public String getCommitMessage() {
        Object selectedItem = typeComboBox.getSelectedItem();
        if (Objects.isNull(selectedItem)) {
            return StringUtils.EMPTY;
        }
        String type = GitCommitMessageTypeEnum.getTitle(StringUtils.trim(StringUtils.split(String.valueOf(selectedItem),"-")[1]));
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(type);
        String scopeTextFieldText = scopeTextField.getText();
        if (StringUtils.isNotBlank(scopeTextFieldText)) {
            msgBuilder.append("(").append(scopeTextFieldText).append(")");
        }
        String shortDescTextFieldText = shortDescTextField.getText();
        if (StringUtils.isNotBlank(shortDescTextFieldText)) {
            msgBuilder.append(": ").append(shortDescTextFieldText);
        }
        String longDescEditorTextFieldText = longDescEditorTextField.getText();
        if (StringUtils.isNotBlank(longDescEditorTextFieldText)) {
            msgBuilder.append(StringUtils.LF).append(StringUtils.LF).append(longDescEditorTextFieldText);
        }
        String breakingEditorTextFieldText = breakingEditorTextField.getText();
        if (StringUtils.isNotBlank(breakingEditorTextFieldText)) {
            msgBuilder.append(StringUtils.LF).append(StringUtils.LF).append("BREAKING CHANGE: ").append(breakingEditorTextFieldText);
        }
        String closedIssueTextFieldText = closedIssueTextField.getText();
        if (StringUtils.isNotBlank(closedIssueTextFieldText)) {
            msgBuilder.append(StringUtils.LF).append(StringUtils.LF).append("Closes ").append(closedIssueTextFieldText);
        }
        return msgBuilder.append(StringUtils.LF).toString();
    }

    public GitCommitMessageTemplate getGitCommitMessageTemplate() {
        GitCommitMessageTemplate commitMessageTemplate = new GitCommitMessageTemplate();
        String typeSelected = String.valueOf(typeComboBox.getSelectedItem());
        commitMessageTemplate.setType(GitCommitMessageTypeEnum.getTitle(StringUtils.trim(StringUtils.split(typeSelected, "-")[1])));
        commitMessageTemplate.setScope(scopeTextField.getText());
        commitMessageTemplate.setSubject(shortDescTextField.getText());
        commitMessageTemplate.setBody(longDescEditorTextField.getText());
        commitMessageTemplate.setChanges(breakingEditorTextField.getText());
        commitMessageTemplate.setCloses(closedIssueTextField.getText());
        return commitMessageTemplate;
    }

}
