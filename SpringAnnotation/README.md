## Spring注解开发笔记

Spring可以使用XML配置的方法向IOC容器中注入bean，但是这种方法有些繁琐和不便，于是便有了使用注解驱动开发的方式，在Spring Boot和Spring Cloud中也是推荐使用注解开发。

### 1、注解开发

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

### 2、@ComponentScan自动扫描组件

在传统的xml配置方法中，如果要扫描一个包下面的所有Bean，需要做如下配置：

	<!-- 包扫描、只要标注了@Controller、@Service、@Repository，@Component -->
	<context:component-scan base-package="com.atguigu" use-default-filters="false"></context:component-scan>

通过这个方法来指定扫描包，然后只要是有@Controller、@Service、@Repository，@Component注解的类都会被IOC容器生成对象。

现在在前面的MainConfig.java文件中加入@ComponentScans(value="com.atguigu")注解，现在配置类的文件内容如下：

	@Configuration
	@ComponentScans(value="com.atguigu")
	public class MainConfig {
		
		//给容器中注册一个Bean;类型为返回值的类型，id默认是用方法名作为id
		@Bean("person")
		public Person person01(){
			return new Person("lisi", 20);
		}

}

这样就取代了之前的xml配置方法。

接下来创建三个类：

BookController.java:

	package com.atguigu.controller;
	
	import org.springframework.stereotype.Controller;
	
	import com.atguigu.service.BookService;
	
	@Controller
	public class BookController {
	
	}

BookService.java:

	package com.atguigu.service;
	
	import org.springframework.stereotype.Service;
	
	@Service
	public class BookService {
		
	}

BookDao.java:

	package com.atguigu.dao;
	
	import org.springframework.stereotype.Repository;
	
	@Repository
	public class BookDao {
	
	}

需要注意的是这三个类上的注解，有了这些注解，Spring才会给这几个类创建实例。

接下来编写一个测试类IOCTest.java，加入如下内容：

	package com.atguigu.test;
	
	import org.junit.Test;
	import org.springframework.context.annotation.AnnotationConfigApplicationContext;
	import com.atguigu.config.MainConfig;
	
	public class IOCTest {
		@SuppressWarnings("resource")
		@Test
		public void test01(){
			AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
			String[] definitionNames = applicationContext.getBeanDefinitionNames();
			for (String name : definitionNames) {
				System.out.println(name);
			}
		}
	}

该测试方法的作用是打印MainConfig.java中扫描包里的被Spring注册到IOC容器中的对象的类名，测试结果如下：

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalRequiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	mainConfig
	bookController
	bookDao
	bookService
	person

目录结构如下所示：

![](https://i.niupic.com/images/2019/09/16/_1287.png)

当然也可以设置容器对象的过滤，修改MainConfig.java代码如下：

	//配置类==配置文件
	@Configuration  //告诉Spring这是一个配置类
	@ComponentScan(value="com.atguigu", excludeFilters = {
	        @Filter(type=FilterType.ANNOTATION,classes={Controller.class, Service.class})
	})
	
	public class MainConfig {
	
	    //给容器中注册一个Bean;类型为返回值的类型，id默认是用方法名作为id
	    @Bean("person")
	    public Person person01(){
	        return new Person("lisi", 20);
	    }
	
	}

这里使用了

	excludeFilters = {@ComponentScan.Filter(type=FilterType.ANNOTATION,classes={Controller.class, Service.class}}

其中type=FilterType.ANNOTATION是过滤类型，这里是根据注解过滤。然后是classes={Controller.class, Service.class}，这里过滤带有@Controller和@Service的对象。

最后测试的结果如下：

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalRequiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	mainConfig
	bookDao
	person

当然，这里也可以指定某些类，而其他类都过滤掉，修改@ComponentScan里面的内容如下：

	@ComponentScan(value="com.atguigu", includeFilters = {
	        @ComponentScan.Filter(type=FilterType.ANNOTATION,classes={Controller.class, Service.class})
	},useDefaultFilters = false)

然后运行结果如下：

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalRequiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	mainConfig
	bookController
	bookService
	person

注意设置useDefaultFilters = false。

当然，也可以通过@ComponentScans来重复指定扫描组件策略：

	@ComponentScans(
	        value = {
	                @ComponentScan(value="com.atguigu",includeFilters = {
						@ComponentScan.Filter(type=FilterType.ANNOTATION,classes={Controller.class}),
	                },useDefaultFilters = false)
	        }
	)

打印输出的效果如下所示：

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalRequiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	mainConfig
	bookController
	person

当然也可以用type=FilterType.ASSIGNABLE_TYPE来指定类型：

	@ComponentScans(
	        value = {
	                @ComponentScan(value="com.atguigu",includeFilters = {
						@ComponentScan.Filter(type=FilterType.ANNOTATION,classes={Controller.class}),
	                     @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE,classes={BookService.class})
	                },useDefaultFilters = false)
	        }
	)

打印输出效果如下:

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalRequiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	mainConfig
	bookController
	bookService
	person

最后介绍自定义Filter，Spring支持使用FilterType.CUSTOM自定义过滤规则。

首先要新建一个实现了TypeFilter接口的实现类，这里是MyTypeFilter.java：

	package com.atguigu.config;
	
	import java.io.IOException;
	
	import org.springframework.core.io.Resource;
	import org.springframework.core.type.AnnotationMetadata;
	import org.springframework.core.type.ClassMetadata;
	import org.springframework.core.type.classreading.MetadataReader;
	import org.springframework.core.type.classreading.MetadataReaderFactory;
	import org.springframework.core.type.filter.TypeFilter;

	public class MyTypeFilter implements TypeFilter {
	    @Override
	    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
	            // TODO Auto-generated method stub
	            //获取当前类注解的信息
	            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
	            //获取当前正在扫描的类的类信息
	            ClassMetadata classMetadata = metadataReader.getClassMetadata();
	            //获取当前类资源（类的路径）
	            Resource resource = metadataReader.getResource();
	
	            String className = classMetadata.getClassName();
	            System.out.println("--->"+className);
	            if(className.contains("er")){
	                return true;
	            }
	            return false;
	    }
	}

这里设置的规则是扫描类名中存在"er"的类。

然后修改MainConfig.java里的扫描规则：

	@ComponentScans(
	        value = {
	                @ComponentScan(value="com.atguigu",includeFilters = {
	                        @ComponentScan.Filter(type=FilterType.CUSTOM,classes={MyTypeFilter.class})
	                },useDefaultFilters = false)
	        }
	)

最后效果是：

	--->com.atguigu.test.IOCTest
	--->com.atguigu.bean.Person
	--->com.atguigu.config.MyTypeFilter
	--->com.atguigu.controller.BookController
	--->com.atguigu.dao.BookDao
	--->com.atguigu.MainTest
	--->com.atguigu.service.BookService

	...
	
	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalRequiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	mainConfig
	person
	myTypeFilter
	bookController
	bookService


最后总结过滤规则：

	@ComponentScan  value:指定要扫描的包
		excludeFilters = Filter[] ：指定扫描的时候按照什么规则排除那些组件
		includeFilters = Filter[] ：指定扫描的时候只需要包含哪些组件
		FilterType.ANNOTATION：按照注解
		FilterType.ASSIGNABLE_TYPE：按照给定的类型；
		FilterType.ASPECTJ：使用ASPECTJ表达式
		FilterType.REGEX：使用正则指定
		FilterType.CUSTOM：使用自定义规则
### 3、
