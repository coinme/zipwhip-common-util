package com.zipwhip.net;

import com.zipwhip.util.StringUtil;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * @author Ali Serghini
 *         <p/>
 *         Date: 11/5/12 Time: 11:04 AM
 *         <p/>
 *         Proxy detection and settings.
 */
public class ProxyUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUtil.class);
    private static final String HTTP_PROXY_HOST = "http.proxyHost";
    private static final String HTTP_PROXY_PORT = "http.proxyPort";
    private static final String HTTPS_PROXY_HOST = "https.proxyHost";
    private static final String HTTPS_PROXY_PORT = "https.proxyPort";
    private static final String SOCKS_PROXY_HOST = "socksProxyHost";
    private static final String SOCKS_PROXY_PORT = "socksProxyPort";
    private static final String FTP_PROXY_HOST = "ftp.proxHost";
    private static final String FTP_PROXY_PORT = "ftp.proxyPort";
    public static final String DEFAULT_HTTP_TEST_URL = "http://network.zipwhip.com";
    public static final String DEFAULT_HTTPS_TEST_URL = "https://network.zipwhip.com";
    public static final String DEFAULT_SOCKETS_TEST_URL = "socket://network.zipwhip.com";
    private static final ProxyConfig PROXY_CONFIG = new FileProxyConfig(new AutoProxyConfig(null));

    private ProxyUtil() {/*
         * Utility class
         */
    }

    /**
     * Attempts to detect the proxy settings
     *
     * @return list of proxies, null if none detected
     */
    public static List<Proxy> getProxy() {
        return PROXY_CONFIG.get();
    }

    public static boolean testInternetAccess() {
        try {
            return testInternetAccess(Proxy.NO_PROXY, new URL(DEFAULT_HTTP_TEST_URL));
        } catch (MalformedURLException ex) {
            LOGGER.error("==X Failed to test internet access", ex);
        }

        return false;
    }

    public static boolean testInternetAccess(final URL httpUrl) {
        return testInternetAccess(Proxy.NO_PROXY, httpUrl);
    }

    public static boolean testInternetAccess(final Proxy proxy, final URL httpUrl) {
        if (proxy == null) {
            throw new IllegalArgumentException("Proxy cannot be null");
        }
        if (httpUrl == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }

        LOGGER.debug("==> Testing Internet access to: " + httpUrl + ", using proxy: " + proxy);
        HttpURLConnection httpConn = null;
        try {
            // try to make a HEAD request for this URL
            final URLConnection urlConn = httpUrl.openConnection(proxy);
            LOGGER.debug("==> Opened connection to: " + httpUrl + ", using proxy: " + proxy);
            if (!(urlConn instanceof HttpURLConnection)) {
                return false;
            }

            httpConn = (HttpURLConnection) urlConn;
            httpConn.setRequestMethod("HEAD");
            httpConn.setUseCaches(false);
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(11000);
            httpConn.connect();
            LOGGER.debug("==> Connected to: " + httpUrl + ", using proxy: " + proxy);

            // Any response will do. as long as we are not getting an exception we should be fine.
            return true;
        } catch (IOException e) {
            LOGGER.error("==X Failed HEAD request to: " + httpUrl + ", using proxy: " + proxy, e);
        } finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        return false;
    }

    /**
     * Configures the JVM HTTP proxy system properties.
     *
     * @param host - proxy host
     * @param port - proxy port
     */
    public static void setHttpProxyProperties(final String host, final int port) {
        setProxyProperties(HTTP_PROXY_HOST, host, HTTP_PROXY_PORT, port);
    }

    /**
     * Configures the JVM HTTP proxy system properties with default port (80).
     *
     * @param host - proxy host
     */
    public static void setHttpProxyProperties(final String host) {
        setHttpProxyProperties(host, 80);
    }

    /**
     * Configures the JVM HTTPS proxy system properties.
     *
     * @param host - proxy host
     * @param port - proxy port
     */
    public static void setHttpsProxyProperties(final String host, final int port) {
        setProxyProperties(HTTPS_PROXY_HOST, host, HTTPS_PROXY_PORT, port);
    }

    /**
     * Configures the JVM HTTPS proxy system properties with default port (443).
     *
     * @param host - proxy host
     */
    public static void setHttpsProxyProperties(final String host) {
        setHttpsProxyProperties(host, 443);
    }

    /**
     * Configures the JVM SOCKS proxy system properties.
     *
     * @param host - proxy host
     * @param port - proxy port
     */
    public static void setSocksProxyProperties(final String host, final int port) {
        setProxyProperties(SOCKS_PROXY_HOST, host, SOCKS_PROXY_PORT, port);
    }

    /**
     * Configures the JVM SOCKS proxy system properties with default port
     * (1080).
     *
     * @param host - proxy host
     */
    public static void setSocksProxyProperties(final String host) {
        setHttpsProxyProperties(host, 1080);
    }

    /**
     * Configures the JVM FTP proxy system properties.
     *
     * @param host - proxy host
     * @param port - proxy port
     */
    public static void setFtpProxyProperties(final String host, final int port) {
        setProxyProperties(FTP_PROXY_HOST, host, FTP_PROXY_PORT, port);
    }

    /**
     * Configures the JVM FTP proxy system properties with default port (80).
     *
     * @param host - proxy host
     */
    public static void setFtpProxyProperties(final String host) {
        setHttpsProxyProperties(host, 80);
    }

    /**
     * Configures the JVM proxy system properties.
     *
     * @param host -
     * @param port -
     */
    private static void setProxyProperties(final String hostProp, final String host, final String portProp, final int port) {
        if (!StringUtil.isNullOrEmpty(host)) {
            System.setProperty(hostProp, host);
            if (port > -1) {
                System.setProperty(portProp, Integer.toString(port));
            }
            LOGGER.debug("==> Using system proxy [host: " + host + " and port" + port + "]");
        }
    }

    /**
     * Clear the JVM proxy system properties.
     */
    public static void clearProxySettings() {
        System.clearProperty(HTTP_PROXY_HOST);
        System.clearProperty(HTTP_PROXY_PORT);
        System.clearProperty(HTTPS_PROXY_HOST);
        System.clearProperty(HTTPS_PROXY_PORT);
        System.clearProperty(SOCKS_PROXY_HOST);
        System.clearProperty(SOCKS_PROXY_PORT);
        System.clearProperty(FTP_PROXY_HOST);
        System.clearProperty(FTP_PROXY_PORT);
        LOGGER.debug("==> Clearing proxy settings");
    }
}
