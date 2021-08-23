package io.yapix.base.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConcurrentUtils {

    private ConcurrentUtils() {
    }

    public static <T> List<T> waitFuturesSilence(List<Future<T>> futures) {
        List<T> values = Lists.newArrayListWithExpectedSize(futures.size());
        for (Future<T> future : futures) {
            try {
                T value = future.get();
                if (value != null) {
                    values.add(value);
                }
            } catch (InterruptedException | ExecutionException e) {
                // ignore
            }
        }
        return values;
    }

}
