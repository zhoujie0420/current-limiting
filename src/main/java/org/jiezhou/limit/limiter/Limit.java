package org.jiezhou.limit.limiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流策略注解
 */

//@Target 表示注解范围   @Retention 注解的生命周期
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    int limit() default 0;

    int time() default 0;
    String key()  default "";

    String msg() default "系统服务繁忙";
}
