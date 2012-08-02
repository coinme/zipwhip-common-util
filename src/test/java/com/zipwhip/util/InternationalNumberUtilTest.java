package com.zipwhip.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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
        String userFriendNationalFormatted = InternationalNumberUtil.getFormattedNumberForContact(userFriendNational, userRegion);

        // An international contact number
        String userFriendInternational = "+841234567890";

        // This string will look like '+84 123 456 7890' -- THE MOST IMPORTANT ASPECT OF THIS CALL IS THAT userRegion IS THE USER'S REGION, NOT THE FRIEND'S
        String userFriendInternationalFormatted = InternationalNumberUtil.getFormattedNumberForContact(userFriendInternational, userRegion);

        /*******************************************************************************************************************************************************
         * Example #2 - International (e.164) number
         */
        String userMobileInternational = "+841234567890";

        // The region will be 'VN'.
        userRegion = InternationalNumberUtil.getRegionCode(userMobileInternational);

        // A local contact number
        userFriendNational = "5074527878";

        // This string will look like '0507 452 3253' -- THE MOST IMPORTANT ASPECT OF THIS CALL IS THAT userRegion IS THE USER'S REGION, NOT THE FRIEND'S
        userFriendNationalFormatted = InternationalNumberUtil.getFormattedNumberForContact(userFriendNational, userRegion);
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

        //Phillipines number, nine digits.
        phoneNumber = "335124578";
        assertFalse(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));

        //Phillipines number, nine digits, with a preceding zero..
        phoneNumber = "0335124578";
        assertFalse(InternationalNumberUtil.isValidZipwhipDomesticNumberFormat(phoneNumber));
    }

    @Test
    public void testInternationalizeZipwhipDomesticNumber() throws Exception {
        String phoneNumber = "2065558888";
        assertEquals("+1" + phoneNumber, InternationalNumberUtil.internationalizeZipwhipDomesticNumber(phoneNumber));

        phoneNumber = "+12065558888";
        assertEquals(phoneNumber, InternationalNumberUtil.internationalizeZipwhipDomesticNumber(phoneNumber));
    }

    @Test
    public void testGetFormattedNumberForContact() throws Exception {

        // Display format for Vietnamese contact number for US user
        String contactPhoneNumber = "+841234567890";
        String regionCode = "US";
        assertEquals("+84 123 456 7890", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for Vietnamese contact number for Vietnamese user
        contactPhoneNumber = "1234567890";
        regionCode = "VN";
        assertEquals("0123 456 7890", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for Vietnamese contact number with leading 0 for Vietnamese user
        contactPhoneNumber = "01234567890";
        regionCode = "VN";
        assertEquals("0123 456 7890", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for US contact number for US user
        contactPhoneNumber = "2069308877";
        regionCode = "US";
        assertEquals("(206) 930-8877", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for Vietnamese contact number with leading 0 in local format for US user
        // No formatting in this case, the number can't be routed anyway
        contactPhoneNumber = "01234567890";
        regionCode = "US";
        assertEquals("01234567890", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for short code for US user (no formatting)
        contactPhoneNumber = "20000001";
        regionCode = "US";
        assertEquals("20000001", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for US contact number for UK user
        contactPhoneNumber = "+12069308888";
        regionCode = "GB";
        assertEquals("+1 206-930-8888", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for Filipino contact number for UK user
        contactPhoneNumber = "+639281942713";
        regionCode = "GB";
        assertEquals("+63 928 194 2713", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for UK national contact number for UK user
        contactPhoneNumber = "7836191191";
        regionCode = "GB";
        assertEquals("07836 191191", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // Display format for UK national contact number with leading 0 for UK user
        contactPhoneNumber = "07836191191";
        regionCode = "GB";
        assertEquals("07836 191191", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // For an internationally formatted Phillipines number being displayed to a PH user, the number should be formatted in the national format.
        contactPhoneNumber = "+639281972713";
        regionCode = "PH";
        assertEquals("0928 197 2713", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

        // For a US user looking at a US number in the international format, we would expect the number to be formatted in the US national format.
        contactPhoneNumber = "+13606712625";
        regionCode = "US";
        assertEquals("(360) 671-2625", InternationalNumberUtil.getFormattedNumberForContact(contactPhoneNumber, regionCode));

    }
    
    @Test
    public void testIsNANPARegionCode(){
        String[] invalidNANPCodes = new String[]{"QY", "KR", "HK", "AQ", "NF", "NU", "GB", "UK", "SV", "CR", "BO", "EC", "ME", "MK", "CZ", "IS", "AL", "FI", "BG", "EG", "SS", "GM", "MR", "GA", "CD", "AO", "SO", "DJ", "ZM", "AW", InternationalNumberUtil.UNKNOWN_REGION};
        for (String nonNanpCode : invalidNANPCodes){
            if (InternationalNumberUtil.isNANPARegionCode(nonNanpCode)){
                fail("'" + nonNanpCode + "' is not being correctly recognized as an invalid NANP region code.");
            }
        }

        String[] validNANPCodes = new String[]{"US", "CA", "AG", "AI", "AS", "BB", "BM", "BS", "DM", "DO", "GD", "GU", "JM", "KN", "KY", "LC", "MP", "MS", "PR", "SX", "TC", "TT", "VC", "VG", "VI"};
        for (String nanpCode : validNANPCodes){
            if (!InternationalNumberUtil.isNANPARegionCode(nanpCode)){
                fail("'" + nanpCode + "' is not being correctly recognized as a valid NANP region code.");
            }
        }
    }
    
    @Test
    public void testGetE164Number() throws Exception {
        //US user with a valid US number, both with and without formatting.
        assertEquals("+13609900541", InternationalNumberUtil.getE164Number("(360) 990-0541", "US"));
        assertEquals("+13609900541", InternationalNumberUtil.getE164Number("3609900541", "US"));
        
        //PH user with a valid PH escape code attached.
        assertEquals("+13609900541", InternationalNumberUtil.getE164Number("001 360 990 0541", "PH"));

        //VN user putting in a valid VN number should attach the VN international code.
        assertEquals("+841234567890", InternationalNumberUtil.getE164Number("1234567890", "VN"));

        //UK number with the preceding 0 (Which is apparently valid)
        assertEquals("+447836191191", InternationalNumberUtil.getE164Number("07836191191", "GB"));

        //UK number without the preceding 0 (Which is apparently valid)
        assertEquals("+447836191191", InternationalNumberUtil.getE164Number("7836191191", "GB"));

        //A valid E164 number should be left unchanged, independent of the country code.
        assertEquals("+639281972713", InternationalNumberUtil.getE164Number("+639281972713", "PH"));
        assertEquals("+12069308888", InternationalNumberUtil.getE164Number("+12069308888", "GB"));
        assertEquals("+639281972713", InternationalNumberUtil.getE164Number("+639281972713", "US"));
        assertEquals("+639281972713", InternationalNumberUtil.getE164Number("+639281972713", InternationalNumberUtil.UNKNOWN_REGION));


        //As an E164 number, it should just have it's spaces removed.
        assertEquals("+639281942713", InternationalNumberUtil.getE164Number("+63 928 194 2713", "GB"));

        //Properly formatted PH domestic number.
        assertEquals("+639281972713", InternationalNumberUtil.getE164Number("0928 197 2713", "PH"));

        //As this is shortcode, we should expect an IllegalArgumentException, as the supplied number cannot be formatted to E164.
        try {
            InternationalNumberUtil.getE164Number("20000001", "US");
            fail();
        } catch (IllegalArgumentException e){}

        try {
            InternationalNumberUtil.getE164Number("(360) 671-2625", "PH");
            fail();
        } catch (IllegalArgumentException e){}

        try {
            InternationalNumberUtil.getE164Number("0928 197 2713", "US");
            fail();
        } catch (IllegalArgumentException e){}

        try {
            InternationalNumberUtil.getE164Number("0123456789", "US");
            fail();
        } catch (IllegalArgumentException e){}

        try {
            InternationalNumberUtil.getE164Number("01 360 990 0541", "PH");
            fail();
        } catch (IllegalArgumentException e){}

        try {
            InternationalNumberUtil.getE164Number("01 360 990 0541", "US");
            fail();
        } catch (IllegalArgumentException e){}

        //As the supplied number isn't E164 formatted, and the region code isn't supplied, this should fail.
        try {
            InternationalNumberUtil.getE164Number("0928 197 2713", InternationalNumberUtil.UNKNOWN_REGION);
            fail();
        } catch (NumberParseException e){}

        //Invalidly formatted UK number.
        try {
            InternationalNumberUtil.getE164Number("0928 197 2713", "UK");
            fail();
        } catch (NumberParseException e){}

        //Missing number
        try {
            InternationalNumberUtil.getE164Number("", "US");
            fail();
        } catch (NumberParseException e){}

        //Gibberish number
        try {
            InternationalNumberUtil.getE164Number("lj3l4k5j", "US");
            fail();
        } catch (NumberParseException e){}

    }

    @Test
    public void testGetFormattedNumberForUser() throws Exception {

        String contactPhoneNumber = "+841234567890";
        String regionCode = "VN";
        assertEquals("0123 456 7890", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));

        contactPhoneNumber = "1234567890";
        regionCode = "VN";
        assertEquals("0123 456 7890", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));

        contactPhoneNumber = "01234567890";
        regionCode = "VN";
        assertEquals("0123 456 7890", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));

        contactPhoneNumber = "2069308877";
        regionCode = "US";
        assertEquals("(206) 930-8877", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));

        contactPhoneNumber = "+12069308877";
        regionCode = "US";
        assertEquals("(206) 930-8877", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));

        contactPhoneNumber = "7836191191";
        regionCode = "GB";
        assertEquals("07836 191191", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));

        contactPhoneNumber = "07836191191";
        regionCode = "GB";
        assertEquals("07836 191191", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));

        contactPhoneNumber = "+447836191191";
        regionCode = "GB";
        assertEquals("07836 191191", InternationalNumberUtil.getFormattedNumberForUser(contactPhoneNumber, regionCode));
    }

}
