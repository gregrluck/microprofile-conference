package io.microprofile.showcase.schedule.persistence;

import javax.cache.annotation.CacheInvocationParameter;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheKeyInvocationContext;
import javax.cache.annotation.GeneratedCacheKey;
import java.lang.annotation.Annotation;
import java.util.Arrays;

import static io.microprofile.showcase.schedule.persistence.LongKey.wrap;

public class LongKeyGenerator implements CacheKeyGenerator {

    @Override
    public GeneratedCacheKey generateCacheKey(CacheKeyInvocationContext<? extends Annotation> cacheKeyInvocationContext) {
        CacheInvocationParameter[] keyParameters = cacheKeyInvocationContext.getKeyParameters();
        int argumentLength = keyParameters.length;
        if (argumentLength != 1) {
            onWrongArgument(keyParameters);
        }
        CacheInvocationParameter keyParameter = keyParameters[0];
        Class<?> rawType = keyParameter.getRawType();
        if (rawType != Long.class) {
            onWrongArgument(keyParameters);
        }
        Long value = (Long) keyParameter.getValue();
        return wrap(value);
    }

    private void onWrongArgument(CacheInvocationParameter[] keyParameters) {
        throw new IllegalArgumentException(LongKeyGenerator.class
            + " can be only used with a single parameter of type Long. Current parameters: "
            + Arrays.toString(keyParameters));
    }
}
