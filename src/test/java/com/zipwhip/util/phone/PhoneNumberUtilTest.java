package com.zipwhip.util.phone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ali Serghini
 *         Date: 9/11/12
 *         Time: 12:30 PM
 */
public class PhoneNumberUtilTest {
    @Test
    public void testGetPhoneNumberForDisplay() throws Exception {
        // Test US numbers
        String number = "2063803910";
        String success = "(206) 380-3910";
        String regionCode = "US";
        System.out.println("Testing US/Local numbers");

        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "206-380-3910";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "206 380 3910";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = " 2 0 6 3 8 0 3 9 1 0";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "2-0-6-3-8-0-3-9-1-0";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "(206) 380-3910";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "+12063803910";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "fgsdf2sd0fda6sdf3asdf8asd03910";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "3252606343";//
        success = "(325) 260-6343";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        regionCode = "FR";
        number = "+12063803910";
        success = "+1 206-380-3910";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "2063803910";
        testGetPhoneNumberForDisplay(null, number, regionCode);

        System.out.println("\nTesting international numbers");
        number = "+33670875130";
        success = "06 70 87 51 30";
        regionCode = "FR";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "+33 670 875 130";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "+33-670-875-130";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "(+33) 670-875-130";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "0033670875130";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "(0033) 670875130";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        number = "0670875130";
        testGetPhoneNumberForDisplay(success, number, regionCode);

        //Italy starts with 0
        number = "+390650932624";
        success = "06 5093 2624";
        regionCode = "IT";
        testGetPhoneNumberForDisplay(success, number, regionCode);

    }

    private void testGetPhoneNumberForDisplay(final String expected, final String number, final String regionCode) {
//        final String result = PhoneNumberUtil.getPhoneNumberForDisplay(number, regionCode);
        final String result = PhoneNumberUtil.optPhoneNumberForDisplay(number, regionCode);
        System.out.println(regionCode + ": " + number + " ==> " + result);
        assertEquals(expected, result);
    }

    @Test
    public void testGetPhoneNumberForStorage() throws Exception {
        // Test US numbers
        String number = "2063803910";
        String success = "2063803910";
        String regionCode = "US";
        System.out.println("Testing US/Local numbers");

        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "206-380-3910";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "206 380 3910";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = " 2 0 6 3 8 0 3 9 1 0";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "2-0-6-3-8-0-3-9-1-0";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "(206) 380-3910";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "+12063803910";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "fgsdf2sd0fda6sdf3asdf8asd03910";
        testGetPhoneNumberForStorage(success, number, regionCode);

        regionCode = "FR";
        number = "+12063803910";
        success = "2063803910";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "2063803910";
        testGetPhoneNumberForStorage(null, number, regionCode); // todo should this be null

        System.out.println("\nTesting international numbers");
        number = "+33670875130";
        success = "+33670875130";
        regionCode = "FR";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "+33 670 875 130";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "+33-670-875-130";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "(+33) 670-875-130";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "0033670875130";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "(0033) 670875130";
        testGetPhoneNumberForStorage(success, number, regionCode);

        number = "(0033) 670875130";
        testGetPhoneNumberForStorage(success, number, "US");


        //Italy starts with 0
        number = "+390650932624";
        success = "+390650932624";
        regionCode = "IT";
        testGetPhoneNumberForStorage(success, number, regionCode);
    }


    @Test
    public void testTranslateDialer() throws Exception {

        final String testString = "abcdefghijklmnopqrstuvwxyz";
        final String resultString = "22233344455566677778889999";

        String testChar = null;
        String testResult = null;
        for (int i = 0; i < testString.length(); i++) {
            testChar = Character.toString(testString.charAt(i));
            testResult = Character.toString(resultString.charAt(i));
            assertEquals(testResult, PhoneNumberUtil.translateDialerInput(testChar));
            assertEquals(testResult, PhoneNumberUtil.translateDialerInput(testChar.toUpperCase()));
        }
    }

    private void testGetPhoneNumberForStorage(final String expected, final String number, final String regionCode) {
//        final String result = PhoneNumberUtil.getPhoneNumberForStorage(number, regionCode);
        final String result = com.zipwhip.util.phone.PhoneNumberUtil.optPhoneNumberForStorage(number, regionCode);
        System.out.println(regionCode + ": " + number + " ==> " + result);
        assertEquals(expected, result);
    }
}
