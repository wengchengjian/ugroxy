package com.weng.ugroxy.proxyserver.support;

import com.weng.ugroxy.proxycommon.utils.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

/**
 * @Author 翁丞健
 * @Date 2022/5/6 21:48
 * @Version 1.0.0
 */
@Slf4j
public class SSLContextHolder {

    public static SSLContext initSSLContext(){
        log.info("初始化SSLContext......");
        log.info("检查ssl配置文件是否存在......");
        ApplicationContext context = ApplicationContextUtil.getContext();

        Environment environment = context.getEnvironment();

        String jksPath = environment.getProperty("server.ssl.jksPath");

        if(StringUtils.isBlank(jksPath)){
            log.error("the keystore path may be null or empty,the sslContext won't be initialized,please check the configuration file,and try again!");
            throw new RuntimeException();
        }

        final String keyStorePassword = environment.getProperty("server.ssl.keyStorePassword");
        final String keyManagerPassword = environment.getProperty("server.ssl.keyManagerPassword");

        if(StringUtils.isBlank(keyStorePassword)){
            log.error("the key store password  may be null or empty,the sslContext won't be initialized,please check the configuration file,and try again!");
            throw new RuntimeException();
        }

        if(StringUtils.isBlank(keyManagerPassword)){
            log.error("the key manager password  may be null or empty,the sslContext won't be initialized,please check the configuration file,and try again!");
            throw new RuntimeException();
        }

        boolean needClientAuth = environment.getRequiredProperty("server.ssl.needClientAuth",boolean.class);

        try{
            log.info("try loading keystore. the key store path is {}",jksPath);
            InputStream inputStream = jksDatastore(jksPath);

            SSLContext sslContext = SSLContext.getInstance("TLS");

            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream,keyStorePassword.toCharArray());

            log.info("initializing key manager...");
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore,keyManagerPassword.toCharArray());

            TrustManager[] trustManagers = null;
            if(needClientAuth){
                log.warn("the server need client auth,initializing trust manager...");

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                trustManagers = trustManagerFactory.getTrustManagers();
            }

            log.info("initializing ssl context...");
            sslContext.init(keyManagerFactory.getKeyManagers(),trustManagers,null);
            log.info("ssl context initialized successfully!");
            return sslContext;
        }catch (Exception ex){
            log.error("Unable to initialize SSL context. Cause = {}, errorMessage = {}.", ex.getCause(),
                    ex.getMessage());
            return null;
        }

    }

    private static InputStream jksDatastore(String jksPath) throws FileNotFoundException {
        ClassLoader classLoader = SSLContextHolder.class.getClassLoader();
        URL jksUrl = classLoader.getResource(jksPath);
        if (jksUrl != null) {
            log.info("Starting with jks at {}, jks normal {}", jksUrl.toExternalForm(), jksUrl);
            return classLoader.getResourceAsStream(jksPath);
        }

        log.warn("No keystore has been found in the bundled resources. Scanning filesystem...");
        File jksFile = new File(jksPath);
        if (jksFile.exists()) {
            log.info("Loading external keystore. Url = {}.", jksFile.getAbsolutePath());
            return new FileInputStream(jksFile);
        }

        log.warn("The keystore file does not exist. Url = {}.", jksFile.getAbsolutePath());
        return null;
    }
}
