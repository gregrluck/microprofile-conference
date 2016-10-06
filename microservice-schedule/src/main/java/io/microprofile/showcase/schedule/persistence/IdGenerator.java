package io.microprofile.showcase.schedule.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Non-Threadsafe ID generator
 *
 * @param <T>
 */
public class IdGenerator<T> {
    private final AtomicLong sequence;
    private final Map<T, Long> mapping;

    public IdGenerator(AtomicLong sequence) {
        this.sequence = sequence;
        this.mapping = new HashMap<>();
    }

    public Long getOrGenerateId(T name) {
        Long id = mapping.get(name);
        if (id != null) {
            return id;
        }
        id = sequence.getAndIncrement();
        mapping.put(name, id);
        return id;
    }


}
