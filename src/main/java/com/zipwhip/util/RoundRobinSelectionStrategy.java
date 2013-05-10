package com.zipwhip.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 12/1/11
 * Time: 11:07 PM
 *
 * TODO: test this?
 */
public class RoundRobinSelectionStrategy<T> extends SelectionStrategyBase<T> {

    AtomicInteger counter = new AtomicInteger();

    @Override
    public T select() {
        int count = counter.getAndIncrement();

        if (count >= size){
            // equal to size, reset next time around
            counter.set(1);
            count = 0;
        }

        return options.get(count);
    }
}
