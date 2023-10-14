package org.jiezhou.limit.limiter;

import org.jiezhou.limit.limiter.Dao.LimiterDTO;

public interface Limiter {

    void set(String key, Integer value, long time);

    Integer get(String key);

    void remove(String key);

    void incr(String key, long time);

    boolean check(LimiterDTO dto);
}
