Bean的创建的生命周期

AService.class--->实例化（推断构造方法-->反射得到实例化对象）-->依赖注入-->初始化前-->初始化-->初始化后（AOP）-->代理对象-->放入Map<beanName,beanDefinition>(单例池)-->Bean对象


1.推断构造方法(先 byType ,然后 byName)
    核心有两步
    1.判断用当前类里的那个构造方法
    2.选好构造方法之后，还得判断构造方法的有没有入参，如果有入参的话，还得找到这个入参对应的对象传给当前这个构造方法

    Spring 扫描的这个类里面可能会有多个构造方法：
    1. 当类里只有一个无参构造方法的时候，则会使用这个无参构造方法
    2. 当类里只有一个有参构造方法的时候，则会使用这个有参构造方法
    3. 当类里有有一个无参构造方法,有一个有参构造方法的时候, 则会使用无参构造方法
    4. 当类里有两个有两个有参构造方法的时候，比如其中一个有一个参数，另一个是两个参数，就会报错(NoSuchMethodException,找不到那个无参构造方法了)，
    Spring 不知道使用那个了，可以在任何一个有参构造方法上加 @Autowired 注解告诉 Spring 用那个构造方法

    AService 的构造方法的入参 BService 必须是一个Bean, 当 Spring 进行构造方法实例化对象的时候，
    首先会去单例池 Map<beanName,beanDefinition> 里找这个入参 BService 的Bean对象,如果找到了，就会直接拿来使用，如果没找到就需要去创建一个。

    这里就会有一种出现循环依赖的情况，比如 AService 的属性是 BService， BService 的属性是 AService；那么Spring 是如何解决循环依赖的呢？



    这里还有一种情况，在单例池 Map<beanName,beanDefinition>里，可能会有多个 BService 类型的对象，他们的名字（BeanName）不同，但是类型相同，
那么 Spring 是是怎么确定 我当前 AService 的构造方法需要的入参数 BService 是那一个 BService的Bean对象呢？

    -- Spring 会先根据类型去匹配，如果找只到了一个 BService 类型的 Bean，那么直接拿来使用；
        如果找到了多个 BService 类型的 Bean，那么再根据 名称去匹配。这就是我们说的 先 byType ,然后 byName。

2.依赖注入
    Spring 会去找那些加了 @Autowired 注解的属性，进行属性填充，在属性填充的时候也是先 byType ,然后 byName。


3.初始化前
    Spring 容器在启动的时候会去找那些加了 @PostConstruct 注解的方法，去执行这些方法。@PostConstruct 注解必须加在非静态无返回值的方法上。


  在Spring中没有加 @Autowired 注解的属性怎么填充？

   1.通过@PostConstruct 注解的方法去为这些属性赋值，
   2.实现了InitializingBean接口,通过重写 afterPropertiesSet()方法进行赋值
   3.通过 Aware接口回调进行赋值
   4.通过实现beanPostProcessor (bean的后置处理器) 进行赋值