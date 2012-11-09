package com.zipwhip.format;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 11/7/12
 * Time: 10:25 AM
 *
 * The multipart formatter does splitting on commas
 */
public class MultipartFormatterAdapterTest {

    MultipartFormatterAdapter formatterAdapter = new MultipartFormatterAdapter(new Formatter<String>() {
        @Override
        public String format(String input) {
            if (input == null){
                return "A";
            }

            return input + "A";
        }
    });

    @Test
    public void testName() throws Exception {
        String result = formatterAdapter.format("A");
        assertEquals(result, "AA");

        result = formatterAdapter.format(null);
        assertEquals(result, "A");

        result = formatterAdapter.format(",A,B");
        assertEquals(result, ",AA,BA");

        result = formatterAdapter.format("A,B");
        assertEquals(result, "A,BA");

        result = formatterAdapter.format("");
        assertEquals(result, "A");

        result = formatterAdapter.format(",,");
        assertEquals(result, ""); // input.split is kind of buggy?

        result = formatterAdapter.format("A,");
        assertEquals(result, "A,A");
    }
}
