## Spring注解开发笔记--组件注册

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

### 3、@Scope设置组件作用域

还是前面的工程，在工程的config目录下新建一个MainConfig2.java文件，里面写入如下内容：

	@Configuration
	public class MainConfig2 {
	
	    @Bean("person")
	    public Person person01(){
	        return new Person("zhangsan", 26);
	    }
	}

这里也是创建了一个配置类，而且生成Person对象到容器中。

然后在IOCTest.java文件里补充测试方法：

    @Test
    public void test02(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : definitionNames) {
            System.out.println(name);
        }
    }

打印MainConfig2配置中存在的对象，这里输出结果如下：

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalRequiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	mainConfig2
	person

接下来修改test02测试方法：

    @Test
    public void test02(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : definitionNames) {
            System.out.println(name);
        }

        Object bean = applicationContext.getBean("person");
        Object bean2 = applicationContext.getBean("person");
        System.out.println(bean == bean2);
    }

这里通过Bean的ID来获取实例对象bean和bean1，然后判断他们是否是同一个对象，最后输出如下：

	...
	true

说明上面在配置中只使用@Bean注解的对象在容器中只有一个，即单例对象。Spring中可以使用@Scope来设置对象创建的作用域，有以下几张类型：

- ConfigurableBeanFactory#SCOPE_PROTOTYPE   prototype   
- @see ConfigurableBeanFactory#SCOPE_SINGLETON    singleton
- @see org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST  request
- @see org.springframework.web.context.WebApplicationContext#SCOPE_SESSION	 sesssion

其中：

- prototype：多实例的：ioc容器启动并不会去调用方法创建对象放在容器中。每次获取的时候才会调用方法创建对象；
- singleton：单实例的（默认值）：ioc容器启动会调用方法创建对象放到ioc容器中。以后每次获取就是直接从容器（map.get()）中拿
- request：同一次请求创建一个实例
- session：同一个session创建一个实例

首先测试下prototype，修改MainConfig2中的person01方法，加上注解@Scope("prototype")：

    @Scope("prototype")
    @Bean("person")
    public Person person01(){
        return new Person("zhangsan", 26);
    }

测试代码运行结果如下：

	...
	false

### 4、@Lazy-bean懒加载

默认情况下ioc容器启动会调用方法创建对象放到ioc容器中。以后每次获取就是直接从容器（map.get()）中拿，设计模式里面称为单例模式的“饿汉式”。如果在创建实例上添加@Lazy标签，那么就变成“懒汉式”了：

    @Lazy
    @Bean("person")
    public Person person01(){
        return new Person("zhangsan", 26);
    }

容器启动不创建对象。第一次使用才(获取)Bean创建对象，并初始化。

### 5、@Conditional-按照条件注册bean

为了方便演示，这里在MainConfig2中添加两个Bean：

	@Bean("bill")
	public Person person01(){
		return new Person("Bill Gates",62);
	}
	
	@Bean("linus")
	public Person person02(){
		return new Person("linus", 48);
	}

分别取名叫"bill"和"linus"。现在需求是，如果系统是windows，给容器中注册("bill")，如果是linux系统，给容器中注册("linus")。

为了实现该需求，我们需要使用@Conditional标签来实现条件注册，@Conditional需要设置条件类，该条件类需要实现Condition接口并覆写public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata)方法。

首先在com.atguigu下创建condition包，在里面创建两个类WindowsCondition和LinuxCondition。

WindowsCondition.java内容如下：

	public class WindowsCondition implements Condition {
	
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			Environment environment = context.getEnvironment();
			String property = environment.getProperty("os.name");
			if(property.contains("Windows")){
				return true;
			}
			return false;
		}
	
	}

这里根据context上下文获取到系统的名字，如果是Windows系统则返回true，否则返回false。

接下来创建一个LinuxCondition：

	//判断是否linux系统
	public class LinuxCondition implements Condition {
	
		/**
		 * ConditionContext：判断条件能使用的上下文（环境）
		 * AnnotatedTypeMetadata：注释信息
		 */
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			// TODO是否linux系统
			//1、能获取到ioc使用的beanfactory
			ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
			//2、获取类加载器
			ClassLoader classLoader = context.getClassLoader();
			//3、获取当前环境信息
			Environment environment = context.getEnvironment();
			//4、获取到bean定义的注册类
			BeanDefinitionRegistry registry = context.getRegistry();
			
			String property = environment.getProperty("os.name");
			
			//可以判断容器中的bean注册情况，也可以给容器中注册bean
			boolean definition = registry.containsBeanDefinition("person");
			if(property.contains("linux")){
				return true;
			}
			
			return false;
		}
	
	}

