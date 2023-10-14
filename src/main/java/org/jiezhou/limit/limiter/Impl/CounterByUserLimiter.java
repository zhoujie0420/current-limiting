package org.jiezhou.limit.limiter.Impl;

import lombok.AllArgsConstructor;
import org.jiezhou.limit.limiter.Dao.LimiterDTO;
import org.jiezhou.limit.limiter.LimiterAbstract;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 针对单个用户
 */
public class CounterByUserLimiter extends LimiterAbstract {
    private Map<String, Data> map = new HashMap<>();

    private BlockingQueue<DataDelay> delayQueue = new DelayQueue<>();

    private static CounterByUserLimiter instance;

    /**
     * 单例
     *
     * @return
     */
    public static CounterByUserLimiter getInstance() {
        if (instance == null) {
            instance = new CounterByUserLimiter();
        }
        return instance;
    }

    private CounterByUserLimiter() {
        init();
    }

    @Override
    public void set(String key, Integer value, long time) {
        map.put(key, new Data(value, time));
        delayQueue.add(new DataDelay(key, time));
    }

    @Override
    public Integer get(String key) {
        return map.get(key).value.get();
    }

    @Override
    public void remove(String key) {
        map.remove(key);
        delayQueue.remove(new DataDelay(key));
    }

    /**
     * 自增，不存在则添加
     *
     * @param
     * @return
     */
    @Override
    public void incr(String key, long time) {
        if (map.containsKey(key)) {
            map.get(key).incr();
        } else {
            set(key, 1, time);
        }
    }

    /**
     * 达到限制在time时间内不可访问
     * 执意访问延续时间
     *
     * @param limiterDTO
     * @return
     */
    @Override
    public boolean check(LimiterDTO limiterDTO) {
        String key = limiterDTO.getKey();
        int limit = limiterDTO.limit;
        int time = limiterDTO.getTime();
        //当前用户没有访问过，不限流
        if (!map.containsKey(key)) {
            set(key, 1, time);
            return false;
        }
        Data data = map.get(key);
        boolean res = data.value.get() >= limit;
        if (res) {
            removeDelayKey(key);
            addDelay(key, data.time);
        }
        incr(key, time);
        return res;
    }

    private void removeDelayKey(String key) {
        delayQueue.remove(new DataDelay((key)));
    }

    private void addDelay(String key, long time) {
        delayQueue.add(new DataDelay(key, time));
    }

    /**
     * 初始化队列
     */
    private void init() {
        new Thread(() -> {
            while (true) {
                try {
                    DataDelay take = delayQueue.take();
                    map.remove(take.key);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "CounterByUserLimiter").start();
    }


    public class Data {
        /**
         * 原子操作
         */
        AtomicInteger value;
        long time;

        public Data(Integer value, long time) {
            this.value = new AtomicInteger(value);
            this.time = time;
        }

        public void incr() {
            value.getAndIncrement();
        }
    }

    /**
     * 延迟队列
     */
    @AllArgsConstructor
    private class DataDelay implements Delayed {
        String key;
        long expire;

        public DataDelay(String key) {
            this.key = key;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long f = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
            return (int) f;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataDelay dataDelay = (DataDelay) o;
            return Objects.equals(key, dataDelay.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}

