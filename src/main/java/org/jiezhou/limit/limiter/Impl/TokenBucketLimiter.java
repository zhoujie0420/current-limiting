package org.jiezhou.limit.limiter.Impl;

import jdk.nashorn.internal.parser.Token;
import org.jiezhou.limit.limiter.Dao.LimiterDTO;
import org.jiezhou.limit.limiter.LimiterAbstract;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenBucketLimiter extends LimiterAbstract {

    /**
     * 桶容量
     */
    private Integer capacity = 5;

    /**
     * 每次放多少token
     */
    private Integer rate = 1;

    /**
     * 间隔时间
     */
    private Integer interval = 5;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    // 剩余的token
    private AtomicInteger surplus = new AtomicInteger(5);

    private  static  TokenBucketLimiter instance;

    //单例，如果创建多个TokenBucketLimiter 则有多个定时器

    public static synchronized TokenBucketLimiter getInstance(){
        if(instance == null){
            instance = new TokenBucketLimiter();
        }
        return instance;
    }

    private TokenBucketLimiter(){
        init();
    }

    /**
     *
     * @param dto
     * @return
     */
    @Override
    public boolean check(LimiterDTO dto) {
        //是否有容量
        if(surplus.get() <= 0){
            return false;
        }
        surplus.getAndIncrement();
        return false;
    }

    private void init(){
        scheduledExecutorService.scheduleWithFixedDelay(()->{
            //不能超过容量
            if(surplus.get() < capacity){
                surplus.getAndAdd(rate);
            }
        }, 0,interval, TimeUnit.SECONDS);
    }
}