这里只在系统是Linux的情况下返回true。

修改MainConfig2.java内容：

	...
	@Conditional({WindowsCondition.class})
	@Bean("bill")
	...
    @Conditional(LinuxCondition.class)
    @Bean("linus")
	...

然后在IOCTest.java里添加

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);

    @Test
    public void test03(){
        Map<String, Person> persons = applicationContext.getBeansOfType(Person.class);
        System.out.println(persons);
    }

打印结果如下:

	{person=Person [name=zhangsan, age=26, nickName=null], bill=Person [name=Bill Gates, age=62, nickName=null]}

当然也可以在配置类上增加这个标签：

	@Conditional({WindowsCondition.class})
	@Configuration
	public class MainConfig2 {}

效果是一样的。

### 6、@Import-给容器中快速导入一个组件

在bean目录下创建两个类Color和Red。

Color.java内容如下：

	package com.atguigu.bean;
	
	public class Color {
	}

Red.java内容如下：

	package com.atguigu.bean;
	
	public class Color {
	}

然后再MainConfig2.java中加入注解：

	@Import({Color.class, Red.class})
	public class MainConfig2 {}

通过@Import倒入一个数组，这里导入了Color和Red两个类，然后再IOCTest.java中编写如下测试程序：

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig2.class);

    @Test
    public void testImport(){
        printBeans(applicationContext);
    }

    private void printBeans(AnnotationConfigApplicationContext applicationContext){
        String[] definitionNames = applicationContext.getBeanDefinitionNames();
        for(String name : definitionNames){
            System.out.println(name);
        }
    }

打印输出结果包含：

	...
	com.atguigu.bean.Color
	com.atguigu.bean.Red
	...

这样便向IOC容器中导入了两个类。

当然，也可以通过ImportSelector的方式来将类导入到容器中。

接下来继续向bean目录中添加Blue和Yellow两个类。

Blue.java内容如下：

	package com.atguigu.bean;
	
	public class Blue {
	}

Yellow.java内容如下：

	package com.atguigu.bean;
	
	public class Yellow {
	}

如果要使用ImportSelector，还需要一个实现了ImportSelector接口的类，在condition目录里添加MyImportSelector类：

	package com.atguigu.condition;
	
	import org.springframework.context.annotation.ImportSelector;
	import org.springframework.core.type.AnnotationMetadata;
	
	//自定义逻辑返回需要导入的组件
	public class MyImportSelector implements ImportSelector {
	
		//返回值，就是到导入到容器中的组件全类名
		//AnnotationMetadata:当前标注@Import注解的类的所有注解信息
		@Override
		public String[] selectImports(AnnotationMetadata importingClassMetadata) {
			// TODO Auto-generated method stub
			//importingClassMetadata
			//方法不要返回null值
			return new String[]{"com.atguigu.bean.Blue","com.atguigu.bean.Yellow"};
		}
	
	}

该类需要实现ImportSelector的public String[] selectImports(AnnotationMetadata importingClassMetadata)方法，然后在该方法中返回需要导入到IOC容器中的类的全类名。

接下来修改MainConfig2.java中的@Import注解：

	@Import({Color.class, Red.class, MyImportSelector.class})

将我们编写的MyImportSelector添加到注解里，然后修改测试函数testImport()，内容如下：

    @Test
    public void testImport(){
        printBeans(applicationContext);

        Blue bean = applicationContext.getBean(Blue.class);
        System.out.println(bean);
    }

输出结果中包含：

	...
	com.atguigu.bean.Blue
	com.atguigu.bean.Yellow
	...
	com.atguigu.bean.Blue@69930714

可见，Blue和Yellow两个类已经导入到IOC容器中了，用Import方式导入的Bean的Id默认是全类名。

对于@Import，这里最后介绍使用MyImportBeanDefinitionRegistrar导入组件的方法。

首先我们需要添加一个类RainBow：

	package com.atguigu.bean;
	
	public class RainBow {
	}

