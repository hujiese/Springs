## Spring注解开发笔记--生命周期

### 1、@Bean指定初始化和销毁方法

为了方便，我们还是使用之前的工程。

首先，在bean目录下创建一个Car.java，其内容如下：

	package com.atguigu.bean;
	
	public class Car {
	
	    public Car(){
	        System.out.println("car constructor...");
	    }
	
	    public void init(){
	        System.out.println("car ... init...");
	    }
	
	    public void detory(){
	        System.out.println("car ... detory...");
	    }
	}

然后创建一个配置类MainConfigOfLifeCycle：

	package com.atguigu.config;
	
	import com.atguigu.bean.Car;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	
	@Configuration
	public class MainConfigOfLifeCycle {
	    @Bean()
	    public Car car(){
	        return new Car();
	    }
	
	}

该配置类会返回一个Car Bean。

接着，我们创建一个测试类IOCTest_LifeCycle，并添加test01()测试方法：

	package com.atguigu.test;
	
	import org.junit.Test;
	import org.springframework.context.annotation.AnnotationConfigApplicationContext;
	
	import com.atguigu.config.MainConfigOfLifeCycle;
	
	public class IOCTest_LifeCycle {
	
	    @Test
	    public void test01(){
	        //1、创建ioc容器
	        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfLifeCycle.class);
	        System.out.println("容器创建完成...");
	    }
	
	}

该测试方法只是创建IOC容器而已，并没有做其他的工作。测试结果如下：

	car constructor...
	IOC ok ...

很普通，只是把Car注册到IOC容器中。

接下来，修改MainConfigOfLifeCycle中代码：

    @Bean(initMethod = "init", destroyMethod = "detory")
    public Car car(){

这里使用@Bean指定Car对象的初始化和销毁方法。测试结果如下:

	car constructor...
	car ... init...
	IOC ok ...

这里没有打印销毁方法，原因是这里创建的car对象销毁方法只有在IOC容器销毁时才会调用。于是修改测试函数：

    @Test
    public void test01(){
        //1、创建ioc容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfLifeCycle.class);
        System.out.println("IOC ok ...");
        applicationContext.destroy();
    }

输出结果如下：

	car constructor...
	car ... init...
	IOC ok ...
	car ... detory...

从这里可以看出，IOC的销毁方法是在IOC容器销毁时调用的。但是这里还有其他情况，我们知道，默认情况下如果实例中不添加@Scope注解指定作用域，默认IOC容器是创建单例实例的，这里可以做个实验，让容器创建多个实例，看看效果如何。首先修改MainConfigOfLifeCycle中代码

    @Bean(initMethod = "init", destroyMethod = "detory")
    @Scope("prototype")
    public Car car(){
        return new Car();
    }

这里指定@Scope为原型，允许创建多个实例。接着修改测试函数：

    @Test
    public void test01(){
        //1、创建ioc容器
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfLifeCycle.class);
        System.out.println("IOC ok ...");
        Car car = applicationContext.getBean(Car.class);
        applicationContext.destroy();
    }

输出结果如下：

	IOC ok ...
	car constructor...
	car ... init...

容器销毁了，但对象的销毁方法并没有调用。所以，在指定了@Scope("prototype")后，IOC容器创建多个实例的情况下，容器是不负责管理对象的销毁方法的。

### 2、InitializingBean和DisposableBean

除了在@Bean中配置初始化方法和销毁方法外Spring支持让某个类实现InitializingBean和DisposableBean并覆写afterPropertiesSet()和destroy()方法。

首先创建这样一个类：

	package com.atguigu.bean;
	
	import org.springframework.beans.factory.DisposableBean;
	import org.springframework.beans.factory.InitializingBean;
	import org.springframework.stereotype.Component;
	
	@Component
	public class Cat implements InitializingBean, DisposableBean {
	
	    public Cat(){
	        System.out.println("cat constructor...");
	    }
	
	    @Override
	    public void destroy() throws Exception {
	        // TODO Auto-generated method stub
	        System.out.println("cat...destroy...");
	    }
	
	    @Override
	    public void afterPropertiesSet() throws Exception {
	        // TODO Auto-generated method stub
	        System.out.println("cat...afterPropertiesSet...");
	    }
	}

注意在该类上加入@Component标签，后面我们以组件扫描包方式注册该类。这里要在MainConfigOfLifeCycle类的开始加上@ComponentScan("com.atguigu.bean")标签，这样Cat类就会被注册到IOC容器中。

修改测试方法：

    @Test
    public void test01(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfLifeCycle.class);
        System.out.println("IOC ok ...");
        applicationContext.destroy();
    }

结果如下：

	cat constructor...
	cat...afterPropertiesSet...
	IOC ok ...
	cat...destroy...

### 3、@PostConstruct和@PreDestroy

JSR250之有@PostConstruct和@PreDestroy两个标签可用于Bean的初始化和销毁方法。创建Dog类：

	package com.atguigu.bean;
	
	import javax.annotation.PostConstruct;
	import javax.annotation.PreDestroy;
	import org.springframework.stereotype.Component;
	
	@Component
	public class Dog {
	
	    public Dog(){
	        System.out.println("dog constructor...");
	    }
	
	    //对象创建并赋值之后调用
	    @PostConstruct
	    public void init(){
	        System.out.println("Dog....@PostConstruct...");
	    }
	
	    //容器移除对象之前
	    @PreDestroy
	    public void destory(){
	        System.out.println("Dog....@PreDestroy...");
	    }
	}

这里在init()方法上加@PostConstruct声明这是一个初始化方法，在destroy上加@PreDestroy标签声明这是一个销毁方法。测试结果包含：

	dog constructor...
	Dog....@PostConstruct...
	Dog....@PreDestroy...

### 4、BeanPostProcessor-后置处理器

后置处理器，初始化前后进行处理工作。

创建一个实现了BeanPostProcessor接口的类MyBeanPostProcessor：

	package com.atguigu.bean;
	
	import org.springframework.beans.BeansException;
	import org.springframework.beans.factory.config.BeanPostProcessor;
	import org.springframework.stereotype.Component;
	
	/**
	 * 后置处理器：初始化前后进行处理工作
	 * 将后置处理器加入到容器中
	 */
	@Component
	public class MyBeanPostProcessor implements BeanPostProcessor {
	
	    @Override
	    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	        // TODO Auto-generated method stub
	        System.out.println("postProcessBeforeInitialization..."+beanName+"=>"+bean);
	        return bean;
	    }
	
	    @Override
	    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	        // TODO Auto-generated method stub
	        System.out.println("postProcessAfterInitialization..."+beanName+"=>"+bean);
	        return bean;
	    }
	
	}

这里覆写了postProcessBeforeInitialization()和postProcessAfterInitialization()方法，这两个方法分别在对象的初始化前后调用。测试结果如下：

	...
	cat constructor...
	postProcessBeforeInitialization...cat=>com.atguigu.bean.Cat@3ec300f1
	cat...afterPropertiesSet...
	postProcessAfterInitialization...cat=>com.atguigu.bean.Cat@3ec300f1
	dog constructor...
	postProcessBeforeInitialization...dog=>com.atguigu.bean.Dog@1f1c7bf6
	Dog....@PostConstruct...
	postProcessAfterInitialization...dog=>com.atguigu.bean.Dog@1f1c7bf6
	...

