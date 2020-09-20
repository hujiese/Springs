package com.jack.spring.relation;

import com.jack.spring.autowire.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans-relation.xml");
//        Address address = (Address)ctx.getBean("address1");
//        System.out.println(address);
//
//        address = (Address)ctx.getBean("address2");
//        System.out.println(address);
        Person person = (Person)ctx.getBean("person");
        System.out.println(person);
    }
}
