package com.weng.ugroxy.proxycommon.container;


import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author 翁丞健
 * @Date 2022/4/25 21:59
 * @Version 1.0.0
 */
@Slf4j
public class ContainerFactoryHelper {

    private static volatile AtomicBoolean running = new AtomicBoolean(false);

    private static List<Container> cacheContainers = new CopyOnWriteArrayList<>();

    public static void start(List<Container> containers){
        boolean isRunning = running.get();

        if(isRunning){
            log.warn("ContainerFactory is running.Please wait a minutes.");
        }
        if(running.compareAndSet(false, true)){
            log.info("ContainerFactory start");

            cacheContainers.addAll(containers);

            startContainer(containers);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("ContainerFactory is shutdowning");
                while(running.compareAndSet(true, false)){
                    stopContainers();
                }
            }));
        }

    }

    private static void stopContainers() {
        List<Container> containers = cacheContainers;
        containers.forEach(container -> {
            try{
                log.info("stopping container [{}]", container);
                container.stop();
                log.info("container [{}] stopped", container);
            }catch (Exception e){
                log.error("container [{}] stopped error: {}", container,e.getMessage());
            }
        });
    }

    private static void startContainer(List<Container> containers) {
        containers.forEach(container -> {
            try{
                log.info("starting container [{}]", container);
                container.start();
                log.info("container [{}] started", container);
            }catch (Exception e){
                log.error("container [{}] started error: {}", container,e.getMessage());
            }

        });
    }

}
