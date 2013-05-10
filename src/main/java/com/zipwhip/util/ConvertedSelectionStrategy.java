package com.zipwhip.util;

import com.zipwhip.util.DataConversionException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 1/31/12
 * Time: 4:00 PM
 * <p/>
 * Convert between the two types
 */
public class ConvertedSelectionStrategy<T, V> implements SelectionStrategy<T> {

    // because we dont want conversion to dictate the underlying strategy.
    SelectionStrategy<V> selectionStrategy;
    Converter<V, T> converter;

    @Override
    public void setOptions(List<T> ts) {
        // bad interface design.
    }

    @Override
    public List<T> getOptions() {
        // bad interface design.
        return null;
    }

    @Override
    public T select() {
        try {
            return converter.convert(selectionStrategy.select());
        } catch (DataConversionException e) {
            throw new RuntimeException(e);
        }
    }

    public SelectionStrategy<V> getSelectionStrategy() {
        return selectionStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy<V> selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    public Converter<V, T> getConverter() {
        return converter;
    }

    public void setConverter(Converter<V, T> converter) {
        this.converter = converter;
    }

}
