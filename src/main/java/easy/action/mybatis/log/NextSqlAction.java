package easy.action.mybatis.log;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.ex.MarkupIterator;
import com.intellij.openapi.editor.ex.MarkupModelEx;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import org.jetbrains.annotations.NotNull;

public class NextSqlAction extends JumpSqlAction {

    public NextSqlAction(ConsoleViewImpl consoleView) {
        super("Next SQL", "Next SQL", AllIcons.Actions.NextOccurence, consoleView);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        int offset = editor.getCaretModel().getPrimaryCaret().getOffset() + 2;
        int textLength = editor.getDocument().getTextLength();
        if (offset >= textLength) {
            return;
        }
        int movedOffset = jump(offset, textLength, true);
        if (movedOffset > -1 && (e.getInputEvent().isShiftDown())) {
                editor.getSelectionModel().setSelection(offset - 2, movedOffset);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(hasNext());
    }

    @Override
    protected boolean isValid(RangeHighlighterEx next, int startOffset, int endOffset) {
        return super.isValid(next, startOffset, endOffset) && startOffset >= 0 && editor.getDocument().getLineNumber(startOffset) != editor.getDocument().getLineNumber(next.getStartOffset());
    }

    private boolean hasNext() {
        int offset = editor.getCaretModel().getPrimaryCaret().getOffset() + 2;
        int textLength = editor.getDocument().getTextLength();

        if (offset >= textLength) {
            return false;
        }

        MarkupModelEx model = (MarkupModelEx) editor.getMarkupModel();
        MarkupIterator<RangeHighlighterEx> iterator = model.overlappingIterator(offset, textLength);
        try {
            return iterator.hasNext() && isValid(iterator.next(), offset, textLength);
        } finally {
            iterator.dispose();
        }

    }

}
