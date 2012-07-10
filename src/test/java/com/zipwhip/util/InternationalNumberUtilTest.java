package com.zipwhip.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jed
 * Date: 7/9/12
 * Time: 1:39 PM
 */
public class InternationalNumberUtilTest {

    public void exampleOfInternationalNumberFormatting() {

        /*******************************************************************************************************************************************************
         * Example #1 - Zipwhip Domestic (ZD) number
         */
        String userMobileZipwhipDomestic = "2067808080";

        // The region will be 'US'. This call handles e.164 and ZD numbers. If all else fails 'ZZ' is returned which libphonenumber recognises as UNKNOWN_REGION
        String userRegion = InternationalNumberUtil.getRegionCode(userMobileZipwhipDomestic);

        // A local contact number
        String userFriendNational = "5074527878";

        // This string will look like '(507) 452-7878' -- THE MOST IMPORTANT ASPECT OF THIS CALL IS THAT userRegion IS THE USER'S REGION, NOT THE FRIEND'S
        String userFriendNationalFormatted = InternationalNumberUtil.getRegionallyFormattedNumber(userFriendNational, userRegion);

        // An international contact number
        String userFriendInternational = "+841234567890";

        // This string will look like '+84 123 456 7890' -- THE MOST IMPORTANT ASPECT OF THIS CALL IS THAT userRegion IS THE USER'S REGION, NOT THE FRIEND'S
        String userFriendInternationalFormatted = InternationalNumberUtil.getRegionallyFormattedNumber(userFriendInternational, userRegion);

        /*******************************************************************************************************************************************************
         * Example #2 - International (e.164) number
         */
        String userMobileInternational = "+841234567890";

        // The region will be 'VN'.
        userRegion = InternationalNumberUtil.getRegionCode(userMobileInternational);

        // A local contact number
        userFriendNational = "5074527878";

        // This string will look like '0507 452 3253' -- THE MOST IMPORTANT ASPECT OF THIS CALL IS THAT userRegion IS THE USER'S REGION, NOT THE FRIEND'S
        userFriendNationalFormatted = InternationalNumberUtil.getRegionallyFormattedNumber(userFriendNational, userRegion);
    }

    @Test
    public void testGetRegionCode() throws Exception {

        String phoneNumber = "841234567890";
        assertEquals(InternationalNumberUtil.UNKNOWN_REGION, InternationalNumberUtil.getRegionCode(phoneNumber)); // Without the '+' we get unknown
        assertEquals("VN", InternationalNumberUtil.getRegionCode(InternationalNumberUtil.PLUS + phoneNumber));

        phoneNumber = "2065558888";
        assertEquals("US", InternationalNumberUtil.getRegionCode(phoneNumber)); // Method adds the '+' and the '1'
        assertEquals("US", InternationalNumberUtil.getRegionCode(InternationalNumberUtil.PLUS + "1" + phoneNumber));

        phoneNumber = "7787882706"; // Canada
        assertEquals("CA", InternationalNumberUtil.getRegionCode(phoneNumber)); // Method adds the '+' and the '1'
        assertEquals("CA", InternationalNumberUtil.getRegionCode(InternationalNumberUtil.PLUS + "1" + phoneNumber));

        phoneNumber = "6846999805"; // American Samoa
        assertEquals("AS", InternationalNumberUtil.getRegionCode(phoneNumber)); // Method adds the '+' and the '1'
        assertEquals("AS", InternationalNumberUtil.getRegionCode(InternationalNumberUtil.PLUS + "1" + phoneNumber));

        phoneNumber = "351911691200"; // Test a Portuguese with a 3 digit country code '351'
        assertEquals("PT", InternationalNumberUtil.getRegionCode(InternationalNumberUtil.PLUS + phoneNumber));

        phoneNumber = "447836191191"; // Test a UK number
        assertEquals("GB", InternationalNumberUtil.getRegionCode(InternationalNumberUtil.PLUS + phoneNumber));
    }

    @Test
    public void testIsValidInternationalNumber() throws Exception {

        // Test a number from Vietnam
        String phoneNumber = "841234567890";
        assertFalse(InternationalNumberUtil.isValidInternationalNumber(phoneNumber)); // Without the '+' it is not valid
        assertTrue(InternationalNumberUtil.isValidInternationalNumber(InternationalNumberUtil.PLUS + phoneNumber));

        // Test a 'Zipwhip domestic' number
        phoneNumber = "2065558888";
        assertFalse(InternationalNumberUtil.isValidInternationalNumber(phoneNumber)); // Without the '+' it is not valid
        assertFalse(InternationalNumberUtil.isValidInternationalNumber(InternationalNumberUtil.PLUS + phoneNumber)); // Without the '+' anf the '1' it is not valid
        assertTrue(InternationalNumberUtil.isValidInternationalNumber(InternationalNumberUtil.PLUS + "1" + phoneNumber));
    }

