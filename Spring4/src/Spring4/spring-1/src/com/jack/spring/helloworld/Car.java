package com.jack.spring.helloworld;

public class Car {

	private String company;
	private String brand;

	private int maxSpeed;
	private float price;

	public Car(String company, String brand, float price) {
		super();
		this.company = company;
		this.brand = brand;
		this.price = price;
	}

	public Car(String company, String brand, int maxSpeed) {
		super();
		this.company = company;
		this.brand = brand;
		this.maxSpeed = maxSpeed;
	}

	public Car(String company, String brand, int maxSpeed, float price) {
		super();
		this.company = company;
		this.brand = brand;
		this.maxSpeed = maxSpeed;
		this.price = price;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	@Override
	public String toString() {
		return "Car [company=" + company + ", brand=" + brand + ", maxSpeed="
				+ maxSpeed + ", price=" + price + "]";
	}
}
