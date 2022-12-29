package com.kelly.spring;

/**
 * 模拟 BeanDefinition
 */
public class BeanDefinition {

    /**
     * bean 的定义
     */
    private Class type;
    /**
     * bean 的作用域
     */
    private String scope;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
