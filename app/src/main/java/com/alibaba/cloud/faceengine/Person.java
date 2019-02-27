package com.alibaba.cloud.faceengine;

public class Person {
    public String id;
    public String name;
    public Feature[] features;

    @Override
    public String toString() {
        if (features == null) {
            return "Person{" +
                    "id='" + id + '\'' +
                    "name='" + name + '\'' +
                    ", features length=0}";
        } else {
            return "Person{" +
                    "id='" + id + '\'' +
                    "name='" + name + '\'' +
                    ", features length=" + features.length + '}';
        }
    }
}
