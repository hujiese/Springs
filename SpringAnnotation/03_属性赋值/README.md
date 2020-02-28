## Spring注解开发笔记--属性赋值

### 1、@Value赋值

修改bean目录里的Person.java，修改部分如下：
```java
@Value("jack")
private String name;
@Value("#{24-4}")
private Integer age;
@Value("heki")
private String nickName;
```
这里使用@Value的方式给Person的属性进行赋值。

接着创建配置类MainConfigOfPropertyValues：
```java
package com.atguigu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atguigu.bean.Person;

@Configuration
public class MainConfigOfPropertyValues {

	@Bean
	public Person person(){
		return new Person();
	}

}
```
然后再创建测试类IOCTest_PropertyValue：
```java
package com.atguigu.test;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.atguigu.bean.Person;
import com.atguigu.config.MainConfigOfPropertyValues;

public class IOCTest_PropertyValue {
	AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfPropertyValues.class);
	@Test
	public void test01(){
		printBeans(applicationContext);
		System.out.println("=============");

		Person person = (Person) applicationContext.getBean("person");
		System.out.println(person);
	}

	private void printBeans(AnnotationConfigApplicationContext applicationContext){
		String[] definitionNames = applicationContext.getBeanDefinitionNames();
		for (String name : definitionNames) {
			System.out.println(name);
		}
	}

}
```
最后的输出包含如下内容：
```
...
=============
Person [name=jack, age=20, nickName=heki]
```
说明赋值成功。

### 2、@PropertySource读取外部配置

使用@PropertySource读取外部配置文件中的k/v保存到运行的环境变量中，加载完外部的配置文件以后使用${}取出配置文件的值。修改MainConfigOfPropertyValues.java，加入如下注解：
```java
@PropertySource(value={"classpath:/person.properties"})
```
所以我们还需要在resources目录下创建一个person.properties文件，该文件内容如下：

	person.nickName=jackster

只有一行键值对。修改Person.java代码：
```java
    @Value("${person.nickName}")
    private String nickName;
```
这里通过${}表达式获取键值对里的值，现在看看输出结果如何：

	Person [name=jack, age=20, nickName=jackster]

成功读取配置里的键值对。


