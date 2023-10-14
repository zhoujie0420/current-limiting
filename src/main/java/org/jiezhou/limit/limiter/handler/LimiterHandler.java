package org.jiezhou.limit.limiter.handler;

import org.jiezhou.limit.limiter.Dao.LimiterDTO;
import org.jiezhou.limit.limiter.Limit;
import org.jiezhou.limit.limiter.Limiter;

public class LimiterHandler implements Limiter {
    Limiter limiter;

    public LimiterHandler(Limiter limiter){
        this.limiter = limiter;
    }

    @Override
    public void set(String key, Integer value, long time){
        limiter.set(key ,value,time);
    }

    @Override
    public Integer get(String key) {
        return limiter.get(key);
    }

    @Override
    public void remove(String key) {
        remove(key);
    }

    @Override
    public void incr(String key, long time) {
        incr(key,time);
    }

    @Override
    public  boolean check(LimiterDTO dto){
        return limiter.check(dto);
    }

}
