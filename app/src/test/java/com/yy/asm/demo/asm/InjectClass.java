package com.yy.asm.demo.asm;

/**
 * @ProjectName: ASMDemo
 * @Author: yangshun
 * @CreateDate: 2020/9/22 10:04 PM
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/9/22 10:04 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class InjectClass {

    @TimeCost
    private void processData() throws InterruptedException {
//        long start = System.currentTimeMillis();
        Thread.sleep(2000);
//        long end = System.currentTimeMillis();
//        long interval = end - start;
//        System.out.println("processData cost time : " + interval + " ms");
    }
}
