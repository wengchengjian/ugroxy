package com.weng.ugroxy.proxyserver.handler;

import com.weng.ugroxy.proxycommon.utils.ApplicationContextUtil;
import com.weng.ugroxy.proxyserver.autoconfigure.config.UgroxyServerProperties;
import com.weng.ugroxy.proxyserver.support.SSLContextHolder;
import io.netty.handler.ssl.SslHandler;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLEngine;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @Author 翁丞健
 * @Date 2022/5/6 21:43
 * @Version 1.0.0
 */
@Service
public class ProxySSLhandler extends SslHandler {

    public ProxySSLhandler(){
        super(EngineHelper.getSSLEngine());
    }

    private static class EngineHelper{
        public static SSLEngine getSSLEngine(){
            SSLEngine sslEngine = Objects.requireNonNull(SSLContextHolder.initSSLContext()).createSSLEngine();
            sslEngine.setUseClientMode(false);

            boolean needClientAuth = ApplicationContextUtil.getContext().getEnvironment().getRequiredProperty("server.ssl.needsClientAuth", boolean.class);

            if(needClientAuth){
                sslEngine.setNeedClientAuth(true);
            }
            return sslEngine;
        }
    }

    public ProxySSLhandler(SSLEngine engine) {
        super(engine);
    }

    public ProxySSLhandler(SSLEngine engine, boolean startTls) {
        super(engine, startTls);
    }

    public ProxySSLhandler(SSLEngine engine, Executor delegatedTaskExecutor) {
        super(engine, delegatedTaskExecutor);
    }

    public ProxySSLhandler(SSLEngine engine, boolean startTls, Executor delegatedTaskExecutor) {
        super(engine, startTls, delegatedTaskExecutor);
    }
}
