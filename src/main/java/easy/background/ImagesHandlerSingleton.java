package easy.background;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ImagesHandlerSingleton {

    INSTANCE;

    private MimetypesFileTypeMap typeMap = new MimetypesFileTypeMap();
    private List<String> randomImageList = null;
    private String lastFolder = null;

    /**
     * 获取随机图像（按顺序进行获取的, 每次获取一张图片就一出掉一张图片，清空之后再重新读取）
     *
     * @param folder 文件夹
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/06 17:19
     */
    public String getRandomImage(String folder) {
        if (!folder.equals(lastFolder) || CollectionUtils.isEmpty(randomImageList)) {
            randomImageList = getRandomImageList(folder);
        }
        lastFolder = folder;
        while (CollectionUtils.isNotEmpty(randomImageList) && !isImage(new File(randomImageList.get(0)))) {
            randomImageList.remove(0);
        }
        return CollectionUtils.isEmpty(randomImageList) ? null : randomImageList.remove(0);
    }

    public void resetRandomImageList() {
        if (CollectionUtils.isNotEmpty(randomImageList)) {
            // 改成只做清空处理，等下次运行时重新调用getRandomImageList进行生成
            randomImageList.clear();
        }
    }

    private List<String> getRandomImageList(String folder) {
        if (StringUtils.isBlank(folder)) {
            return null;
        }
        List<String> images = new ArrayList<>();
        // 读取选择文件夹下的图片
        collectImages(images, folder);
        // 重新打乱图片顺序
        Collections.shuffle(images);
        return Collections.synchronizedList(images);
    }

    private void collectImages(List<String> images, String folder) {
        if (StringUtils.isBlank(folder)) {
            return;
        }
        File root = new File(folder);
        if (!root.exists()) {
            return;
        }
        File[] list = root.listFiles();
        if (list == null) {
            return;
        }
        for (File file : list) {
            if (file.isDirectory()) {
                collectImages(images, file.getAbsolutePath());
                continue;
            }
            if (isImage(file)) {
                images.add(file.getAbsolutePath());
            }
        }
    }

    private boolean isImage(File file) {
        String[] parts = typeMap.getContentType(file).split("/");
        return parts.length != 0 && "image".equals(parts[0]);
    }

}
