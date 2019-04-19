package com.alibaba.cloud.faceengine;

public class Person {
    public String id;
    public String name;
    public String tag;
    public Feature[] features;

    @Override
    public String toString() {
        if (features == null) {
            return "Person{" +
                    "id='" + id + '\'' +
                    "name='" + name + '\'' +
                    "tag='" + tag + '\'' +
                    ", features length=0}";
        } else {
            return "Person{" +
                    "id='" + id + '\'' +
                    "name='" + name + '\'' +
                    "tag='" + tag + '\'' +
                    ", features length=" + features.length + '}';
        }
    }
}
