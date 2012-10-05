package com.zipwhip.util;

import com.google.i18n.phonenumbers.NumberParseException;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 10/4/12
 * Time: 3:30 PM
 *
 *
 */
public class RegionCodeFormatter implements Formatter<String> {

    private final String regionCode;

    public RegionCodeFormatter(String regionCode) {
        this.regionCode = regionCode;
    }

    @Override
    public String format(String input) {
        try {
            return InternationalNumberUtil.getE164Number(input, regionCode);
        } catch (NumberParseException e) {
            throw new RuntimeException("Exception with input " + input, e);
        }
    }
}
