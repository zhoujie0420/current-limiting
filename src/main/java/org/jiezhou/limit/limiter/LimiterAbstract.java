package org.jiezhou.limit.limiter;


/**
 * 该抽象类的作用是后续继承此类只需要重现check方法
 * 后续的限流策略都只需要在check重写
 */
public abstract class LimiterAbstract implements Limiter {

    @Override
    public void set(String key, Integer value, long time) {
    }

    @Override
    public Integer get(String key) {
        return null;
    }

    @Override
    public void remove(String key) {
    }


    @Override
    public void incr(String key, long time) {
    }

}
