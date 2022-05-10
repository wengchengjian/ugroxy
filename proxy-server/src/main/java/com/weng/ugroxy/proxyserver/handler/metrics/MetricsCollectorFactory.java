package com.weng.ugroxy.proxyserver.handler.metrics;

import lombok.Builder;
import lombok.Data;
import org.thymeleaf.util.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author 翁丞健
 * @Date 2022/5/10 22:21
 * @Version 1.0.0
 */
public class MetricsCollectorFactory {

    private static final Map<Integer,MetricsCollector> metricsCollectorMap = new ConcurrentHashMap<>(64);

    private static Lock lock = new ReentrantLock();

    @Data
    @Builder
    public  class MetricsCollector {
        private Integer port;

        private AtomicLong readBytes = new AtomicLong(0);

        private AtomicLong writeBytes = new AtomicLong(0);

        private AtomicLong readMsgs = new AtomicLong(0);

        private AtomicLong writeMsgs = new AtomicLong(0);

        private AtomicLong channels = new AtomicLong(0);

        private Metrics getAndResetMetrics(){
            return Metrics.builder()
                    .port(port)
                    .channels(channels.getAndSet(0))
                    .timestamp(System.currentTimeMillis())
                    .readBytes(readBytes.getAndSet(0))
                    .writeBytes(writeBytes.getAndSet(0))
                    .readMsgs(readMsgs.getAndSet(0))
                    .wroteMsgs(writeMsgs.getAndSet(0))
                    .build();
        }

        public Metrics getMetrics(){
            return Metrics.builder()
                    .port(port)
                    .channels(channels.get())
                    .timestamp(System.currentTimeMillis())
                    .readBytes(readBytes.get())
                    .writeBytes(writeBytes.get())
                    .readMsgs(readMsgs.get())
                    .wroteMsgs(writeMsgs.get())
                    .build();
        }

        public void incrementReadBytes(long bytes) {
            readBytes.addAndGet(bytes);
        }

        public void incrementWroteBytes(long bytes) {
            writeBytes.addAndGet(bytes);
        }

        public void incrementReadMsgs(long msgs) {
            readMsgs.addAndGet(msgs);
        }

        public void incrementWroteMsgs(long msgs) {
            writeMsgs.addAndGet(msgs);
        }

        public AtomicLong getChannels() {
            return channels;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

    }

    public static  MetricsCollector getMetricsCollector(Integer port) {
        MetricsCollector metricsCollector = metricsCollectorMap.get(port);
        if(metricsCollector == null) {
            lock.lock();
            try{
                metricsCollector = metricsCollectorMap.get(port);
                if(metricsCollector==null){
                    metricsCollector = MetricsCollector.builder().port(port).build();
                    metricsCollectorMap.put(port,metricsCollector);
                }
            }finally {
                lock.unlock();
            }
        }
        return metricsCollector;
    }

    public static List<Metrics> getAndResetAllMetrics(){
        List<Metrics> metricsList = new ArrayList<>(64);

        Set<Integer> keySet = metricsCollectorMap.keySet();

        for (Integer key : keySet) {
            MetricsCollector metricsCollector = metricsCollectorMap.get(key);

            metricsList.add(metricsCollector.getAndResetMetrics());
        }

        return metricsList;
    }

    public static List<Metrics> getAllMetrics(){
        List<Metrics> metricsList = new ArrayList<>(64);

        Set<Integer> keySet = metricsCollectorMap.keySet();

        for (Integer key : keySet) {
            MetricsCollector metricsCollector = metricsCollectorMap.get(key);

            metricsList.add(metricsCollector.getMetrics());
        }

        return metricsList;
    }







}
