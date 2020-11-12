package com.bamboo.canary.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/***
 * 空指向修复
 */
public class CrashLibFixedClassVisitor extends ClassVisitor {


    CrashLibFixedClassVisitor(int api) {
        super(api)
    }

    CrashLibFixedClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        org.objectweb.asm.MethodVisitor method =  super.visitMethod(access, name, descriptor, signature, exceptions)

        println "visitMethod called -> name : $name , descriptor : $descriptor"
        if(name == "report" && descriptor == "(Ljava/lang/String;)V"){
            return new CrashLibFixedMethodVisitor(api, method, access, name, descriptor)
        }
        return method
    }


    static class  CrashLibFixedMethodVisitor extends AdviceAdapter {

        protected CrashLibFixedMethodVisitor(int api, org.objectweb.asm.MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor)
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter()
            println "================fixed NP success================"
            visitFieldInsn(GETSTATIC, "com/bamboo/report/DoReport", "printer", "Lcom/bamboo/report/Printer;");
            Label label0 = new Label();
            visitJumpInsn(IFNONNULL, label0);
            visitTypeInsn(NEW, "com/bamboo/report/Printer");
            visitInsn(DUP);
            visitMethodInsn(INVOKESPECIAL, "com/bamboo/report/Printer", "<init>", "()V", false);
            visitFieldInsn(PUTSTATIC, "com/bamboo/report/DoReport", "printer", "Lcom/bamboo/report/Printer;");
            visitLabel(label0);
        }
    }

}


