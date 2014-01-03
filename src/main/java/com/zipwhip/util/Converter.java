package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 1/28/11
 * Time: 11:40 PM
 *
 * Converts from TSource to TDestination
 */
public interface Converter<TSource, TDestination> {

    TDestination convert(TSource source) throws DataConversionException;

}
