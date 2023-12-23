package easy.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络相关处理
 *
 * @project: EasyChar
 * @package: easy.util
 * @author: mabin
 * @date: 2023/11/15 10:02:07
 */
public class NetUtil {

    private static final Logger log = Logger.getInstance(NetUtil.class);

    private static Map<String, String> ipRegionMap = new ConcurrentHashMap<>(16);

    private NetUtil() {
    }

    /**
     * 获取网络ip
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/17 17:07
     */
    public static String getIp() {
        try {
            return HttpUtil.doGet("https://api.ipify.org");
        } catch (Exception e) {
            log.warn("获取网络ip异常: " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取ip区域信息
     *
     * @param ip
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/17 17:13
     */
    public static String getIpRegion(String ip) {
        try {
            if (ipRegionMap.containsKey(ip) && !StringUtils.equals(ipRegionMap.get(ip), ip)) {
                return ipRegionMap.get(ip);
            }
            String response = HttpUtil.doGet("http://ip-api.com/json/" + ip);
            if (StringUtils.isBlank(response)) {
                return null;
            }
            ipRegionMap.put(ip, response);
            return response;
        } catch (Exception e) {
            log.warn("获取ip区域信息异常: " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取本机IP
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/15 10:10
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    log.warn("ip: " + inetAddress.getHostAddress());
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(':') == -1) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取本机IP异常" + e.getMessage());
        }
        return null;
    }

    /**
     * 根据配置文件获取项目端口号
     *
     * @param project
     * @param containingFile
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/23 11:55
     */
    public static String getPortByConfigFile(Project project, PsiFile containingFile) {
        String portByYmlFile = getPortByYamlFile("application.yml", project, containingFile);
        if (StringUtils.isNotBlank(portByYmlFile)) {
            return portByYmlFile;
        }
        String portByYamlFile = getPortByYamlFile("application.yaml", project, containingFile);
        if (StringUtils.isNotBlank(portByYamlFile)) {
            return portByYamlFile;
        }
        String portByPropertiesFile = getPortByPropertiesFile(project, containingFile);
        if (StringUtils.isNotBlank(portByPropertiesFile)) {
            return portByPropertiesFile;
        }
        return null;
    }

    /**
     * 解析properties配置文件
     *
     * @param project
     * @param containingFile
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/23 11:56
     */
    private static String getPortByPropertiesFile(Project project, PsiFile containingFile) {
        Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName("application.properties", GlobalSearchScope.projectScope(project));
        if (CollectionUtils.isEmpty(virtualFiles)) {
            return null;
        }
        String backPort = null;
        for (VirtualFile virtualFile : virtualFiles) {
            try {
                Properties properties = new Properties();
                properties.load(virtualFile.getInputStream());
                String port = properties.getProperty("server.port");
                if (StringUtils.isBlank(port)) {
                    continue;
                }
                if (containingFile != null) {
                    String path = containingFile.getVirtualFile().getPath();
                    String configFilePath = virtualFile.getPath();
                    String projectBasePath1 = getProjectBasePath(path);
                    String projectBasePath2 = getProjectBasePath(configFilePath);
                    if (projectBasePath1.equals(projectBasePath2)) {
                        return port;
                    }
                }
                if (backPort == null) {
                    backPort = port;
                }
            } catch (IOException e) {
                return backPort;
            }
        }
        return backPort;
    }

    /**
     * 解析yaml配置文件
     *
     * @param name
     * @param project
     * @param containingFile
     * @return java.lang.String
     * @author mabin
     * @date 2023/12/23 12:15
     */
    private static String getPortByYamlFile(String name, Project project, PsiFile containingFile) {
        Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(name, GlobalSearchScope.projectScope(project));
        if (CollectionUtils.isEmpty(virtualFiles)) {
            return null;
        }
        String backPort = null;
        for (VirtualFile virtualFile : virtualFiles) {
            try {
                Yaml yaml = new Yaml();
                Map<String, Object> map = yaml.load(virtualFile.getInputStream());
                if (MapUtils.isEmpty(map)) {
                    continue;
                }
                Object serverObj = map.get("server");
                if (!(serverObj instanceof Map)) {
                    continue;
                }
                Map server = (Map) serverObj;
                Object portObj = server.get("port");
                if (Objects.isNull(portObj)) {
                    continue;
                }
                String port = portObj.toString();
                if (containingFile != null) {
                    String path = containingFile.getVirtualFile().getPath();
                    if (getProjectBasePath(path).equals(getProjectBasePath(virtualFile.getPath()))) {
                        return port;
                    }
                }
                if (backPort == null) {
                    backPort = port;
                }
            } catch (Exception ignored) {
                return backPort;
            }
        }
        return backPort;
    }

    private static String getProjectBasePath(String path) {
        int indexOf = path.indexOf("src/");
        if (indexOf != -1) {
            return path.substring(0, path.indexOf("src/"));
        }
        return StringUtils.EMPTY;
    }

}
