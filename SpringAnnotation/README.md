## Spring注解开发笔记

Spring可以使用XML配置的方法向IOC容器中注入bean，但是这种方法有些繁琐和不便，于是便有了使用注解驱动开发的方式，在Spring Boot和Spring Cloud中也是推荐使用注解开发。

#### 首先看一个例子，这个例子使用传统的xml配置方式开发。

首先创建一个Person类：

	package com.atguigu.bean;
	
	
	public class Person {

		private String name;
		private Integer age;
		private String nickName;
	
		public String getNickName() {
			return nickName;
		}
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
	
		public Person(String name, Integer age) {
			super();
			this.name = name;
			this.age = age;
		}
		public Person() {
			super();
			// TODO Auto-generated constructor stub
		}
		@Override
		public String toString() {
			return "Person [name=" + name + ", age=" + age + ", nickName=" + nickName + "]";
		}
		
	}

根据要求，还需要创建一个配置文件，这里叫做beans.xml。配置文件内容如下：

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:context="http://www.springframework.org/schema/context"
		xmlns:aop="http://www.springframework.org/schema/aop"
		xmlns:tx="http://www.springframework.org/schema/tx"
		xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
			http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.3.xsd
			http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.3.xsd
			http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.3.xsd">
	
		<bean id="person" class="com.atguigu.bean.Person"  scope="prototype" >
			<property name="age" value="18"></property>
			<property name="name" value="zhangsan"></property>
		</bean>
	
	</beans>

然后编写测试类MainTest，测试类内容如下：

	package com.atguigu;
	
	import org.springframework.context.ApplicationContext;
	import org.springframework.context.support.ClassPathXmlApplicationContext;
	
	import com.atguigu.bean.Person;
	
	public class MainTest {
		
		@SuppressWarnings("resource")
		public static void main(String[] args) {
			ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
			Person bean = (Person) applicationContext.getBean("person");
			System.out.println(bean);
	
		}
	
	}

测试结果如下：

	Person [name=zhangsan, age=18, nickName=null]

#### 接下来是使用注解驱动开发

同样的Person类，使用xml开发方式需要在xml中配置该实例的属性，但使用注解开发推荐使用创建配置类的方式。

首先在主包下建立一个子包config，里面放置我们的配置类，然后在包里面创建MainConfig类，其代码如下所示：

	package com.atguigu.config;
	
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;
	import com.atguigu.bean.Person;
	
	//配置类==配置文件
	@Configuration  //告诉Spring这是一个配置类
	public class MainConfig {
		
		//给容器中注册一个Bean;类型为返回值的类型，id默认是用方法名作为id
		@Bean("person")
		public Person person01(){
			return new Person("lisi", 20);
		}
	
	}

这里需要使用@Configuration标注该类，告诉Spring这是一个注解类，等同于一个配置XML文件。使用@Bean表明生成一个IOC对象Person，使用@Bean("person")标注该类的id，否则该类的id是标注的函数名，这里是person01。

编写测试类：

	package com.atguigu;
	
	import org.springframework.context.ApplicationContext;
	import org.springframework.context.annotation.AnnotationConfigApplicationContext;
	
	import com.atguigu.bean.Person;
	import com.atguigu.config.MainConfig;
	
	public class MainTest {
		
		@SuppressWarnings("resource")
		public static void main(String[] args) {
	
			ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
			Person bean = applicationContext.getBean(Person.class);
			System.out.println(bean);
	
			String[] namesForType = applicationContext.getBeanNamesForType(Person.class);
			for (String name : namesForType) {
				System.out.println(name);
			}
		
		}
	
	}

输出如下：

	Person [name=lisi, age=20, nickName=null]
	person