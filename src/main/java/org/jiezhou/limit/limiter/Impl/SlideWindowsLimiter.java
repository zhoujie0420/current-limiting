package org.jiezhou.limit.limiter.Impl;

import org.jiezhou.limit.limiter.Dao.LimiterDTO;
import org.jiezhou.limit.limiter.LimiterAbstract;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.concurrent.TimeUnit;

public class SlideWindowsLimiter extends LimiterAbstract {

    RedisTemplate redisTemplate;

    public SlideWindowsLimiter(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    //具体的逻辑
    @Override
    public boolean check(LimiterDTO dto) {
        return canAccess(10,5);
    }

    static final String SLIDING_WINDOWS = "SLIDING_WINDOWS";

    /**
     * 判断key的value中有效访问次数是否超过最大限定值maxCount，没有则increment 窗口访问次数+1
     * 判断与增长同步处理
     */
    public boolean canAccess(int windowsInSecond, long maxCount){
        String key  = SLIDING_WINDOWS;
        // 按key 统计集合中的有效数量
        Long count = redisTemplate.opsForZSet().zCard(key);
        if (count < maxCount) {
            increment(key,windowsInSecond);
            return false ;
        }
        return true;
    }
    /**
     * 滑动窗口的计数增长
     */
    public void increment(String key, Integer windowInSecond){
        //当前时间
        long currentTimeMillis = System.currentTimeMillis();
        // 废弃窗口的开始时间，用于删除废弃窗口
        long windowStartMs = currentTimeMillis - windowInSecond * 1000;
        // 单例模式（提高性能）
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        //清除窗口国旗成员
        zSetOperations.removeRangeByScore(key,0,windowStartMs);
        //添加当前时间
        zSetOperations.add(key,String.valueOf(currentTimeMillis),currentTimeMillis);
        redisTemplate.expire(key,windowInSecond, TimeUnit.SECONDS);
    }
}
