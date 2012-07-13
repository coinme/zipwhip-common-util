package com.zipwhip.util;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: jed
 * Date: 8/29/11
 * Time: 12:13 PM
 */
public class StringUtilTest {

	@Test
	public void testEquals() throws Exception {
		String s1 = "S1";
		String s2 = "S2";
		String s3 = "S1";

		Assert.assertTrue(StringUtil.equals(null, null));
		Assert.assertTrue(StringUtil.equals(s1, s1));
		Assert.assertFalse(StringUtil.equals(null, s1));
		Assert.assertFalse(StringUtil.equals(s1, null));
		Assert.assertFalse(StringUtil.equals(s1, s2));
		Assert.assertTrue(StringUtil.equals(s1, s3));
	}

	@Test
	public void testEqualsIgnoreCase() throws Exception {

	}

	@Test
	public void testSplit() throws Exception {

	}

	@Test
	public void testSafeCleanMobileNumber() throws Exception {

		String alphaNumerics = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890`~!@#$%^&*()-_={[}]|\\;:'\",<.>/?\n\t ";
		String cleaned = StringUtil.cleanMobileNumber(alphaNumerics);
		assertEquals("1234567890", cleaned);

		cleaned = StringUtil.cleanMobileNumber("+84" + alphaNumerics);
		assertEquals("+841234567890", cleaned);

		assertEquals("2065551212", StringUtil.cleanMobileNumber("2065551212"));
		assertEquals("2065551212", StringUtil.cleanMobileNumber("206 555 1212"));
		assertEquals("2065551212", StringUtil.cleanMobileNumber("206-555-1212"));
		assertEquals("+842065551212", StringUtil.cleanMobileNumber("+84206-555-1212"));
		assertEquals("+842065551212", StringUtil.cleanMobileNumber("+84206 555 1212"));
		assertEquals("+842065551212", StringUtil.cleanMobileNumber("+842065551212"));
		assertEquals("+639281942713", StringUtil.cleanMobileNumber("+639281942713"));
		assertEquals("+84904695334", StringUtil.safeCleanMobileNumber("++++8490++++4695334+", true));

		assertEquals("2065551212", StringUtil.cleanMobileNumber("+12065551212"));
		assertEquals("2065551212", StringUtil.cleanMobileNumber("+1206 555 1212"));
		assertEquals("2065551212", StringUtil.cleanMobileNumber("+1206-555-1212"));
	}

	@Test
	public void testExists() throws Exception {

	}

	@Test
	public void testIsNullOrEmpty() throws Exception {

	}

	@Test
	public void testDefaultValue() throws Exception {

	}

	@Test
	public void testReplaceAll() throws Exception {

	}

	@Test
	public void testReplace() throws Exception {

	}

	@Test
	public void testStartsWith() throws Exception {

	}

	@Test
	public void testContains() throws Exception {

	}

	@Test
	public void testJoin() throws Exception {

	}

	@Test
	public void testParseBoolean() throws Exception {

	}

	@Test
	public void testContainsExtendedChars() throws Exception {

	}

	@Test
	public void testStripNonValidXMLCharacters() throws Exception {

	}

	@Test
	public void testConvertPatterns() throws Exception {

	}

	@Test
	public void testJoinAfter() throws Exception {

	}

	@Test
	public void testStripStringNull() throws Exception {

	}

}
