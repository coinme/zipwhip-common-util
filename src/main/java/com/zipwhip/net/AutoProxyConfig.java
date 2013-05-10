package com.zipwhip.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ali Serghini
 *         <p/>
 *         Date: 11/9/12 Time: 1:31 PM
 */
public class AutoProxyConfig extends ProxyConfigFallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoProxyConfig.class);
    private static final String JAVA_NET_USE_SYSTEM_PROXIES = "java.net.useSystemProxies";
    /**
     * URIs to test with. Order important here
     */
    private static final URI[] TEST_URI = new URI[]{
            URI.create(ProxyUtil.DEFAULT_HTTP_TEST_URL),
            URI.create(ProxyUtil.DEFAULT_HTTPS_TEST_URL),
            URI.create(ProxyUtil.DEFAULT_SOCKETS_TEST_URL)};

    public AutoProxyConfig(final ProxyConfig fallback) {
        super(fallback);
    }

    @Override
    public List<Proxy> get() {
        final List<Proxy> result = new ArrayList<Proxy>(5);
        List<Proxy> proxyList;
        for (URI uri : TEST_URI) {
            proxyList = get(uri);
            if (proxyList != null && !proxyList.isEmpty()) result.addAll(proxyList);
        }

        return result.isEmpty() ? super.get() : result;
    }

    @Override
    public List<Proxy> get(final URI uri) {
        if (uri == null) throw new IllegalArgumentException("URI cannot be be null");

        enableProxySettings();
        final List<Proxy> proxies = cleanupProxy(ProxySelector.getDefault().select(uri));
        LOGGER.debug("==> Proxy detected: " + proxies);

        return proxies == null || proxies.isEmpty() ? super.get(uri) : proxies;
    }

    private void enableProxySettings() {
        final String useSystemProxy = System.getProperty(JAVA_NET_USE_SYSTEM_PROXIES);
        if (useSystemProxy == null || !"true".equals(useSystemProxy.trim().toLowerCase())) {
            System.setProperty(JAVA_NET_USE_SYSTEM_PROXIES, "true");
        }
    }

    private List<Proxy> cleanupProxy(final List<Proxy> proxies) {
        if (proxies == null || proxies.isEmpty()) return null;

        final List<Proxy> result = new ArrayList<Proxy>(proxies.size());
        for (Proxy proxy : proxies) {
            if (proxy != null && !Proxy.Type.DIRECT.equals(proxy.type())) result.add(proxy);
        }

        return result.isEmpty() ? null : result;
    }
}
