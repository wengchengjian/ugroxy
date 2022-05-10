package com.weng.ugroxy.proxycommon.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author 翁丞健
 * @Date 2022/5/1 18:42
 * @Version 1.0.0
 */
public class SequenceGenerator {
    private static volatile AtomicLong sequence = new AtomicLong(0);

    public static long next() {
        if(sequence.get()==Long.MAX_VALUE){
            sequence.compareAndSet(Long.MAX_VALUE,0);
        }
        return sequence.incrementAndGet();
    }
}
