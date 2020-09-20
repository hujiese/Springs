package com.jack.spring.collections;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext apx = new ClassPathXmlApplicationContext("beans.xml");
//        Person person = (Person)apx.getBean("person3");
//        System.out.println(person);

//        NewPerson person = (NewPerson)apx.getBean("person4");
//        System.out.println(person);

//        DataSource dataSource = (DataSource)apx.getBean("dataSource");
//        System.out.println(dataSource);

//        Person person = (Person)apx.getBean("person5");
//        System.out.println(person);

        Person person = (Person)apx.getBean("person6");
        System.out.println(person);
    }
}
