<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Spring注解开发笔记--AOP](#spring%E6%B3%A8%E8%A7%A3%E5%BC%80%E5%8F%91%E7%AC%94%E8%AE%B0--aop)
    - [第一步、导入aop模块；Spring AOP：(spring-aspects)](#%E7%AC%AC%E4%B8%80%E6%AD%A5%E5%AF%BC%E5%85%A5aop%E6%A8%A1%E5%9D%97spring-aopspring-aspects)
    - [第二步、定义一个业务逻辑类（MathCalculator）](#%E7%AC%AC%E4%BA%8C%E6%AD%A5%E5%AE%9A%E4%B9%89%E4%B8%80%E4%B8%AA%E4%B8%9A%E5%8A%A1%E9%80%BB%E8%BE%91%E7%B1%BBmathcalculator)
    - [第三步、定义一个日志切面类（LogAspects）](#%E7%AC%AC%E4%B8%89%E6%AD%A5%E5%AE%9A%E4%B9%89%E4%B8%80%E4%B8%AA%E6%97%A5%E5%BF%97%E5%88%87%E9%9D%A2%E7%B1%BBlogaspects)
    - [第四步、将切面类和业务逻辑类（目标方法所在类）都加入到容器中](#%E7%AC%AC%E5%9B%9B%E6%AD%A5%E5%B0%86%E5%88%87%E9%9D%A2%E7%B1%BB%E5%92%8C%E4%B8%9A%E5%8A%A1%E9%80%BB%E8%BE%91%E7%B1%BB%E7%9B%AE%E6%A0%87%E6%96%B9%E6%B3%95%E6%89%80%E5%9C%A8%E7%B1%BB%E9%83%BD%E5%8A%A0%E5%85%A5%E5%88%B0%E5%AE%B9%E5%99%A8%E4%B8%AD)
    - [第五步、编写测试](#%E7%AC%AC%E4%BA%94%E6%AD%A5%E7%BC%96%E5%86%99%E6%B5%8B%E8%AF%95)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Spring注解开发笔记--AOP

AOP【动态代理】，指在程序运行期间动态的将某段代码切入到指定方法指定位置进行运行的编程方式。

如果要使用Spring的AOP功能，需要按照下面三步走;

- 1）、将业务逻辑组件和切面类都加入到容器中；告诉Spring哪个是切面类（@Aspect）
- 2）、在切面类上的每一个通知方法上标注通知注解，告诉Spring何时何地运行（切入点表达式）
- 3）、开启基于注解的aop模式；@EnableAspectJAutoProxy

下面将以一个除法的例子来说明具体的用法。

#### 第一步、导入aop模块；Spring AOP：(spring-aspects)

使用AOP功能需要加入meavn依赖：
```xml
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-aspects</artifactId>
	<version>4.3.12.RELEASE</version>
</dependency>
```
#### 第二步、定义一个业务逻辑类（MathCalculator）

我们需要在业务逻辑运行的时候将日志进行打印（方法之前、方法运行结束、方法出现异常，xxx）。还是用之前的工程，创建aop包，该类编写如下：
```java
package com.atguigu.aop;

public class MathCalculator {
	
	public int div(int i,int j){
		System.out.println("MathCalculator...div...");
		return i/j;	
	}
    // public int mul(int i,int j){
    //     System.out.println("MathCalculator...mul...");
    //     return i * j;
    // }
}
```
#### 第三步、定义一个日志切面类（LogAspects）

切面类里面的方法需要动态感知MathCalculator.div运行到哪里然后执行。切面的通知方法有如下几种;

- 前置通知(@Before)：logStart：在目标方法(div)运行之前运行
- 后置通知(@After)：logEnd：在目标方法(div)运行结束之后运行（无论方法正常结束还是异常结束）
- 返回通知(@AfterReturning)：logReturn：在目标方法(div)正常返回之后运行
- 异常通知(@AfterThrowing)：logException：在目标方法(div)出现异常以后运行
- 环绕通知(@Around)：动态代理，手动推进目标方法运行（joinPoint.procced()）

必须告诉Spring哪个类是切面类(给切面类上加一个注解：@Aspect)，我们创建的切面类是LogAspects：
```java
package com.atguigu.aop;

import org.aspectj.lang.annotation.Aspect;

@Aspect
public class LogAspects {
}
```
当然，我们还需要给切面类的目标方法标注何时何地运行（通知注解）。修改LogAspects.java代码：
```java
package com.atguigu.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
	* 切面类
	* @Aspect： 告诉Spring当前类是一个切面类
	*
	*/
@Aspect
public class LogAspects {

	//抽取公共的切入点表达式
	//1、本类引用
	//2、其他的切面引用
	// @Pointcut("execution(public int com.atguigu.aop.MathCalculator.div(..))")
	@Pointcut("execution(public int com.atguigu.aop.MathCalculator.*(..))")
	public void pointCut(){};

	//@Before在目标方法之前切入；切入点表达式（指定在哪个方法切入）
	@Before("pointCut()")
	public void logStart(JoinPoint joinPoint){
		Object[] args = joinPoint.getArgs();
		System.out.println(""+joinPoint.getSignature().getName()+" runing ... @Before:args is: {"+Arrays.asList(args)+"}");
	}

	@After("com.atguigu.aop.LogAspects.pointCut()")
	public void logEnd(JoinPoint joinPoint){
		System.out.println(""+joinPoint.getSignature().getName()+" end ... @After");
	}

	//JoinPoint一定要出现在参数表的第一位
	@AfterReturning(value="pointCut()",returning="result")
	public void logReturn(JoinPoint joinPoint,Object result){
		System.out.println(""+joinPoint.getSignature().getName()+" return ok ... @AfterReturning:result: {"+result+"}");
	}

	@AfterThrowing(value="pointCut()",throwing="exception")
	public void logException(JoinPoint joinPoint,Exception exception){
		System.out.println(""+joinPoint.getSignature().getName()+" exception ... exception information: {"+exception+"}");
	}

}
```

#### 第四步、将切面类和业务逻辑类（目标方法所在类）都加入到容器中

这里编写配置类MainConfigOfAOP：
```java
package com.atguigu.config;

import com.atguigu.aop.LogAspects;
import com.atguigu.aop.MathCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@Configuration
public class MainConfigOfAOP {

	//业务逻辑类加入容器中
	@Bean
	public MathCalculator calculator(){
		return new MathCalculator();
	}

	//切面类加入到容器中
	@Bean
	public LogAspects logAspects(){
		return new LogAspects();
	}
}
```
需要注意的是，必须给配置类中加 @EnableAspectJAutoProxy ，告诉容器开启基于注解的aop模式。

#### 第五步、编写测试
```java
package com.atguigu.test;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.atguigu.aop.MathCalculator;
import com.atguigu.config.MainConfigOfAOP;

public class IOCTest_AOP {

	@Test
	public void test01(){
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);
		MathCalculator mathCalculator = applicationContext.getBean(MathCalculator.class);

		mathCalculator.div(6, 3);
		// mathCalculator.mul(6, 2);
		applicationContext.close();
	}

}
```
运行结果如下;

	div runing ... @Before:args is: {[6, 3]}
	MathCalculator...div...
	div end ... @After
	div return ok ... @AfterReturning:result: {2}

修改部分代码如下;
```java
mathCalculator.div(6, 0);
```
输出结果为;

	div runing ... @Before:args is: {[6, 0]}
	MathCalculator...div...
	div end ... @After
	div exception ... exception information: {java.lang.ArithmeticException: / by zero}
	
	java.lang.ArithmeticException: / by zero
	
		at com.atguigu.aop.MathCalculator.div(MathCalculator.java:7)
	
	.......

直接抛出异常。

需要注意的是，我们这里是使用了IOC容器获取容器中的MathCalculator对象，但如果我们通过：
```java
MathCalculator mathCalculator = new MathCalculator();
mathCalculator.div(1, 1);
```
调用，AOP是不会起作用的。