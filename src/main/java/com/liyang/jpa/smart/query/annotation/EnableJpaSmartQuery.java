package com.liyang.jpa.smart.query.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.liyang.jpa.smart.query.config.JpaSmartQueryAutoConfiguration;
import com.liyang.jpa.smart.query.service.ApplicationContextSupport;


@Retention(RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import(JpaSmartQueryAutoConfiguration.class)
public @interface EnableJpaSmartQuery {

}
