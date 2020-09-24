package com.yy.asm.demo.asm;


/**
 * @ProjectName: ASMDemo
 * @Author: yangshun
 * @CreateDate: 2020/9/24 3:05 PM
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/9/24 3:05 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class TestJava {

    public static void main(String[] args) {
        try {
            new InjectClass().processData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
