package com.kelly.service;

import com.kelly.spring.BeanPostProcessor;
import com.kelly.spring.Component;
import org.springframework.beans.BeansException;

/**
 * BeanPostProcessor 可以让我们更容器的干涉Bean 的初始化过程
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("userService")) {
            System.out.println("我是userService初始化前执行的方法。。。");
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (beanName.equals("userService")) {
            System.out.println("我是userService初始化后执行的方法。。。");
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
