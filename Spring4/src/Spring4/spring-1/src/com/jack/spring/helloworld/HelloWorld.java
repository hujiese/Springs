package com.jack.spring.helloworld;

public class HelloWorld {

	private String user;
	
	public HelloWorld() {
		System.out.println("HelloWorld's constructor...");
	}
	
	public void setUser(String user) {
		System.out.println("setUser:" + user);
		this.user = user;
	}

	public void hello(){
		System.out.println("Hello: " + user);
	}
	
}
