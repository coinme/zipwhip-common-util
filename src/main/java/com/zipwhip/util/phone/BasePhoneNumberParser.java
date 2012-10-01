package com.zipwhip.util.phone;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;
import com.zipwhip.util.StringUtil;

/**
 * User: Ali Serghini
 * Date: 9/24/12
 * Time: 11:07 AM
 */
public abstract class BasePhoneNumberParser<TValue> extends BaseFallbackParser<PhoneNumberParams, TValue> {
    private static final String LEADING_INTL_ZEROS = "00";

    protected BasePhoneNumberParser(final BaseFallbackParser<PhoneNumberParams, TValue> fallbackParser) {
        super(fallbackParser);
    }

    @Override
    protected TValue get(final PhoneNumberParams phoneNumberParams) throws FallbackParserException {
        if (fallbackParser != null) return fallbackParser.get(phoneNumberParams);
        throw new FallbackParserException("Failed to load value based on param: " + phoneNumberParams);
    }

    protected String getCleanNumber(final String phoneNumber) {
        // Strip all non digit chars
        String number = com.google.i18n.phonenumbers.PhoneNumberUtil.normalizeDigitsOnly(phoneNumber);
        if (StringUtil.isNullOrEmpty(number)) return null;

        // Check if international number starting with "00"
        if (number.startsWith(LEADING_INTL_ZEROS)) number = number.substring(2);
        return StringUtil.isNullOrEmpty(number) ? null : number;
    }

    protected String constructE164PhoneNumber(final PhoneNumberParams phoneNumberParams) throws FallbackParserException {
        if (!PhoneNumberUtil.isValidRegionCode(phoneNumberParams.getRegionCode()))
            throw new FallbackParserException("Invalid region code: " + phoneNumberParams);

        // Try to detect the country code based on the region code
        final int countryCode = PhoneNumberUtil.PHONE_NUMBER_UTIL.getCountryCodeForRegion(phoneNumberParams.getRegionCode());
        if (!PhoneNumberUtil.REGION_CODE_UNKNOWN.equals(phoneNumberParams.getRegionCode()) && countryCode < 1)
            throw new FallbackParserException("Invalid country code: " + countryCode + " for phone number: " + phoneNumberParams);

        // Check if the number already start with the country code
        String sCountryCode = Integer.toString(countryCode);
        // lib phone number should take care of the case where the country code is duplicated
        if (countryCode < 1) sCountryCode = "";

        // parse the number
        final Phonenumber.PhoneNumber libNumber;
        try {
            libNumber = PhoneNumberUtil.PHONE_NUMBER_UTIL.parse("+" + sCountryCode + phoneNumberParams.getPhoneNumber(), phoneNumberParams.getRegionCode());
        } catch (NumberParseException e) {
            throw new FallbackParserException("Failed to construct a valid e164 number based on : " + phoneNumberParams, e);
        }

        // Format and return the number in e164 format
        if (PhoneNumberUtil.PHONE_NUMBER_UTIL.isValidNumber(libNumber)) {
            return PhoneNumberUtil.PHONE_NUMBER_UTIL.format(libNumber, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
        } else {
            throw new FallbackParserException("Failed to construct a valid e164 number based on : " + phoneNumberParams);
        }
    }

}
