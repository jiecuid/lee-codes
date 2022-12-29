package com.kelly.service;

import com.kelly.spring.BeanPostProcessor;
import com.kelly.spring.Component;
import org.springframework.beans.BeansException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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

        if (beanName.equals("AOPService")) {
            Object proxyInstance = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(),
                    bean.getClass().getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            System.out.println("JDK动态代理切面逻辑...");
                            return method.invoke(bean, args);
                        }
                    });
            return proxyInstance;
        }
        return bean;
    }
}
