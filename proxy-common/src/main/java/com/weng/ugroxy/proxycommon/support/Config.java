package com.weng.ugroxy.proxycommon.support;

import lombok.Data;

import java.util.Properties;

/**
 * @Author 翁丞健
 * @Date 2022/4/25 21:14
 * @Version 1.0.0
 */
@Data
public class Config {
    public final static String DEFAULT_CONFIG_NAME = "ugroxy.properties";

    private String configName;

    private String configId;

    private Properties properties;

    public Config(){

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
}
