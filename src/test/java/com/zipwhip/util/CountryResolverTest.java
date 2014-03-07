package com.zipwhip.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Russ
 * Date: 3/4/14
 * Time: 6:05 PM
 */
public class CountryResolverTest {

    private static CountryResolver countryResolver = new CountryResolver();

    @Test
    public void testIt() {
        // Normal cases
        assertEquals("US", countryResolver.resolve("+12066181111"));
        assertEquals("CA", countryResolver.resolve("+15198365495"));
        assertEquals("FR", countryResolver.resolve("+33344548655"));

        // if not in E164, we'll try it with a +1
        assertEquals("US", countryResolver.resolve("2066181111"));
        assertEquals("CA", countryResolver.resolve("5198365495"));

        // if that doesn't work, it's unknown (ZZ)
        assertEquals("ZZ", countryResolver.resolve("0344548655"));
        assertEquals("ZZ", countryResolver.resolve("+14564568749"));

    }
}
