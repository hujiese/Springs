package com.jack.spring.collections;

import com.jack.spring.helloworld.Car;

import java.util.Map;

public class NewPerson {
    private int age;
    private String name;
    private Map<String, Car> cars;

    public NewPerson() {
    }

    public NewPerson(int age, String name, Map<String, Car> cars) {
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

    public Map<String, Car> getCars() {
        return cars;
    }

    public void setCars(Map<String, Car> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return "NewPerson{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", cars=" + cars +
                '}';
    }
}
