package com.zipwhip.net;

import java.net.Proxy;
import java.net.URI;
import java.util.List;

/**
 * @author Ali Serghini
 *         <p/>
 *         Date: 11/9/12 Time: 10:43 AM
 */
abstract class ProxyConfigFallback implements ProxyConfig {

    final ProxyConfig fallback;

    protected ProxyConfigFallback(final ProxyConfig fallback) {
        this.fallback = fallback;
    }

    @Override
    public List<Proxy> get() {
        return fallback != null ? fallback.get() : null;
    }

    @Override
    public List<Proxy> get(URI uri) {
        return fallback != null ? fallback.get(uri) : null;
    }

    Proxy.Type getProxyType(final String type) {
        final String proxyType;
        if (type == null || type.trim().length() < 1 || (proxyType = type.trim().toLowerCase()).startsWith("http")) {
            return Proxy.Type.HTTP;
        } else if (proxyType.startsWith("sock")) {
            return Proxy.Type.SOCKS;
        } else {
            return Proxy.Type.DIRECT;
        }
    }
}
