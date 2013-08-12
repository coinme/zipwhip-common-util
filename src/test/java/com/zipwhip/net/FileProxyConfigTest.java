package com.zipwhip.net;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Ali Serghini
 * Date: 11/20/12
 * Time: 1:51 PM
 */
public class FileProxyConfigTest {
    private static final FileProxyConfig PROXY_CONFIG = new FileProxyConfig(null);
    private static final String TEST_HTTP_HOST = "10.168.1.12";
    private static final String TEST_SOCKS_HOST = "10.168.1.12";
    private static final String TEST_HTTP_PORT = "8888";
    private static final String TEST_SOCKS_PORT = "8888";

    @Test
    public void testSaveHttp() throws Exception {
        // save http proxy
        PROXY_CONFIG.save(FileProxyConfig.ProxyType.HTTP, TEST_HTTP_HOST, TEST_HTTP_PORT);
        List<Proxy> proxies = PROXY_CONFIG.get();
        assertNotNull(proxies);
        assertEquals(1, proxies.size());
        assertEquals(Proxy.Type.HTTP, proxies.get(0).type());
        InetSocketAddress address = (InetSocketAddress) proxies.get(0).address();
        assertNotNull(address);
        assertEquals(TEST_HTTP_HOST, address.getHostName());
        assertEquals(TEST_HTTP_PORT, Integer.toString(address.getPort()));
    }

    @Test
    public void testSaveAuto() throws Exception {
        //save auto
        PROXY_CONFIG.setDefaultProxy(FileProxyConfig.ProxyType.AUTO);
        List<Proxy> proxies = PROXY_CONFIG.get();
        assertNull(proxies);
    }

    @Test
    public void testSaveSocks() throws Exception {
        // save socks
        PROXY_CONFIG.save(FileProxyConfig.ProxyType.SOCKS, TEST_SOCKS_HOST, TEST_SOCKS_PORT);
        List<Proxy> proxies = PROXY_CONFIG.get(URI.create("sockets://zipwhip.com"));
        assertNotNull(proxies);
        assertEquals(1, proxies.size());
        assertEquals(Proxy.Type.SOCKS, proxies.get(0).type());
        InetSocketAddress address = (InetSocketAddress) proxies.get(0).address();
        assertNotNull(address);
        assertEquals(TEST_SOCKS_HOST, address.getHostName());
        assertEquals(TEST_SOCKS_PORT, Integer.toString(address.getPort()));
    }

}
