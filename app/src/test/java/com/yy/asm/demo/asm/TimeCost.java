package com.yy.asm.demo.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ProjectName: ASMDemo
 * @Author: yangshun
 * @CreateDate: 2020/9/22 9:13 PM
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/9/22 9:13 PM
 * @UpdateRemark:
 * @Version: 1.0
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface TimeCost {
}
