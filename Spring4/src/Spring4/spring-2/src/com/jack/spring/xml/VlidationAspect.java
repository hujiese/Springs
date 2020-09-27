package com.jack.spring.xml;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

public class VlidationAspect {

	public void validateArgs(JoinPoint joinPoint){
		System.out.println("-->validate:" + Arrays.asList(joinPoint.getArgs()));
	}
}
