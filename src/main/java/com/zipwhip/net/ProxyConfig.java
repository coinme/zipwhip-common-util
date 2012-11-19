package com.zipwhip.net;

import java.net.Proxy;
import java.net.URI;
import java.util.List;

/**
 * User: Ali Serghini
 * Date: 11/6/12
 * Time: 4:31 PM
 */
public interface ProxyConfig {

    /**
     * Returns the best available proxies for HTTP.
     *
     * @return list of proxies
     */
    public List<Proxy> get();

    /**
     * Returns the best available proxies for the URI.
     *
     * @return list of proxies
     */
    public List<Proxy> get(URI uri);
}
