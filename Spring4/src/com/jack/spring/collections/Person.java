package com.jack.spring.collections;

import com.jack.spring.helloworld.Car;

import java.util.List;

public class Person {
    private int age;
    private String name;
    private List<Car> cars;

    public Person() {
    }

    public Person(int age, String name, List<Car> cars) {
        this.age = age;
        this.name = name;
        this.cars = cars;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", cars=" + cars +
                '}';
    }
}