    @Test
    public void testIsValidZipwhipDomesticNumber() throws Exception {

        /**
         * These tests are redundant at the time of writing, but if we were to switch to using
         * libphonenumber to validate region, instead of just length and format this would be useful.
         */

        // Invalid
        String phoneNumber = "20655588";
        assertFalse(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));
        phoneNumber = "206555888a";
        assertFalse(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Test a US number
        phoneNumber = "2065558888";
        assertFalse(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(InternationalNumberUtil.PLUS + "1" + phoneNumber));
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Test a Canadian number
        phoneNumber = "7787882706";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // American Samoa - +1 684
        phoneNumber = "6845558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Anguilla - +1 264
        phoneNumber = "2645558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Antigua and Barbuda - +1 268
        phoneNumber = "2685558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Bahamas - +1 242
        phoneNumber = "2425558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Barbados - +1 246
        phoneNumber = "2465558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Bermuda - +1 441
        phoneNumber = "4415558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // British Virgin Islands - +1 284
        phoneNumber = "2845558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Cayman Islands - +1 345
        phoneNumber = "3455558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Dominica - +1 767
        phoneNumber = "2065558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Dominican Republic - +1 809, +1 829, +1 849
        phoneNumber = "8095558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));
        phoneNumber = "8295558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));
        phoneNumber = "8495558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Grenada - +1 473
        phoneNumber = "4735558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Guam - +1 671
        phoneNumber = "6715558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Jamaica - +1 876
        phoneNumber = "8765558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Montserrat - +1 664
        phoneNumber = "6645558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Northern Mariana Islands - +1 670
        phoneNumber = "6705558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Puerto Rico - +1 787, +1 939
        phoneNumber = "7875558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));
        phoneNumber = "9395558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Saint Kitts and Nevis - +1 869
        phoneNumber = "8695558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Saint Lucia - +1 758
        phoneNumber = "7585558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Saint Vincent and the Grenadines - +1 784
        phoneNumber = "7845558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        //  Sint Maarten - +1 721
        phoneNumber = "7215558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Trinidad and Tobago - +1 868
        phoneNumber = "8685558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // Turks and Caicos Islands - +1 649
        phoneNumber = "6495558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        // United States Virgin Islands - +1 340
        phoneNumber = "3405558888";
        assertTrue(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));
    }

    @Test
    public void testInternationalizeZipwhipDomesticNumber() throws Exception {
        String phoneNumber = "2065558888";
        assertEquals("+1" + phoneNumber, InternationalNumberUtil.internationalizeZipwhipDomesticNumber(phoneNumber));

        phoneNumber = "+12065558888";
        assertEquals(phoneNumber, InternationalNumberUtil.internationalizeZipwhipDomesticNumber(phoneNumber));
    }

    @Test
    public void testGetRegionallyFormattedNumber() throws Exception {

        // Display format for Vietnamese contact number for US user
        String phoneNumber = "+841234567890";
        String regionCode = "US";
        assertEquals("+84 123 456 7890", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for Vietnamese contact number for Vietnamese user
        phoneNumber = "1234567890";
        regionCode = "VN";
        assertEquals("0123 456 7890", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for Vietnamese contact number with leading 0 for Vietnamese user
        phoneNumber = "01234567890";
        regionCode = "VN";
        assertEquals("0123 456 7890", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for US contact number for US user
        phoneNumber = "2069308877";
        regionCode = "US";
        assertEquals("(206) 930-8877", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for Vietnamese contact number with leading 0 in local format for US user
        // No formatting in this case, the number can't be routed anyway
        phoneNumber = "01234567890";
        assertEquals("01234567890", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for short code for US user (no formatting)
        phoneNumber = "20000001";
        assertEquals("20000001", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for US contact number for UK user
        phoneNumber = "+12069308888";
        regionCode = "GB";
        assertEquals("+1 206-930-8888", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for Filipino contact number for UK user
        phoneNumber = "+639281942713";
        regionCode = "GB";
        assertEquals("+63 928 194 2713", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for UK national contact number for UK user
        phoneNumber = "7836191191";
        regionCode = "GB";
        assertEquals("07836 191191", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

        // Display format for UK national contact number with leading 0 for UK user
        phoneNumber = "07836191191";
        assertEquals("07836 191191", InternationalNumberUtil.getRegionallyFormattedNumber(phoneNumber, regionCode));

    }

}
