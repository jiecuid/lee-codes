package com.kelly.service;

import com.kelly.spring.Autowired;
import com.kelly.spring.BeanNameAware;
import com.kelly.spring.Component;
import com.kelly.spring.InitializingBean;


@Component("userService")
//@Scope(value = "prototype")
public class UserService implements BeanNameAware, InitializingBean {

    private String testFiled;

    private String beanName;

    /**
     * 模拟 依赖注入
     */
    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println("UserService 属性注入 [orderService]：" + orderService);
    }

    /**
     * Aware 回调 获取 beanName
     * @param beanName
     */
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * InitializingBean 初始化
     */
    @Override
    public void afterPropertiesSet() {
        if (beanName.equals("userService")) {
            System.out.println("我是 userService 初始化执行的方法。。。");
        }
    }
}
