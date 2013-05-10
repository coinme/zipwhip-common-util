package com.zipwhip.util;

import java.util.List;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 12/1/11
 * Time: 11:02 PM
 */
public class RandomSelectionStrategy<T> extends SelectionStrategyBase<T> {

    private Random random = new Random(System.currentTimeMillis());

    public RandomSelectionStrategy(List<T> options) {
        this.setOptions(options);
    }

    public RandomSelectionStrategy() {

    }

    @Override
    public T select() {
        return options.get(random.nextInt(size));
    }

}
