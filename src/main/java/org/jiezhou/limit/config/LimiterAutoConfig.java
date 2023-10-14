package org.jiezhou.limit.config;


import org.jiezhou.limit.aop.LimiterAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LimiterAutoConfig {


    @Bean
    public LimiterAop limiterAop(){
        return new LimiterAop();
    }

}
