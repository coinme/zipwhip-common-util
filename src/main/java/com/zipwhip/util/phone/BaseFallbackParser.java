package com.zipwhip.util.phone;

/**
 * User: Ali Serghini
 * Date: 9/24/12
 * Time: 11:03 AM
 */
public abstract class BaseFallbackParser<TKey, TValue> {

    protected final BaseFallbackParser<TKey, TValue> fallbackParser;

    protected BaseFallbackParser(final BaseFallbackParser<TKey, TValue> fallbackParser) {
        super();
        this.fallbackParser = fallbackParser;
    }

    /**
     * Attempts to retrieve the value based on the supplied key. Throws an exception if value not found or there was an issue retrieving the value.
     *
     * @param key - Key
     * @return value we are after
     * @throws FallbackParserException
     */
    protected abstract TValue get(final TKey key) throws FallbackParserException;
//
//    /**
//     * Attempts to retrieve the value based on the supplied key. Returns null if value not found or there was an issue retrieving the value.
//     *
//     * @param key - Key
//     * @return value we are after. Null if not found
//     */
//    protected abstract TValue opt(final TKey key);
}
