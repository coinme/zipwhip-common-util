package com.zipwhip.util;

import com.google.i18n.phonenumbers.NumberParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Russ
 * Date: 3/5/14
 * Time: 2:13 PM
 *
 * Given a base and value, return the normalized value
 */
public interface Normalizer<R,S,T> {

    T normalize(R base, S value) throws NumberParseException;

}
