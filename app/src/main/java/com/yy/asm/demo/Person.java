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
    public int talk() {
        int a = 10;
        int b = 20;
        int c = (a + b) * 10;
        return c;
    }
}

















