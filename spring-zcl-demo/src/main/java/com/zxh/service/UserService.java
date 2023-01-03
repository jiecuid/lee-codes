package com.zxh.service;

import com.zxh.spring.*;

@Component
public class UserService implements BeanNameAware, InitializingBean {
    
    @Autowired
    private OrderService orderService;
    private String beanName;

    private String xxx;
    
    public  void test(){
        System.out.println("orderService = " + orderService);
    }


    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("afterPropertiesSet = " + "afterPropertiesSet");
    }
}
