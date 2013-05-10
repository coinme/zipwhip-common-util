package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 1/28/11
 * Time: 11:40 PM
 *
 * Converts out of and into bytes.
 *
 */
public interface Converter<TSource, TUnderlying> {

    TUnderlying convert(TSource source) throws DataConversionException;

}
