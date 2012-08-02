package com.zipwhip.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created with IntelliJ IDEA.
 * User: jed
 * Date: 7/9/12
 * Time: 1:38 PM
 */
public class InternationalNumberUtil {

    // Region-code for the unknown region as defined internally in libphonenumber
    public static final String UNKNOWN_REGION = "ZZ";

    protected static final String PLUS = "+";
    protected static final String ONE = "1";
    protected static final String ZERO = "0";
    protected static final String PLUS_ONE = "+1";

    /**
     * Given a valid international number or a valid Zipwhip Domestic number this function will return
     * the region code for the number or 'ZZ' if the region can not be determined.
     * <p/>
     * If the number is considered Zipwhip Domestic (10 digits without a leading '+1') then it will be
     * converted to e.164. Converted numbers are not guaranteed to return a region code.
     *
     * @param mobileNumber A valid international number or a valid Zipwhip Domestic number
     * @return The region code for the number or 'ZZ' if the region can not be determined.
     */
    public static String getRegionCode(String mobileNumber) {

        if (isValidZipwhipDomesticNumberFormat(mobileNumber)) {
            mobileNumber = internationalizeZipwhipDomesticNumber(mobileNumber);
        }

        if (!isValidInternationalNumber(mobileNumber)) {
            return UNKNOWN_REGION;
        }

        Phonenumber.PhoneNumber phoneNumber;

        try {
            phoneNumber = PhoneNumberUtil.getInstance().parse(mobileNumber, UNKNOWN_REGION);
        } catch (NumberParseException e) {
            return UNKNOWN_REGION;
        }

        if (phoneNumber.hasCountryCode()) {
            String regionCode = PhoneNumberUtil.getInstance().getRegionCodeForNumber(phoneNumber);
            return StringUtil.exists(regionCode) ? regionCode : UNKNOWN_REGION;
        }

        return UNKNOWN_REGION;
    }

    /**
     * Given a mobile number this method will determine if the number is e.164
     * compliant and therefore internationally routable.
     *
     * @param mobileNumber The mobile number to be validated.
     * @return true if the number is valid.
     */
    public static boolean isValidInternationalNumber(String mobileNumber) {

        Phonenumber.PhoneNumber phoneNumber;

        try {
            phoneNumber = PhoneNumberUtil.getInstance().parse(mobileNumber, UNKNOWN_REGION);
        } catch (NumberParseException e) {
            return false;
        }

        return PhoneNumberUtil.getInstance().isValidNumber(phoneNumber);
    }

    /**
     * A Zipwhip Domestic number is a number in the North American Numbering Plan.
     * <p/>
     * To be in a valid format for Zipwhip Domestic the number must be 10 digits and not start with '+1'
     *
     * @param mobileNumber The mobile number to be validated.
     * @return true if mobileNumber has a length of 10 and does not start with '+1'
     */
    public static boolean isValidZipwhipDomesticNumberFormat(String mobileNumber) {

        if (StringUtil.isNullOrEmpty(mobileNumber) || mobileNumber.startsWith(PLUS) || mobileNumber.startsWith(ZERO) || mobileNumber.startsWith(ONE) || mobileNumber.length() != 10) {
            return false;
        }

        try {
            Long.parseLong(mobileNumber);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method will return an internationally routable number.
     * <p/>
     * The precondition of this method is that {@code isValidZipwhipDomesticNumberFormat}
     * returns true for mobileNumber. If {@code isValidZipwhipDomesticNumberFormat} returns
     * false then mobileNumber is returned unmodified.
     *
     * @param mobileNumber The Zipwhip Domestic mobile number to be internationalized.
     * @return The internationalized Zipwhip Domestic mobile number.
     */
    public static String internationalizeZipwhipDomesticNumber(String mobileNumber) {

        if (!isValidZipwhipDomesticNumberFormat(mobileNumber)) {
            return mobileNumber;
        }

        return PLUS_ONE + mobileNumber;
    }

    /**
     * Takes the supplied number, as well as the region code of the user who typed the number, and converts said number
     * into the full E164 format (Without spaces).  Useful for saving values back to the cloud.
     * @param mobileNumber
     * @param regionCode
     * @return
     * @throws NumberParseException If the supplied mobile number cannot be properly parsed by PhoneNumberUtil.
     * @throws IllegalArgumentException If the supplied mobile number is a valid number, but cannot be formatted to E164 (Such as shortcodes)
     */
    public static String getE164Number(String mobileNumber, String regionCode) throws NumberParseException, IllegalArgumentException {
        Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(mobileNumber, regionCode);
        if (!PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)){
            throw new IllegalArgumentException("'" + mobileNumber + "' is not a valid phone number for the '" + regionCode + "' region code.");
        }
        return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    /**
     * Tells whether a given region code is associated with a NANP region.
     * @param regionCode
     * @return
     */
    public static boolean isNANPARegionCode(String regionCode){
        return PhoneNumberUtil.getInstance().isNANPACountry(regionCode);
    }

    /**
     * Use this method for formatting a user's contacts relative to the user's local region code.
     *
     * @param mobileNumber    The contact mobile number to be formatted for display.
     * @param usersRegionCode The region code of the user's region, NOT necessarily the region of the mobileNumber.
     * @return The mobile number formatted to the local format or internal format of it is an international number.
     */
    public static String getFormattedNumberForContact(String mobileNumber, String usersRegionCode) {

        PhoneNumberUtil.PhoneNumberFormat format;
        String defaultRegion;

        if (isValidInternationalNumber(mobileNumber)) {
            // If we have a valid e.164 number we can ignore the usersRegionCode
            defaultRegion = UNKNOWN_REGION;
            format = PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
            
            try {
                Phonenumber.PhoneNumber number = PhoneNumberUtil.getInstance().parse(mobileNumber, UNKNOWN_REGION);

                //In case the number that we're formatting has the same country code as the user's country code, use national
                //formatting instead of international formatting.
                if (PhoneNumberUtil.getInstance().getRegionCodeForNumber(number).equals(usersRegionCode)){
                    format = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
                }
            } catch (NumberParseException e) {
                //In theory, this shouldn't happen, as we would have failed to parse at isValidInternationalNumber.
                //However, just in case an exception is thrown, treat the number as an international number.
            }
        } else {
            defaultRegion = usersRegionCode;
            format = PhoneNumberUtil.PhoneNumberFormat.NATIONAL;
        }

        return getFormattedNumber(mobileNumber, defaultRegion, format);
    }

    /**
     * Use this method for formatting a user's mobile number relative to the user's local region code.
     *
     * @param mobileNumber    The user's mobile number to be formatted for display.
     * @param usersRegionCode The region code of the user's region, NOT necessarily the region of the mobileNumber.
     * @return The mobile number formatted to the local format or internal format of it is an international number.
     */
    public static String getFormattedNumberForUser(String mobileNumber, String usersRegionCode) {

        String defaultRegion;

        if (isValidInternationalNumber(mobileNumber)) {
            // If we have a valid e.164 number we can ignore the usersRegionCode
            defaultRegion = UNKNOWN_REGION;
        } else {
            defaultRegion = usersRegionCode;
        }

        return getFormattedNumber(mobileNumber, defaultRegion, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
    }

    protected static String getFormattedNumber(String mobileNumber, String usersRegionCode, PhoneNumberUtil.PhoneNumberFormat format) {
        try {
            Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(mobileNumber, usersRegionCode);
            return PhoneNumberUtil.getInstance().format(phoneNumber, format);
        } catch (NumberParseException e) {
            return mobileNumber;
        }
    }

}