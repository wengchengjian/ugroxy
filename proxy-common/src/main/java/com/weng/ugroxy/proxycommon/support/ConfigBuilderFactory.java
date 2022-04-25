package com.weng.ugroxy.proxycommon.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author 翁丞健
 * @Date 2022/4/25 21:23
 * @Version 1.0.0
 */
public class ConfigBuilderFactory {

    public static ConfigBuilder getConfigBuilder(){
        return new ConfigBuilder();
    }

    private static class ConfigBuilder{

        private Properties properties;

        public ConfigBuilder(){
            properties = new Properties();
        }

        public Config getConfig(){
            return this.getConfig(Config.DEFAULT_CONFIG_NAME);
        }

        public Config getConfig(String configFilePath){
            InputStream input = null;

            try{
                input = ConfigBuilderFactory.class.getClassLoader().getResourceAsStream(configFilePath);
                if(input==null){
                    throw new IOException("config file not found");
                }else{
                    properties.load(input);
                    input.close();
                }
                return new Config(properties);
            }catch (IOException e){
                throw new RuntimeException("load config file error",e);
            }
        }
    }
}
