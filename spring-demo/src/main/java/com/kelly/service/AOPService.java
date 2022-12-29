package com.kelly.service;

import com.kelly.spring.Component;

@Component
public class AOPService implements AOPServiceI{
    @Override
    public void testAOP() {
        System.out.println("模拟 Spring 的 AOP");
    }
}
