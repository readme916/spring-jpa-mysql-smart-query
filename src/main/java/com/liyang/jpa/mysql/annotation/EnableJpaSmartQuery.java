package com.liyang.jpa.mysql.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.liyang.jpa.mysql.config.JpaSmartQuerySupport;
import com.liyang.jpa.mysql.service.ApplicationContextSupport;


@Retention(RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import(JpaSmartQuerySupport.class)
public @interface EnableJpaSmartQuery {

}
