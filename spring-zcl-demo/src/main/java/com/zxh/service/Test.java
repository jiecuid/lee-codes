package com.zxh.service;

import com.zxh.spring.ZxhApplicationContext;

public class Test {

    public static void main(String[] args) {

        ZxhApplicationContext applicationContext = new ZxhApplicationContext(AppConfig.class);

//        UserService userService = (UserService) applicationContext.getBean("userService");

//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("userService"));
//        System.out.println(applicationContext.getBean("orderService"));
          UserService userService = (UserService) applicationContext.getBean("userService");
          userService.test();


    }
}
