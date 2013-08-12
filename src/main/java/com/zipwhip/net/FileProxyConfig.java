package com.zipwhip.net;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ali Serghini
 */
public class FileProxyConfig extends ProxyConfigFallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileProxyConfig.class);
    public static final String DEFAULT_PROXY_CONFIG_FILE_NAME = "proxy.properties";
    private static final String KEY_PROXY_DEFAULT = "proxy.default";
    private PropertiesConfiguration propertiesConfiguration = null;
    private final File proxyConfigFile;

    public enum ProxyType {

        AUTO, HTTP, SOCKS;
        private static final Map<String, ProxyType> MAPPING = new HashMap<String, ProxyType>(3);

        static {
            MAPPING.put(AUTO.toString(), AUTO);
            MAPPING.put(HTTP.toString(), HTTP);
            MAPPING.put(SOCKS.toString(), SOCKS);
        }

        /**
         * Converts a string (case sensitive) to its equivalent ProxyType.
         *
         * @param type - proxy type as string
         * @return ProxyType
         * @throws <code>IllegalArgumentException</code>
         *          if invalid type.
         */
        public static ProxyType toEnum(final String type) {
            if (type == null || type.length() < 1) throw new IllegalArgumentException("Invalid proxy type. cannot be null or empty: " + type);
            if (!MAPPING.containsKey(type))
                throw new IllegalArgumentException("Invalid proxy type. Type need to be one of the following values [AUTO, HTTP, SOCKS]. Current type: " + type);

            return MAPPING.get(type);
        }


    }

    public FileProxyConfig(final ProxyConfig fallback) {
        this(new File(DEFAULT_PROXY_CONFIG_FILE_NAME), fallback);
    }

    public FileProxyConfig(final File proxyConfigFile, final ProxyConfig fallback) {
        super(fallback);

        this.proxyConfigFile = proxyConfigFile != null ? proxyConfigFile : new File(DEFAULT_PROXY_CONFIG_FILE_NAME);
        init();
    }

    private void init() {
        // Create the config file if it does not exist
        if (!this.proxyConfigFile.exists()) {
            try {
                final boolean success = this.proxyConfigFile.createNewFile();
                if (success) LOGGER.debug(String.format("==> Proxy config file created successfully [%s]", this.proxyConfigFile.getAbsolutePath()));
            } catch (IOException ex) {
                throw new IllegalStateException(String.format("Failed to create proxy config file [%s]", this.proxyConfigFile.getAbsolutePath()), ex);
            }
        } else {
            LOGGER.debug(String.format("==> Proxy config file exists [%s]", this.proxyConfigFile.getAbsolutePath()));
        }
    }

    @Override
    public List<Proxy> get() {
        if (!isProxyEnabled()) return super.get();

        final Proxy httpProxy = read(ProxyType.HTTP);
        List<Proxy> proxies = null;
        if (httpProxy != null) {
            proxies = new ArrayList<Proxy>(1);
            proxies.add(httpProxy);
        }

        return proxies == null ? super.get() : proxies;
    }

    @Override
    public List<Proxy> get(final URI uri) {
        if (!isProxyEnabled()) return super.get(uri);

        final String scheme;
        if (uri.getScheme() == null || (scheme = uri.getScheme().trim().toUpperCase()).length() < 1) return super.get(uri);

        List<Proxy> proxies = null;
        final Proxy proxy = read(scheme.startsWith(ProxyType.HTTP.toString()) ? ProxyType.HTTP : ProxyType.SOCKS);
        LOGGER.debug("==> Proxy detected: " + proxies);
        if (proxy != null) {
            proxies = new ArrayList<Proxy>(1);
            proxies.add(proxy);
        }

        return proxies == null ? super.get() : proxies;
    }

    /**
     * Reads the proxy properties from disk
     *
     * @param proxyType - proxy type
     */
    public Proxy read(final ProxyType proxyType) {
        if (proxyType == null || proxyType.equals(ProxyType.AUTO)) return null;

        final String prefix = proxyType.toString();
        final String hostKey;
        final String portKey;
        if (!getPropertiesConfig().containsKey((hostKey = prefix + ".host")) || !getPropertiesConfig().containsKey((portKey = prefix + ".port"))) return null;

        final String host = getPropertiesConfig().getString(hostKey, null);
        final String port = getPropertiesConfig().getString(portKey, null);
        return (host == null || host.trim().length() < 1 || port == null || port.trim().length() < 1) ? null : new Proxy(getProxyType(prefix), InetSocketAddress.createUnresolved(host, Integer.parseInt(port)));
    }

    protected PropertiesConfiguration getPropertiesConfig() {
        if (propertiesConfiguration != null) return propertiesConfiguration;

        try {
            propertiesConfiguration = new PropertiesConfiguration(proxyConfigFile);
            propertiesConfiguration.setAutoSave(true);
        } catch (ConfigurationException ex) {
            throw new IllegalStateException(String.format("Failed to initialize proxy config file [%s]", this.proxyConfigFile.getAbsolutePath()), ex);
        }
        return propertiesConfiguration;
    }

    protected boolean isProxyEnabled() {
        return !getDefaultProxy().equals(ProxyType.AUTO);
    }

    /**
     * Reads the default proxy from the config file. default = AUTO
     *
     * @return default proxy.
     */
    public ProxyType getDefaultProxy() {
        final String defaultProxy = getPropertiesConfig().getString(KEY_PROXY_DEFAULT, null);
        return defaultProxy != null ? ProxyType.toEnum(defaultProxy.toUpperCase()) : ProxyType.AUTO;
    }


    /**
     * Sets the default proxy in the config file.
     */
    protected void setDefaultProxy(final ProxyType proxyType) {
        getPropertiesConfig().setProperty(KEY_PROXY_DEFAULT, proxyType.toString());
    }

    /**
     * Saves the proxy properties to disk
     *
     * @param proxyType - proxy type
     * @param host      - proxy host
     * @param port      - proxy port
     * @throws UnknownHostException
     */
    public void save(final ProxyType proxyType, final String host, final String port) throws UnknownHostException {
        if (proxyType.equals(ProxyType.AUTO)) {
            setDefaultProxy(proxyType);
            return;
        }

        if (!isValidHost(host)) throw new UnknownHostException(String.format("Invalid Host: %s", host));
        if (!isValidPort(port)) throw new UnknownHostException(String.format("Invalid Port: %s", port));

        // Set proxy properties
        setDefaultProxy(proxyType);
        getPropertiesConfig().setProperty(proxyType.toString() + ".host", host);
        getPropertiesConfig().setProperty(proxyType.toString() + ".port", port);
    }


    private boolean isValidHost(final String host) {
        if (host == null || host.length() < 1) return false;

        try {
            InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            LOGGER.debug("==> Failed to resolve host: " + host, e);
            return false;
        }
        return true;
    }

    private boolean isValidPort(final String port) {
        if (!NumberUtils.isDigits(port)) return false;

        final int intPort = Integer.parseInt(port);
        return intPort <= 65535 && intPort >= 0;
    }
}
