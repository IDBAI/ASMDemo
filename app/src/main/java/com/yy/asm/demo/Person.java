package com.yy.asm.demo;

/**
 * @ProjectName: ASMDemo
 * @Author: yangshun
 * @CreateDate: 2020/9/22 2:50 PM
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/9/22 2:50 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class Person {

    public static void main(String[] args) {
        Person person = new Person();
        person.talk();

    }


    @TimeCost
    public void talk() {
        System.out.println("hello man");
    }


    @TimeCost
    public int doSomething() {
        System.out.println("i am doing");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 100;
    }
}

















