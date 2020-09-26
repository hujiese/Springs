package com.jack.spring.annotation;

import com.jack.spring.annotation.controller.UserController;
import com.jack.spring.annotation.repository.UserRepositoryImpl;
import com.jack.spring.annotation.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans-annotation.xml");

//        TestObject obj = (TestObject)ctx.getBean("testObjects");
//        System.out.println(obj);

//        UserController userController = (UserController)ctx.getBean("userController");
//        System.out.println(userController);

//        UserService userService = (UserService)ctx.getBean("userService");
//        System.out.println(userService);
//
//        UserRepositoryImpl userRepositoryImpl = (UserRepositoryImpl)ctx.getBean("userRepositoryImpl");
//        System.out.println(userRepositoryImpl);

        UserController userController = (UserController)ctx.getBean("userController");
        userController.execute();
    }
}
