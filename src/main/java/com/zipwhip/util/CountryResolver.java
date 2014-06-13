package com.zipwhip.util;

import com.zipwhip.util.phone.PhoneNumberUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Russ
 * Date: 3/4/14
 * Time: 5:29 PM
 *
 * Resolves a phone number to its two-character country code.
 *
 * Examples:
 *      countryResolver.resolve("+12066181111") == "US"
 *      countryResolver.resolve("+15198365495") == "CA"
 *      countryResolver.resolve("+33344548655") == "FR"
 */
public class CountryResolver implements Resolver<String, String> {

    private static final CountryResolver INSTANCE = new CountryResolver();

    /**
     * @param phoneNumber - expected to be in e164
     * @return two-char country code
     */
    @Override
    public String resolve(String phoneNumber) {
        return PhoneNumberUtil.getRegionCode(phoneNumber, "US");
    }

    public static CountryResolver getInstance() {
        return INSTANCE;
    }
}
