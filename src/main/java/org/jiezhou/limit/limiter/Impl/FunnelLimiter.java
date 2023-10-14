package org.jiezhou.limit.limiter.Impl;

import org.jiezhou.limit.limiter.Dao.LimiterDTO;
import org.jiezhou.limit.limiter.LimiterAbstract;


/**
 * 漏桶算法
 */
public class FunnelLimiter extends LimiterAbstract {

    @Override
    public boolean check(LimiterDTO dto) {
        return allow();
    }

    public boolean allow() {
        return Funnel.getInstance().watering(1);
    }

    static class Funnel {
        /**
         * 水流速度
         */
        private float waterRate;
        /**
         * 漏斗容量
         */
        private Integer capacity;
        /**
         * 剩余容量
         */
        private Integer leftCapacity;
        /**
         * 上一次访问时间
         */
        private long lastAccess;

        private static Funnel funnel;

        private Funnel(Integer waterRate, Integer capacity) {
            this.waterRate = waterRate;
            this.capacity = capacity;
            this.leftCapacity = capacity;
            this.lastAccess = System.currentTimeMillis();
        }

        public synchronized static Funnel getInstance() {
            if (funnel == null) {
                funnel = new Funnel(1, 5);
            }
            return funnel;
        }

        /**
         * 出水
         */
        public void runningWaster() {
            long nowTime = System.currentTimeMillis();
            long deltaTime = nowTime - lastAccess;

            //计算当前时间单位内的出水量
            int deltaCapacity = (int) (waterRate * deltaTime);
            // 更正上一次请求时间
            this.lastAccess = nowTime;
            //上一次请求时间过长，计算出负值需要重新校验
            if (deltaCapacity < 0) {
                this.leftCapacity = capacity;
                return;
            }
            if (deltaCapacity < 1) return;
            // 当前桶空间大小 = 单位时间出水量 + 剩余
            this.leftCapacity += deltaCapacity;

            if (this.leftCapacity > this.capacity) {
                this.leftCapacity = this.capacity;
            }
        }

        /**
         * 进水
         */
        public boolean watering(Integer waterCapacity) {
            // 计算当前桶容量
            runningWaster();
            if (leftCapacity >= waterCapacity) {
                this.leftCapacity -= waterCapacity;
                return false;
            }
            return true;
        }
    }


}
