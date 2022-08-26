package com.mirkowu.mvm.leetcode;

public class RunOrderTest {
    public static void main(String[] args) {
        Animal animal = new Dog();
    }
}

class Animal {
    public static String type = "父类";
    public String name = "Animal";
    public int age;

    static {
        System.out.println("Animal static init fun :" + type);
    }

    {
        System.out.println("Animal  init fun");
    }

    public Animal() {
        System.out.println("Animal 构造函数");
        a();
    }

    public void a() {
        System.out.println("Animal a");
    }
}

class Dog extends Animal {
    public static String type = "子类";
    public String name = "Dog";
    public int age = 1;

    static {
        System.out.println("Dog static init fun :" + type);
    }

    {
        System.out.println(this.name + " init fun " + this.sex);
    }

    public String sex = "1"; //在非静态初始化代码块之后

    public Dog() {
        super();
        System.out.println("Dog 构造函数");
    }

    @Override
    public void a() {
        System.out.println(name + " a " + age);
    }

}

