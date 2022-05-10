package com.weng.ugroxy.proxycommon.support;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 翁丞健
 * @Date 2022/4/25 21:14
 * @Version 1.0.0
 */
@Data
public class Config {
    public final static String DEFAULT_CONFIG_NAME = "ugroxy.properties";

    private static Map<String, Config> instances = new ConcurrentHashMap<String, Config>();

    private String configName;

    private String configId;

    private Properties properties;

    public Config(){
        initConfig(DEFAULT_CONFIG_NAME);
    }
    private void initConfig(String configFile) {
        InputStream is = Config.class.getClassLoader().getResourceAsStream(configFile);
        try {
            properties.load(is);
            is.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获得Configuration实例。 默认为config.property
     *
     * @return Configuration实例
     */
    public static Config getInstance() {
        return getInstance(DEFAULT_CONFIG_NAME);
    }

    public static Config getInstance(String configFile) {
        Config config = instances.get(configFile);
        if (config == null) {
            synchronized (instances) {
                config = instances.get(configFile);
                if (config == null) {
                    config = new Config(configFile);
                    instances.put(configFile, config);
                }
            }
        }
        return config;
    }

    public Config(Properties properties){
        this.properties = properties;
    }

    public Config(String configName){
        this.configName = configName;
    }

    public String getProperty(String key,String defaultValue){
        String propertyValue = properties.getProperty(key);

        return propertyValue==null ? defaultValue : propertyValue;
    }

    public void setProperty(String key,String value){
        properties.setProperty(key,value);
    }

    public int getIntProperty(String key,int defaultValue){
        return getTypeProperty(key,int.class,defaultValue);
    }

    public double getDoubleProperty(String key,double defaultValue){
        return getTypeProperty(key,double.class,defaultValue);
    }

    public long getLongProperty(String key,long defaultValue){
        return getTypeProperty(key,long.class,defaultValue);
    }

    public <T> T getTypeProperty(String key,Class<T> type,T defaultValue){
        String propertyValue = properties.getProperty(key);

        if(propertyValue==null){
            return defaultValue;
        }
        try{
            //TODO 待优化
            T value;
            if (int.class.equals(type)) {
                value = type.cast(Integer.parseInt(propertyValue));
            } else if (long.class.equals(type)) {
                value = type.cast(Long.parseLong(propertyValue));
            } else if (double.class.equals(type)) {
                value = type.cast(Double.parseDouble(propertyValue));
            } else if (boolean.class.equals(type)) {
                value = type.cast(Boolean.parseBoolean(propertyValue));
            } else {
                value = type.cast(propertyValue);
            }
            return value;
        }catch (Exception e){
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key,boolean defaultValue){
        return getTypeProperty(key,boolean.class,defaultValue);
    }

    public String getStringProperty(String key, String defaultValue) {
        return getTypeProperty(key,String.class,defaultValue);
    }
}
