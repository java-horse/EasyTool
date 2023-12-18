package easy.restful.icons;

import easy.restful.api.HttpMethod;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public abstract class IconType {

    /**
     * 默认显示
     *
     * @param method method
     * @return default
     */
    @NotNull
    public abstract Icon getDefaultIcon(HttpMethod method);

    /**
     * 选中图标
     *
     * @param method method
     * @return select
     */
    @NotNull
    public abstract Icon getSelectIcon(HttpMethod method);

    /**
     * 获取默认图标列表
     *
     * @return list
     */
    @NotNull
    public abstract List<PreviewIcon> getDefaultIcons();

    /**
     * 获取选中图标列表
     *
     * @return list
     */
    @NotNull
    public abstract List<PreviewIcon> getSelectIcons();

    /**
     * 图标名
     *
     * @return name
     */
    @Override
    @NotNull
    public abstract String toString();

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return toString().equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static class IconComparator implements Comparator<PreviewIcon> {

        @Override
        public int compare(@NotNull PreviewIcon o1, @NotNull PreviewIcon o2) {
            char[] chars1 = o1.getText().toCharArray();
            char[] chars2 = o2.getText().toCharArray();

            int maxLen = Math.min(chars1.length, chars2.length);
            for (int i = 0; i < maxLen; i++) {
                if (chars1[i] != chars2[i]) {
                    return chars1[i] - chars2[i];
                }
            }

            return chars1.length - chars2.length;
        }
    }
}
