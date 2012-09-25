package com.zipwhip.util.phone;

/**
 * User: Ali Serghini
 * Date: 9/24/12
 * Time: 12:03 PM
 */
public class E164FallbackParser extends E164BasicParser {

    public E164FallbackParser(BasePhoneNumberParser<String> fallbackParser) {
        super(fallbackParser);
    }

    @Override
    protected String get(final PhoneNumberParams phoneNumberParams) throws FallbackParserException {
        return super.get(new PhoneNumberParams(phoneNumberParams.getPhoneNumber(), PhoneNumberUtil.REGION_CODE_UNKNOWN));
    }
}
