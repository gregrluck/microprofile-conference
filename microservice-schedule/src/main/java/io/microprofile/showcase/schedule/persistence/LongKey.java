package io.microprofile.showcase.schedule.persistence;

import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.GeneratedCacheKey;

/**
 * Silliness ahead: We have to wrap long key inside another object.
 * This is needed as JCache spec mandates {@link CacheKeyGenerator} to return an instance of {@link GeneratedCacheKey}
 *
 */
public class LongKey implements GeneratedCacheKey {

    private final long value;

    private LongKey(long value) {
        this.value = value;
    }

    public static LongKey wrap(long value) {
        return new LongKey(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongKey)) return false;

        LongKey longKey = (LongKey) o;

        return value == longKey.value;

    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }
}
