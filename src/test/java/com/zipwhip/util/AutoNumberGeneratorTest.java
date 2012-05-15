package com.zipwhip.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:55 PM
 *
 * Simple validation
 */
public class AutoNumberGeneratorTest {

    @Test
    public void testIncrementing() throws Exception {

        Generator<Long> generator = new AutoNumberGenerator();

        assertNotNull(generator.next()); //1
        assertNotNull(generator.next()); //2
        assertNotNull(generator.next()); //3

        assertEquals(generator.next(), (Long)4L);

        assertNotNull(generator.next()); //5
        assertNotNull(generator.next()); //6
        assertNotNull(generator.next()); //7

        assertEquals(generator.next(), (Long)8L);

    }
}
