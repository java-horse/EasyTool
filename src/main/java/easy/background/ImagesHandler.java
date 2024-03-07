package easy.background;

import cn.hutool.core.util.ArrayUtil;
import com.intellij.ide.util.PropertiesComponent;
import easy.base.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ImagesHandler {

    INSTANCE;

    private final MimetypesFileTypeMap typeMap = new MimetypesFileTypeMap();
    private List<String> randomImageList = null;
    private List<String> swapImageList = Collections.synchronizedList(new ArrayList<>());
    private String lastFolder = null;

    /**
     * 获取随机图像
     * 随机规则：启动时和文件夹变更时或者2个文件夹数据全部为空时，重新读取图片列表，其他情况下只进行图片资源的呼互换（节省IO资源）
     *
     * @param folder 文件夹
     * @return {@link java.lang.String }
     * @author mabin
     * @date 2024/03/06 17:19
     */
    public String getRandomImage(String folder) {
        if (!StringUtils.equals(folder, lastFolder) || StringUtils.isBlank(lastFolder)
                || (CollectionUtils.isEmpty(randomImageList) && CollectionUtils.isEmpty(swapImageList))) {
            randomImageList = readImageFolderList(folder);
            lastFolder = folder;
        } else if (CollectionUtils.isEmpty(randomImageList) && CollectionUtils.isNotEmpty(swapImageList)) {
            randomImageList.addAll(swapImageList);
            swapImageList.clear();
        }
        if (CollectionUtils.isEmpty(randomImageList)) {
            return null;
        }
        String displayImage = randomImageList.remove(0);
        swapImageList.add(displayImage);
        return displayImage;
    }

    /**
     * 重置随机图像列表
     * 重置规则：直接进行图片资源互换并且打乱元素顺序，不再重新读取图片列表（节省IO资源）
     *
     * @author mabin
     * @date 2024/03/07 16:24
     */
    public void resetRandomImageList() {
        if (CollectionUtils.isNotEmpty(randomImageList)) {
            for (String image : randomImageList) {
                if (!swapImageList.contains(image)) {
                    swapImageList.add(image);
                }
            }
            Collections.shuffle(swapImageList);
            randomImageList.clear();
        } else if (CollectionUtils.isNotEmpty(swapImageList)) {
            Collections.shuffle(swapImageList);
        }
    }

    /**
     * 清空随机图像列表
     *
     * @author mabin
     * @date 2024/03/07 16:41
     */
    public void clearRandomImageList() {
        if (CollectionUtils.isNotEmpty(randomImageList)) {
            randomImageList.clear();
        }
        if (CollectionUtils.isNotEmpty(swapImageList)) {
            swapImageList.clear();
        }
    }

    private List<String> readImageFolderList(String folder) {
        if (StringUtils.isBlank(folder)) {
            return null;
        }
        List<String> images = new ArrayList<>();
        // 读取选择文件夹下的图片
        collectImages(images, folder);
        // 重新打乱图片顺序
        if (CollectionUtils.isNotEmpty(images)) {
            Collections.shuffle(images);
            return Collections.synchronizedList(images);
        }
        return images;
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
        ArrayUtil.shuffle(list);
        int imageCount = PropertiesComponent.getInstance().getInt(Constants.Persistence.BACKGROUND_IMAGE.IMAGE_COUNT, Constants.NUM.FIVE);
        for (File file : list) {
            if (file.isDirectory()) {
                collectImages(images, file.getAbsolutePath());
                continue;
            }
            if (isImage(file)) {
                images.add(file.getAbsolutePath());
            }
            if (images.size() >= imageCount) {
                break;
            }
        }
    }

    private boolean isImage(File file) {
        String[] parts = typeMap.getContentType(file).split("/");
        return parts.length != 0 && "image".equals(parts[0]);
    }

}
