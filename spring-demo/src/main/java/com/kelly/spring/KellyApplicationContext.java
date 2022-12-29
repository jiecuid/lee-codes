package com.kelly.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class KellyApplicationContext {

    /**
     * 模拟 Spring 扫描的类
     */
    private Class configClass;

    /**
     * 缓存 BeanDefinition
     */
    private ConcurrentHashMap<String /*beanName*/, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();


    /**
     * 单例 Bean
     */
    private ConcurrentHashMap<String /*beanName*/, Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * beanPostProcessorList
     */
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /**
     * 模拟 Spring 容器加载过程
     * @param configClass
     */
    public KellyApplicationContext(Class configClass){
        this.configClass = configClass;

        // 1.模拟 Spring 扫描的底层实现
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            // 扫描加了@ComponentScan 注解的 Bean
            ComponentScan annotation = (ComponentScan)configClass.getAnnotation(ComponentScan.class);
            // 获取扫描的路径 com.kelly.service
            String value = annotation.value();
            value = value.replace(".", "/");

            //通过 ClassLoader 获取 扫描路径下的资源
            ClassLoader classLoader = KellyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(value);
            File file = new File(resource.getFile());
            System.out.println("获取到的资源路径：" + file);

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath(); // /Users/kelly/Desktop/env/code/lee-codes/spring-demo/target/classes/com/kelly/service/UserService.class
                    System.out.println("扫描到的类：" +fileName);
                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class")); // /com/kelly/service/UserService
                        className =  className.replace("/", "."); // com.kelly.service.UserService
                        System.out.println("类的class对象：" + className);

                        try {
                            Class<?> aClass = classLoader.loadClass(className);
                            // 3.判断拿到的类上是否有 @Component 注解， 构建 BeanDefinition
                            if (aClass.isAnnotationPresent(Component.class)) {

                                // 扫描实现了 BeanPostProcessor的类（Bean的后置处理器），缓存到 beanPostProcessorList 里
                                if (BeanPostProcessor.class.isAssignableFrom(aClass)) {
                                    BeanPostProcessor instance = (BeanPostProcessor)aClass.newInstance();
                                    beanPostProcessorList.add(instance);
                                }


                                Component componentAnnotation = aClass.getAnnotation(Component.class);
                                String beanName = componentAnnotation.value();
                                if (beanName.equals("")) {
                                    // 给出默认的 BeanName
                                    beanName = Introspector.decapitalize(aClass.getSimpleName());
                                }

                                // 构建 BeanDefinition
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(aClass);
                                if (aClass.isAnnotationPresent(Scope.class)) {
                                    // 获取类的【作用域】放置到 BeanDefinition里
                                    Scope scopeAnnotation = aClass.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                }else {
                                    // 默认单例
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);

                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }


                }
            }



        }


        // 2.模拟 实例化单例 Bean
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                // 单例
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }


        }
    }

    /**
     * 模拟 Bean 的创建
     *
     * Bean 的生命周期
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    public Object createBean(String beanName, BeanDefinition beanDefinition) {

        Class clazz = beanDefinition.getType();

        try {
            // 1.实例化 创建对象
            Object instance = clazz.getConstructor().newInstance();

            // 2.模拟 依赖注入（属性填充）
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    // 加了@Autowired 注解的才需要注入
                    field.setAccessible(true); // 开启反射修改
                    field.set(instance, getBean(field.getName()));
                }
            }

            // 3. Aware 回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            // 4.beanPostProcessor (bean的后置处理器): postProcessBeforeInitialization
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // 5.InitializingBean 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }

            // 6.beanPostProcessor (bean的后置处理器): postProcessAfterInitialization
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }

            //  初始化后AOP

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 模拟 getBean 的底层实现
     *
     * @param beanName
     * @return
     */
    public Object getBean(String beanName){
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) {
                // 单例
                Object bean = singletonObjects.get(beanName);
                if (bean == null) {
                    Object o = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, o);
                }
                return bean;
            } else {
                // 多例
               return createBean(beanName, beanDefinition);
            }
        }
    }
}
