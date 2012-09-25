package com.zipwhip.util.phone;

import com.zipwhip.util.StringUtil;

/**
 * User: Ali Serghini
 * Date: 9/24/12
 * Time: 12:03 PM
 */
public class E164BasicParser extends BasePhoneNumberParser<String> {

    public E164BasicParser(BasePhoneNumberParser<String> fallbackParser) {
        super(fallbackParser);
    }

    @Override
    protected String get(final PhoneNumberParams phoneNumberParams) throws FallbackParserException {
        // Strip all non digit chars
        final String phoneNumber = getCleanNumber(phoneNumberParams.getPhoneNumber());
        if (StringUtil.isNullOrEmpty(phoneNumber)) throw new FallbackParserException("Invalid Mobile Number: " + phoneNumberParams);

        // Check if valid e164 number based on the default params.
        try {
            return constructE164PhoneNumber(new PhoneNumberParams(phoneNumber, phoneNumberParams.getRegionCode()));
        } catch (FallbackParserException e) {
            return super.get(phoneNumberParams);
        }
    }
}
