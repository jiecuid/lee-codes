package com.kelly.service;

import com.kelly.spring.KellyApplicationContext;

public class Test {

    public static void main(String[] args) {

        // 1.创建一个 Spring 容器
        KellyApplicationContext applicationContext = new KellyApplicationContext(AppConfig.class);

        // 2. 通过 applicationContext 获取 Bean
        UserService userService = (UserService)applicationContext.getBean("userService");

        userService.test();

//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));
//
//        System.out.println(applicationContext.getBean("orderService"));


















    }
}
