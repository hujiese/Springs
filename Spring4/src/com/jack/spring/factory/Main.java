package com.jack.spring.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans-factory.xml");
//        Car car = (Car)ctx.getBean("car1");
//        System.out.println(car);

        Car car = (Car)ctx.getBean("car2");
        System.out.println(car);
    }
}
