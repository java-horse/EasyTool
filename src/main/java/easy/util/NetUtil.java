package easy.util;

import com.intellij.openapi.diagnostic.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

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

    private NetUtil() {
    }

    /**
     * 获取本机IP
     *
     * @param
     * @return java.lang.String
     * @author mabin
     * @date 2023/11/15 10:10
     */
    public static String getIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(':') == -1) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取本机IP异常" + e.getMessage());
        }
        return null;
    }

}
