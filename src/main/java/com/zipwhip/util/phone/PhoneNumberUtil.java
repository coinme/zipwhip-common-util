package com.zipwhip.util.phone;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;
import com.zipwhip.util.InternationalNumberUtil;
import com.zipwhip.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.NATIONAL;

/**
 * User: Ali Serghini
 * Date: 9/24/12
 * Time: 11:54 AM
 */
public class PhoneNumberUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberUtil.class);
    static final com.google.i18n.phonenumbers.PhoneNumberUtil PHONE_NUMBER_UTIL = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
    private static BasePhoneNumberParser<String> E_164_PARSER;
    private static int ID = 0;

    public static final String REGION_CODE_US = "US";
    public static final String REGION_CODE_UNKNOWN = "ZZ";

    static {
        final Method[] ms = PHONE_NUMBER_UTIL.getClass().getMethods();
        for (Method m : ms) {
            if (m.getName().equals("getSupportedRegions")) {
                ID = 1;
                break;
            }
        }
    }

    /*Utility class*/
    private PhoneNumberUtil() {
        super();
    }

    /**
     * Returns a valid e164 formatted phone number. null if it can't parse the number
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return e164 formatted phone number
     */
    public static String optE164PhoneNumber(final String phoneNumber, final String regionCode) {
        try {
            return getE164PhoneNumber(phoneNumber, regionCode);
        } catch (FallbackParserException e) {
            return null;
        }
    }

    /**
     * Returns a valid e164 formatted phone number. throws an exception if it can't parse the number.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return e164 formatted phone number
     * @throws FallbackParserException
     */
    public static String getE164PhoneNumber(final String phoneNumber, final String regionCode) throws FallbackParserException {
        if (E_164_PARSER != null) return E_164_PARSER.get(new PhoneNumberParams(phoneNumber, regionCode));

        // Order is important: basic, fallback ...
        E_164_PARSER = new E164BasicParser(new E164FallbackParser(null));
        return E_164_PARSER.get(new PhoneNumberParams(phoneNumber, regionCode));
    }

    /**
     * Check if the phone number is valid.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return boolean - true if valid phone number.
     */
    public static boolean isValidPhoneNumber(final String phoneNumber, final String regionCode) {
        try {
            // Should be valid if we can convert is to a valid e164 number
            final String e164PhoneNumber = getE164PhoneNumber(phoneNumber, regionCode);
            if (StringUtil.isNullOrEmpty(e164PhoneNumber)) {
                LOGGER.error("Failed to parse " + phoneNumber + " and region code " + regionCode + " to a valid e164 number. Converted e164 number: " + e164PhoneNumber);
                return false;
            }

            // Parse the e164 number
            final Phonenumber.PhoneNumber libPhoneNumber = e164ToLibPhoneNumber(e164PhoneNumber);
            if (libPhoneNumber == null) {
                LOGGER.error("Failed to parse " + phoneNumber + " and region code " + regionCode + " to a valid e164 number. Converted e164 number: " + e164PhoneNumber);
                return false;
            }

            // Perform the validation
            return PHONE_NUMBER_UTIL.isValidNumber(libPhoneNumber);
        } catch (FallbackParserException e) {
            LOGGER.error("==X Failed to validate phone number " + phoneNumber + " and region code " + regionCode, e);
        } catch (NumberParseException e) {
            LOGGER.error("==X Failed to validate phone number " + phoneNumber + " and region code " + regionCode, e);
        }

        return false;
    }

    /**
     * Formats the phone number based on the regionCode.
     * if fallbackRegionCode matches the region code derived from the phone number. the number is formatted int the National format.
     * <p/>
     * Example:
     * number "2062221234" and region code "US" is formatted as (206) 222-1234
     * number "+12062221234" and region code "FR" is formatted as +1 206-222-1234
     * number "2062221234" and region code "FR" is invalid and will return null.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return formatted phone number
     * @throws FallbackParserException if we fail to parse the number/region code combination
     */
    public static String getPhoneNumberForDisplay(final String phoneNumber, final String regionCode) throws FallbackParserException {
        final String e164PhoneNumber = getE164PhoneNumber(phoneNumber, regionCode);
        if (StringUtil.isNullOrEmpty(e164PhoneNumber))
            throw new FallbackParserException("Failed to parse " + phoneNumber + " and region code " + regionCode + " to a valid e164 number. Converted e164 number: " + e164PhoneNumber);

        final String e164RegionCode = InternationalNumberUtil.getRegionCode(e164PhoneNumber);
        if (!isValidRegionCode(e164RegionCode))
            throw new FallbackParserException("Failed to parse " + phoneNumber + " and region code " + regionCode + " to a valid e164 number. Invalid region code detected: " + e164RegionCode);

        try {
            final Phonenumber.PhoneNumber parsableNumber = PHONE_NUMBER_UTIL.parse(e164PhoneNumber, e164RegionCode);
            if (regionCode.equals(e164RegionCode)) {
                return PHONE_NUMBER_UTIL.format(parsableNumber, NATIONAL);
            } else {
                return PHONE_NUMBER_UTIL.format(parsableNumber, INTERNATIONAL);
            }
        } catch (NumberParseException e) {
            throw new FallbackParserException("Failed to parse phone number: " + phoneNumber + " with regionCode: " + regionCode, e);
        }
    }

    /**
     * Formats the phone number based on the regionCode.
     * if region code matches the region code derived from the phone number, the number is formatted int the National format.
     * Otherwise, it uses the international format.
     * <p/>
     * Example:
     * number "2062221234" and region code "US" is formatted as (206) 222-1234
     * number "+12062221234" and region code "FR" is formatted as +1 206-222-1234
     * number "2062221234" and region code "FR" is invalid and will return null.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return formatted phone number. null if we fail to parse the number/region code combination
     */
    public static String optPhoneNumberForDisplay(final String phoneNumber, final String regionCode) {
        try {
            return getPhoneNumberForDisplay(phoneNumber, regionCode);
        } catch (FallbackParserException e) {
            return null;
        }
    }

    /**
     * Formats the phone number based on the regionCode.
     * if region code matches the region code derived from the phone number, the number is formatted int the National format.
     * Otherwise, it uses the international format.
     * <p/>
     * Example:
     * number "2062221234" and region code "US" is formatted as (206) 222-1234
     * number "+12062221234" and region code "FR" is formatted as +1 206-222-1234
     * number "2062221234" and region code "FR" is invalid and will return null.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return formatted phone number. null if we fail to parse the number/region code combination
     */
    public static String optPhoneNumberForDisplay(final String phoneNumber, final String regionCode, final String defaultVal) {
        final String val = optPhoneNumberForDisplay(phoneNumber, regionCode);
        return (StringUtil.isNullOrEmpty(val)) ? defaultVal : val;
    }

    /**
     * Formats the phone number to the e164 format based on the regionCode.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return e164 formatted number or (e164 number  - region code) for US or unsupported region codes. null if we cannot parse the number.
     * @throws FallbackParserException if we fail to parse the number/region code combination
     */
    public static String getPhoneNumberForStorage(final String phoneNumber, final String regionCode) throws FallbackParserException {
        final String e164PhoneNumber = getE164PhoneNumber(phoneNumber, regionCode);
        if (StringUtil.isNullOrEmpty(e164PhoneNumber))
            throw new FallbackParserException("Failed to parse " + phoneNumber + " and region code " + regionCode + " to a valid e164 number. Converted e164 number: " + e164PhoneNumber);

        final String e164RegionCode = InternationalNumberUtil.getRegionCode(e164PhoneNumber);
        if (!isValidRegionCode(e164RegionCode))
            throw new FallbackParserException("Failed to parse " + phoneNumber + " and region code " + regionCode + "to a valid e164 number. Invalid region code detected: " + e164RegionCode);

        if (e164RegionCode.equals(REGION_CODE_US) || e164RegionCode.equals(REGION_CODE_UNKNOWN)) {
            return StringUtil.safeCleanMobileNumber(e164PhoneNumber);
        } else {
            return e164PhoneNumber;
        }
    }

    /**
     * Formats the phone number to the e164 format based on the regionCode.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return e164 formatted number or (e164 number  - region code) for US or unsupported region codes. null if we cannot parse the number.
     */
    public static String optPhoneNumberForStorage(final String phoneNumber, final String regionCode) {
        try {
            return getPhoneNumberForStorage(phoneNumber, regionCode);
        } catch (FallbackParserException e) {
            return null;
        }
    }

    /**
     * Formats the phone number to the e164 format based on the regionCode.
     *
     * @param phoneNumber - phone number
     * @param regionCode  - region code
     * @return e164 formatted number or (e164 number  - region code) for US or unsupported region codes. default value if we cannot parse the number.
     */
    public static String optPhoneNumberForStorage(final String phoneNumber, final String regionCode, final String defaultVal) {
        final String val = optPhoneNumberForStorage(phoneNumber, regionCode);
        return (StringUtil.isNullOrEmpty(val)) ? defaultVal : val;
    }

    /**
     * Returns a sample phone number formatted int he international format based on the region code.
     *
     * @param regionCode - region code
     * @return sample phone number
     */
    public static String getSamplePhoneNumberForDisplay(final String regionCode) {
        return PHONE_NUMBER_UTIL.format(PHONE_NUMBER_UTIL.getExampleNumber(regionCode), com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }

    /**
     * Check if the region code is valid
     *
     * @param regionCode - region code
     * @return boolean - true if valid region code
     */
    public static boolean isValidRegionCode(final String regionCode) {
        return !StringUtil.isNullOrEmpty(regionCode) && (REGION_CODE_UNKNOWN.equals(regionCode) || getSupportedRegions().contains(regionCode));
    }

    private static Phonenumber.PhoneNumber e164ToLibPhoneNumber(final String phoneNumber) throws NumberParseException {
        return PHONE_NUMBER_UTIL.parse(phoneNumber, REGION_CODE_UNKNOWN);
    }

    /**
     * Returns the country calling code for a specific region. For example, this would be 1 for the
     * United States, and 64 for New Zealand.
     *
     * @param regionCode -   the region that we want to get the country calling code for
     * @return the country calling code for the region denoted by regionCode
     */
    public static int getCountryCodeForRegion(final String regionCode) {
        return PHONE_NUMBER_UTIL.getCountryCodeForRegion(regionCode);
    }

    /**
     * Returns a three-letter abbreviation for this locale's country.
     * If the country matches an ISO 3166-1 alpha-2 code, the
     * corresponding ISO 3166-1 alpha-3 uppercase code is returned.
     * If the locale doesn't specify a country, this will be the empty
     * string.
     * <p/>
     * <p>The ISO 3166-1 codes can be found on-line.
     *
     * @param regionCode - region code
     * @return A three-letter abbreviation of this locale's country.
     * @throws java.util.MissingResourceException Throws MissingResourceException if the
     *                                            three-letter country abbreviation is not available for this locale.
     */
    public static String getISO3Country(final String regionCode) {
        return getISO3Country(new Locale("", regionCode));
    }

    /**
     * Returns a three-letter abbreviation for this locale's country.
     * If the country matches an ISO 3166-1 alpha-2 code, the
     * corresponding ISO 3166-1 alpha-3 uppercase code is returned.
     * If the locale doesn't specify a country, this will be the empty
     * string.
     * <p/>
     * <p>The ISO 3166-1 codes can be found on-line.
     *
     * @param locale - locale
     * @return A three-letter abbreviation of this locale's country.
     * @throws java.util.MissingResourceException Throws MissingResourceException if the
     *                                            three-letter country abbreviation is not available for this locale.
     */
    public static String getISO3Country(final Locale locale) {
        return locale.getISO3Country();
    }

    public static String getDisplayCountry(final String regionCode) {
        return getDisplayCountry(new Locale("", regionCode));
    }

    public static String getDisplayCountry(final Locale locale) {
        return locale.getDisplayCountry(Locale.getDefault());
    }

    /**
     * Translate the input from a phone dialer to a valid phone number.
     * <p/>
     * Example: 855zipwhip -> 8559479447
     *
     * @param input - dialer input
     * @return phone number or null if the input is blank.
     */
    public static String translateDialerInput(final String input) {
        return StringUtils.isBlank(input) ? null : StringUtils.replaceChars(input.toUpperCase(), "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "22233344455566677778889999");
    }

    /**
     * Get a set og the supported regions
     *
     * @return region codes
     */
    public static Set<String> getSupportedRegions() {
        // Some android sdks are packaged with older version of lib phone number and use getSupportedCountries instead of getSupportedRegions.
        try {
            final Set<String> regionCodes = ID == 1 ? PHONE_NUMBER_UTIL.getSupportedRegions() : (Set<String>) PHONE_NUMBER_UTIL.getClass().getMethod("getSupportedCountries").invoke(PHONE_NUMBER_UTIL);
            if (regionCodes != null && !regionCodes.isEmpty()) return Collections.unmodifiableSet(regionCodes);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
