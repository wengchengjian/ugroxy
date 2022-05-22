package com.weng.ugroxy.test.config;

import com.weng.ugroxy.test.model.Animal;
import com.weng.ugroxy.test.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author 翁丞健
 * @Date 2022/5/21 23:30
 * @Version 1.0.0
 */
@Configuration
public class TestConfig {

    @Bean
    public Person person(){
        return new Person();
    }

    @Bean
    public Animal animal(){
        return new Animal();
    }

    @PostConstruct
    public void init(){
        person().animal = animal();
    }
}
