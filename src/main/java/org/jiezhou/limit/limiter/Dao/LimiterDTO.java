package org.jiezhou.limit.limiter.Dao;

import lombok.Data;
import org.jiezhou.limit.config.LimiterAutoConfig;

@Data
public class LimiterDTO {

    /**
     * 限制次数
     */
    public final int limit;


    /**
     * 限制时间
     */
    private final int time;

    /**
     * key
     */
    private final String key;

    public LimiterDTO(int limit, int time,String key){
        this.limit = limit;
        this.time = time;
        this.key = key;
    }
}
