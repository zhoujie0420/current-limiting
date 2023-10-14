package org.jiezhou.limit.limiter.Impl;

import org.jiezhou.limit.limiter.Dao.LimiterDTO;
import org.jiezhou.limit.limiter.LimiterAbstract;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class CounterLimiter extends LimiterAbstract {

    RedisTemplate redisTemplate;

    public CounterLimiter(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @Override
    public boolean check(LimiterDTO limiterDTO) {
        int limit = limiterDTO.limit;
        return check(limit);
    }

    static final String KEY = "COUNTER_LIMITER";

    /**
     * 默认一分钟一个时间段
     */
    private boolean check(int limit){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String text = simpleDateFormat.format(new Date());
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        try {
            // 转毫秒
            long time = simpleDateFormat.parse(text).getTime();
            // 移除小于time的元素
            zSetOperations.removeRangeByScore(KEY, 0,time);
        }catch (ParseException e){
            e.printStackTrace();
        }
        Long count = redisTemplate.opsForZSet().zCard(KEY);
        if(count < limit){
            // 没有限流
            long time = new Date().getTime();
            zSetOperations.add(KEY,String.valueOf(time),time);
            return false;
        }else {
            return true;
        }
    }


}
