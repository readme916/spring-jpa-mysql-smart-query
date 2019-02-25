package com.liyang.jpa.mysql.config;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

public class ApplicationContextSupport implements ApplicationContextAware{
	
	protected final static Logger logger = LoggerFactory.getLogger(ApplicationContextSupport.class); 
	private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	ApplicationContextSupport.applicationContext = applicationContext;
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }
    //通過註解，查找bean
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotation){
    	return getApplicationContext().getBeansWithAnnotation(annotation);
    }
    public static <T> Map<String , T> getBeansOfType(Class<T> clazz){
    	return getApplicationContext().getBeansOfType(clazz);
    }
}