然后编写一个实现了ImportBeanDefinitionRegistrar接口的类MyImportBeanDefinitionRegistrar，并覆写public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)方法：

	package com.atguigu.condition;
	
	import org.springframework.beans.factory.support.BeanDefinitionRegistry;
	import org.springframework.beans.factory.support.RootBeanDefinition;
	import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
	import org.springframework.core.type.AnnotationMetadata;
	
	import com.atguigu.bean.RainBow;
	
	public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	
		/**
		 * AnnotationMetadata：当前类的注解信息
		 * BeanDefinitionRegistry:BeanDefinition注册类；
		 * 		把所有需要添加到容器中的bean；调用
		 * 		BeanDefinitionRegistry.registerBeanDefinition手工注册进来
		 */
		@Override
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
			
			boolean definition = registry.containsBeanDefinition("com.atguigu.bean.Red");
			boolean definition2 = registry.containsBeanDefinition("com.atguigu.bean.Blue");
			if(definition && definition2){
				//指定Bean定义信息；（Bean的类型，Bean。。。）
				RootBeanDefinition beanDefinition = new RootBeanDefinition(RainBow.class);
				//注册一个Bean，指定bean名
				registry.registerBeanDefinition("rainBow", beanDefinition);
			}
		}
	
	}

在registerBeanDefinitions方法的参数里有BeanDefinitionRegistry对象，首先调用了该对象的BeanDefinitionRegistry()方法，通过传入某个类的全类名来判断该类是否存在于IOC容器中，这里判断Red和Blue两个类，如果两个类都存在与IOC容器中，则通过：

	RootBeanDefinition beanDefinition = new RootBeanDefinition(RainBow.class);
	registry.registerBeanDefinition("rainBow", beanDefinition);

将我们前面创建的RainBow类导入到IOC容器中。

修改MainConfig2.java中的@Import标签内容如下：

	@Import({Color.class, Red.class, MyImportSelector.class,MyImportBeanDefinitionRegistrar.class})

然后通过IOCTest.java中的testImport()方法来测试RainBow类是否注册到IOC容器中，最后测试结果包含：

	...
	rainBow
	...

说明导入成功。

### 7、使用FactoryBean注册组件

下面将对于Color类来使用工厂方法注册组件。这里创建了一个类ColorFactoryBean，该类实现了FactoryBean<Color>接口：

	package com.atguigu.bean;
	
	import org.springframework.beans.factory.FactoryBean;
	
	//创建一个Spring定义的FactoryBean
	public class ColorFactoryBean implements FactoryBean<Color> {
	
		//返回一个Color对象，这个对象会添加到容器中
		@Override
		public Color getObject() throws Exception {
			// TODO Auto-generated method stub
			System.out.println("ColorFactoryBean...getObject...");
			return new Color();
		}
	
		@Override
		public Class<?> getObjectType() {
			// TODO Auto-generated method stub
			return Color.class;
		}
	
		//是单例？
		//true：这个bean是单实例，在容器中保存一份
		//false：多实例，每次获取都会创建一个新的bean；
		@Override
		public boolean isSingleton() {
			// TODO Auto-generated method stub
			return false;
		}
	
	}

然后在MainConfig2.java中添加如下代码：

    @Bean
    public ColorFactoryBean colorFactoryBean(){
        return new ColorFactoryBean();
    }

最后修改IOCTest.java中的testImport()方法：

    @Test
    public void testImport(){
        printBeans(applicationContext);

        Blue bean = applicationContext.getBean(Blue.class);
        System.out.println(bean);

        //工厂Bean获取的是调用getObject创建的对象
        Object bean2 = applicationContext.getBean("colorFactoryBean");
        Object bean3 = applicationContext.getBean("colorFactoryBean");
        System.out.println("bean的类型："+bean2.getClass());
        System.out.println(bean2 == bean3);

        Object bean4 = applicationContext.getBean("&colorFactoryBean");
        System.out.println(bean4.getClass());
    }

测试的输出如下：

	ColorFactoryBean...getObject...
	ColorFactoryBean...getObject...
	the type of bean is:class com.atguigu.bean.Color
	false
	class com.atguigu.bean.ColorFactoryBean

由于我们设置了：

	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

返回值是false，所以工厂返回的Bean不是单例，每次获取该FactoryBean时都会实例化一个Color对象。

### 8、总结

给Spring容器中注册组方法：

- 1）、包扫描+组件标注注解（@Controller/@Service/@Repository/@Component）[自己写的类]
- 2）、@Bean[导入的第三方包里面的组件]
- 3）、@Import[快速给容器中导入一个组件]

		a）、@Import(要导入到容器中的组件)；容器中就会自动注册这个组件，id默认是全类名
		b）、ImportSelector:返回需要导入的组件的全类名数组；
		c）、ImportBeanDefinitionRegistrar:手动注册bean到容器中

- 4）、使用Spring提供的 FactoryBean（工厂Bean）;

		a）、默认获取到的是工厂bean调用getObject创建的对象
		b）、要获取工厂Bean本身，我们需要给id前面加一个&
			&colorFactoryBean


