package com.jack.spring.xml;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

public class LoggingAspect {

	public void beforeMethod(JoinPoint joinPoint){
		String methodName = joinPoint.getSignature().getName();
		Object [] args = joinPoint.getArgs();

		System.out.println("The method " + methodName + " begins with " + Arrays.asList(args));
	}

	public void afterMethod(JoinPoint joinPoint){
		String methodName = joinPoint.getSignature().getName();
		System.out.println("The method " + methodName + " ends");
	}

	public void afterReturning(JoinPoint joinPoint, Object result){
		String methodName = joinPoint.getSignature().getName();
		System.out.println("The method " + methodName + " ends with " + result);
	}

	public void afterThrowing(JoinPoint joinPoint, Exception e){
		String methodName = joinPoint.getSignature().getName();
		System.out.println("The method " + methodName + " occurs excetion:" + e);
	}

//	public Object aroundMethod(ProceedingJoinPoint pjd){
//
//		Object result = null;
//		String methodName = pjd.getSignature().getName();
//
//		try {
//			//前置通知
//			System.out.println("The method " + methodName + " begins with " + Arrays.asList(pjd.getArgs()));
//			//执行目标方法
//			result = pjd.proceed();
//			//返回通知
//			System.out.println("The method " + methodName + " ends with " + result);
//		} catch (Throwable e) {
//			//异常通知
//			System.out.println("The method " + methodName + " occurs exception:" + e);
//			throw new RuntimeException(e);
//		}
//		//后置通知
//		System.out.println("The method " + methodName + " ends");
//
//		return result;
//	}
}
