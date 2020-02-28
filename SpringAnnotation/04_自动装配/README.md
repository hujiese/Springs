<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Spring注解开发笔记--自动装配](#spring%E6%B3%A8%E8%A7%A3%E5%BC%80%E5%8F%91%E7%AC%94%E8%AE%B0--%E8%87%AA%E5%8A%A8%E8%A3%85%E9%85%8D)
  - [1、@Autowired、@Qualifier和@Primary](#1autowiredqualifier%E5%92%8Cprimary)
  - [2、@Resource和@Inject](#2resource%E5%92%8Cinject)
  - [3、@Autowire--方法、构造器位置的自动装配](#3autowire--%E6%96%B9%E6%B3%95%E6%9E%84%E9%80%A0%E5%99%A8%E4%BD%8D%E7%BD%AE%E7%9A%84%E8%87%AA%E5%8A%A8%E8%A3%85%E9%85%8D)
  - [4、@Profile](#4profile)
  - [5、自动装配总结](#5%E8%87%AA%E5%8A%A8%E8%A3%85%E9%85%8D%E6%80%BB%E7%BB%93)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Spring注解开发笔记--自动装配

### 1、@Autowired、@Qualifier和@Primary

修改工程中的BookDao.java如下：
```java
	package com.atguigu.dao;
	
	import org.springframework.stereotype.Repository;
	
	//名字默认是类名首字母小写
	@Repository
	public class BookDao {
		
		private String lable = "1";
	
		public String getLable() {
			return lable;
		}
	
		public void setLable(String lable) {
			this.lable = lable;
		}
	
		@Override
		public String toString() {
			return "BookDao [lable=" + lable + "]";
		}
	}
```
修改BookService.java如下：
```java
package com.atguigu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.dao.BookDao;

@Service
public class BookService {

	@Autowired
	private BookDao bookDao;

	public void print(){
		System.out.println(bookDao);
	}

	@Override
	public String toString() {
		return "BookService [bookDao=" + bookDao + "]";
	}
}
```
修改BookController.java如下：
```java
package com.atguigu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.atguigu.service.BookService;

@Controller
public class BookController {
	
	@Autowired
	private BookService bookService;

}
```
创建一个配置类MainConifgOfAutowired，内容如下：
```java
package com.atguigu.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.atguigu.service","com.atguigu.dao", "com.atguigu.controller","com.atguigu.bean"})
public class MainConifgOfAutowired {
}
```
创建测试类IOCTest_Autowired.java：
```java
package com.atguigu.test;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.atguigu.config.MainConifgOfAutowired;
import com.atguigu.service.BookService;

public class IOCTest_Autowired {

	@Test
	public void test01(){
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConifgOfAutowired.class);

		BookService bookService = applicationContext.getBean(BookService.class);
		System.out.println(bookService);
	}

}
```
打印结果如下：

	BookService [bookDao=BookDao [lable=1]]

BookService的toString()方法里会打印自动装配的BookDao的lable值。如果成功打印该值，说明BookDao在BookService创建后就被自动装配了。

修改MainConifgOfAutowired.java，加入：
```java
@Bean("bookDao2")
public BookDao bookDao(){
	BookDao bookDao = new BookDao();
	bookDao.setLable("2");
	return bookDao;
}
```
这样一来，整个容器中就存在两个BookDao了。测试结果如下：

	BookService [bookDao=BookDao [lable=1]]

这里还是使用了默认的BookDao实例，没有使用配置类里的。

修改BookService中的bookDao为bookDao2，测试结果如下：

	BookService [bookDao=BookDao [lable=2]]

@Autowired默认优先按照类型去容器中找对应的组件:applicationContext.getBean(BookDao.class);找到就赋值。如果找到多个相同类型的组件，再将属性的名称作为组件的id去容器中查找applicationContext.getBean("bookDao")

还是上面那个例子，现在BookService里面装配的是bookDao2，如果还是装配bookDao1，那么就要修改代码：
```java
@Qualifier("bookDao")
@Autowired
private BookDao bookDao2;
```
通过@Qualifier("bookDao")标签来明确告诉需要装配的对象，测试结果如下：

	BookService [bookDao=BookDao [lable=1]]


接下来再做这个试验，修改BookService代码：
```java
@Qualifier("bookDao2")
@Autowired
private BookDao bookDao2;
```
明确指出装配对象是bookDao2。但是这里却移除了MainConifgOfAutowired中的bookDao2：
```java
//    @Bean("bookDao2")
public BookDao bookDao(){
	BookDao bookDao = new BookDao();
	bookDao.setLable("2");
	return bookDao;
}
```
结果报错：

	org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'bookService': Unsatisfied dependency expressed through field 'bookDao2'; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.atguigu.dao.BookDao' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Qualifier(value=bookDao2), @org.springframework.beans.factory.annotation.Autowired(required=true)}

也就是说，只要我们使用了自动装配功能，那么在环境中一定要找到能装配的实例对象，否则就会报错。

Spring也提供了@Autowired(required=false)方法来解决该问题，如果找到对象，则注入，对象为空。

	BookService [bookDao=null]

当然Spring也提供了@Primary来设置注入对象的优先级：
```java
@Primary
@Bean("bookDao2")
public BookDao bookDao(){
	BookDao bookDao = new BookDao();
	bookDao.setLable("2");
	return bookDao;
}
```
但前提是注释掉@Qualifier：
```java
//    @Qualifier("bookDao")
@Autowired(required=false)
private BookDao bookDao;
```
输出结果如下：

	BookService [bookDao=BookDao [lable=2]]

### 2、@Resource和@Inject

Spring中提供了@Resource和@Inject来完成类似于@Autowire的自动注入功能。

修改BookService.java代码：
```java
@Resource()
private BookDao bookDao;
```
然后测试结果如下：

	BookService [bookDao=BookDao [lable=1]]

但是我们的配置类中却还保存了:
```java
@Primary
@Bean("bookDao2")
```
@Resource可以和@Autowired一样实现自动装配功能，但默认是按照组件名称进行装配的，没有能支持@Primary功能没有支持@Autowired（reqiured=false），但可以通过指定名字来指定注入哪个Bean：
```java
@Resource(name="bookDao2")
```
输出结果如下：

	BookService [bookDao=BookDao [lable=2]]

@Inject提供了@Autowire类似的功能，但在使用前需要导入依赖：
```xml
<dependency>
	<groupId>javax.inject</groupId>
	<artifactId>javax.inject</artifactId>
	<version>1</version>
</dependency>
```
然后修改BookService.java内容：
```java
@Inject
private BookDao bookDao;
```
测试输出结果如下;

	BookService [bookDao=BookDao [lable=2]]

@Inject虽然功能上看起来和@Autowire一样，但没有required=false的功能。

此外，需要注意的是，@Autowired:Spring定义的； @Resource、@Inject都是java规范。

### 3、@Autowire--方法、构造器位置的自动装配

还是使用之前的工程，在Car类上加入@Component，让Car类可以注册到IOC容器中。然后编写Boss类;
```java
package com.atguigu.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Boss {

	@Autowired
	private Car car;

	public Boss(Car car){
		this.car = car;
		System.out.println("Boss...有参构造器");
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	@Override
	public String toString() {
		return "Boss [car=" + car + "]";
	}
}
```
这里再私有成员对象里使用了@Autowire注解;
```java
@Autowired
private Car car;
```
然后修改IOCTest_Autowired.java里的测试函数test01()：
```java
AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConifgOfAutowired.class);

Boss boss = applicationContext.getBean(Boss.class);
System.out.println(boss);
Car car = applicationContext.getBean(Car.class);
System.out.println(car);
```
在该测试函数中从IOC容器中获取了一个Boss对象和一个Car对象，最后打印出来：

	Boss [car=com.atguigu.bean.Car@7fc229ab]
	com.atguigu.bean.Car@7fc229ab

从打印结果看，Boss里自动装配的Car对象就是容器中的Car对象。

当然，除了可以在变量上加入@Autowired注解外，还可以set赋值方法上加入该注解，修改Boss.java：
```java
@Autowired
public void setCar(Car car) {
	this.car = car;
}
```
最后效果如下;

	Boss [car=com.atguigu.bean.Car@527e5409]
	com.atguigu.bean.Car@527e5409

甚至可以这样：
```java
public void setCar(@Autowired Car car) {
	this.car = car;
}
```
最后的效果是一样的。

@Autowire也支持在构造器上添加：
```java
@Autowired
public Boss(Car car){
	this.car = car;
	System.out.println("Boss...有参构造器");
}
```
如果组件只有一个有参构造器，这个有参构造器的@Autowired可以省略，参数位置的组件还是可以自动从容器中获取：
```java
public Boss(Car car){
	this.car = car;
	System.out.println("Boss...有参构造器");
}
```
效果如下：

	Boss [car=com.atguigu.bean.Car@7fc229ab]
	com.atguigu.bean.Car@7fc229ab

### 4、@Profile

Spring为我们提供的可以根据当前环境，动态的激活和切换一系列组件的功能，这需要依赖@Profile。

现在假设我们的项目有三个环境，开发环境、测试环境、生产环境，也有三个数据源(/A)(/B)(/C)。现在我们要根据不同的环境动态切换数据源。通过这个例子来展示@Profile的使用。

为了方便测试，这里使用了Mysql数据库和c3p0数据源，所以项目需要添加meavn依赖：
```xml
<!-- https://mvnrepository.com/artifact/c3p0/c3p0 -->
<dependency>
	<groupId>c3p0</groupId>
	<artifactId>c3p0</artifactId>
	<version>0.9.1.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
	<version>5.1.44</version>
</dependency>
```
然后在resources下创建dbconfig.properties文件，里面是我们数据库连接的一些配置：

	db.user=root
	db.password=123
	db.driverClass=com.mysql.jdbc.Driver

然后编写MainConfigOfProfile配置类，该类的内容如下:
```java
package com.atguigu.config;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringValueResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
	* Profile：
	* 		Spring为我们提供的可以根据当前环境，动态的激活和切换一系列组件的功能；
	*
	* 开发环境、测试环境、生产环境；
	* 数据源：(/A)(/B)(/C)；
	*
	*
	* @Profile：指定组件在哪个环境的情况下才能被注册到容器中，不指定，任何环境下都能注册这个组件
	*
	* 1）、加了环境标识的bean，只有这个环境被激活的时候才能注册到容器中。默认是default环境
	* 2）、写在配置类上，只有是指定的环境的时候，整个配置类里面的所有配置才能开始生效
	* 3）、没有标注环境标识的bean在，任何环境下都是加载的；
	*/

@PropertySource("classpath:/dbconfig.properties")
@Configuration
public class MainConfigOfProfile implements EmbeddedValueResolverAware{

	@Value("${db.user}")
	private String user;

	private StringValueResolver valueResolver;

	private String  driverClass;

	@Bean("testDataSource")
	public DataSource dataSourceTest(@Value("${db.password}")String pwd) throws Exception{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser(user);
		dataSource.setPassword(pwd);
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setDriverClass(driverClass);
		return dataSource;
	}

	@Bean("devDataSource")
	public DataSource dataSourceDev(@Value("${db.password}")String pwd) throws Exception{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser(user);
		dataSource.setPassword(pwd);
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/example");
		dataSource.setDriverClass(driverClass);
		return dataSource;
	}

	@Bean("prodDataSource")
	public DataSource dataSourceProd(@Value("${db.password}")String pwd) throws Exception{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser(user);
		dataSource.setPassword(pwd);
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/forta");

		dataSource.setDriverClass(driverClass);
		return dataSource;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		// TODO Auto-generated method stub
		this.valueResolver = resolver;
		driverClass = valueResolver.resolveStringValue("${db.driverClass}");
	}

}
```
然后编写测试类：
```java
package com.atguigu.test;

import javax.sql.DataSource;

import com.atguigu.config.MainConfigOfProfile;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Profile {
	@Test
	public void test01(){
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfProfile.class);

		String[] namesForType = applicationContext.getBeanNamesForType(DataSource.class);
		for (String string : namesForType) {
			System.out.println(string);
		}
	}

}
```
打印出：

	testDataSource
	devDataSource
	prodDataSource

目前环境下有三种数据源，但我们实际情况只存在一种环境和一种数据源。现在假设我们的环境是dev环境，如何做到切换数据源呢？

首先修改MainConfigOfProfile类：
```java
...
@Profile("test")
@Bean("testDataSource")
...
@Profile("dev")
@Bean("devDataSource")
...
@Profile("prod")
@Bean("prodDataSource")
...
```java
这里使用了@Profile来标识不同环境下的数据源。

最后修改IOCTest_Profile.java的测试方法：

```java
@Test
public void test01(){
	AnnotationConfigApplicationContext applicationContext =
			new AnnotationConfigApplicationContext();
	//1、创建一个applicationContext
	//2、设置需要激活的环境
	applicationContext.getEnvironment().setActiveProfiles("dev");
	//3、注册主配置类
	applicationContext.register(MainConfigOfProfile.class);
	//4、启动刷新容器
	applicationContext.refresh();


	String[] namesForType = applicationContext.getBeanNamesForType(DataSource.class);
	for (String string : namesForType) {
		System.out.println(string);
	}
}
```
在测试方法中我们选择了当前环境是dev环境，打印结果如下;

	devDataSource

这样就可以根据不同的环境切换对应的数据源了。

除此之外，也可以使用命令行动态参数，在虚拟机参数位置加载 

	-Dspring.profiles.active=test

### 5、自动装配总结

自动装配：Spring利用依赖注入（DI），完成对IOC容器中中各个组件的依赖关系赋值；

- 1）、@Autowired：自动注入：

		1）、默认优先按照类型去容器中找对应的组件:applicationContext.getBean(BookDao.class);找到就赋值
		2）、如果找到多个相同类型的组件，再将属性的名称作为组件的id去容器中查找
							applicationContext.getBean("bookDao")
		3）、@Qualifier("bookDao")：使用@Qualifier指定需要装配的组件的id，而不是使用属性名
		4）、自动装配默认一定要将属性赋值好，没有就会报错；
			可以使用@Autowired(required=false);
		5）、@Primary：让Spring进行自动装配的时候，默认使用首选的bean；
				也可以继续使用@Qualifier指定需要装配的bean的名字
		BookService{
			@Autowired
			BookDao  bookDao;
		}


- 2）、Spring还支持使用@Resource(JSR250)和@Inject(JSR330)[java规范的注解]：
	
		@Resource:
			可以和@Autowired一样实现自动装配功能；默认是按照组件名称进行装配的；
			没有能支持@Primary功能没有支持@Autowired（reqiured=false）;
		@Inject:
			需要导入javax.inject的包，和Autowired的功能一样。没有required=false的功能；
	    @Autowired:Spring定义的； @Resource、@Inject都是java规范
	
	    AutowiredAnnotationBeanPostProcessor:解析完成自动装配功能；		


- 3）、 @Autowired:构造器，参数，方法，属性都是从容器中获取参数组件的值：

		1）、[标注在方法位置]：@Bean+方法参数；参数从容器中获取;默认不写@Autowired效果是一样的；都能自动装配
		2）、[标在构造器上]：如果组件只有一个有参构造器，这个有参构造器的@Autowired可以省略，参数位置的组件还是可以自动从容器中获取
		3）、放在参数位置：

- 4）、自定义组件想要使用Spring容器底层的一些组件（ApplicationContext，BeanFactory，xxx）：

		自定义组件实现xxxAware；在创建对象的时候，会调用接口规定的方法注入相关组件；Aware；
		把Spring底层一些组件注入到自定义的Bean中；
		xxxAware：功能使用xxxProcessor；
			ApplicationContextAware==》ApplicationContextAwareProcessor；