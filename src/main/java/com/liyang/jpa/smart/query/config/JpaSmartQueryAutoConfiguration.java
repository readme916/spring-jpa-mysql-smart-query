package com.liyang.jpa.smart.query.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@EnableCaching
@ComponentScan("com.liyang.jpa.smart.query.service")
@AutoConfigureAfter(JpaRepositoriesAutoConfiguration.class)
@Configuration
public class JpaSmartQueryAutoConfiguration {

}
