package com.weng.ugroxy.proxycommon.container;

import java.io.Closeable;
import java.io.Serializable;

/**
 * @Author 翁丞健
 * @Date 2022/4/25 21:58
 * @Version 1.0.0
 */
public interface Container extends Closeable, Serializable {

    void start();

    void stop();

    void reset();

    void onChange();

}
