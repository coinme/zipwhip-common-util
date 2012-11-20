package com.zipwhip.format;

import com.zipwhip.format.Formatter;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 10/25/12
 * Time: 5:17 PM
 *
 * Extend this to customize your own formatter adapter.
 */
public class FormatterAdapterBase<T> implements Formatter<T> {

    private final Formatter<T> formatter;

    public FormatterAdapterBase(Formatter<T> formatter) {
        this.formatter = formatter;
    }

    @Override
    public T format(T input) {
        return formatter.format(input);
    }
}
