package com.yy.asm.demo.asm;

import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @ProjectName: ASMDemo
 * @Author: yangshun
 * @CreateDate: 2020/9/22 9:43 PM
 * @Description:
 * @UpdateUser:
 * @UpdateDate: 2020/9/22 9:43 PM
 * @UpdateRemark:
 * @Version: 1.0
 */
public class MainEntry {
    static String inputClass =
            "/Users/dw/StudioProjects/Demo/ASMDemo/app/src/test/java/com/yy/asm/demo/asm/InjectClass.class";

    static String outputClass =
            "/Users/dw/StudioProjects/Demo/ASMDemo/app/src/test/java/com/yy/asm/demo/target/InjectClass2.class";

    /****
     * ASM 7.x 版本不支持 jdk8 编译的class文件读取
     * java.lang.IllegalArgumentException: Unsupported class file major version 59
     */
    @Test
    public void startInject() {
        try {

            FileInputStream fileInputStream = new FileInputStream(inputClass);

            ClassReader classReader = new ClassReader(fileInputStream);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

            //开始插桩
            classReader.accept(new MyClassVisitor(Opcodes.ASM7, classWriter), ClassReader.EXPAND_FRAMES);

            byte[] bytes = classWriter.toByteArray();

            FileOutputStream outputStream = new FileOutputStream(outputClass);

            //插桩之后的class，写入文件
            outputStream.write(bytes);

            outputStream.close();

            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 分析器
     */
    private static class MyClassVisitor extends ClassVisitor {


        public MyClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor method = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new MyMethodVisitor(api, method, access, name, descriptor);
        }
    }


    /***
     * 方法访问器
     */
    private static class MyMethodVisitor extends AdviceAdapter {

        protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
        }

        boolean inject = false;

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {

            System.out.println(getName() +" -> "+descriptor);
            if ("Lcom/yy/asm/demo/asm/TimeCost;".equals(descriptor)) {
                inject = true;
            }

            return super.visitAnnotation(descriptor, visible);
        }

        int start;
        int end;

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
            if (!inject) {
                return;
            }

//        LINENUMBER 17 L0
//        INVOKESTATIC java/lang/System.currentTimeMillis ()J
//        LSTORE 1

            invokeStatic(Type.getType("Ljava/lang/System;"), new Method("currentTimeMillis", "()J"));
            start = newLocal(Type.LONG_TYPE);
            storeLocal(start);

        }


        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            if (!inject) {
                return;
            }

//        LINENUMBER 19 L2
//        INVOKESTATIC java/lang/System.currentTimeMillis ()J
//        LSTORE 3

            invokeStatic(Type.getType("Ljava/lang/System;"), new Method("currentTimeMillis", "()J"));
            end = newLocal(Type.LONG_TYPE);
            storeLocal(end);

//        LINENUMBER 20 L3
//        LLOAD 3
//        LLOAD 1
//        LSUB
//        LSTORE 5

            loadLocal(end);
            loadLocal(start);
            math(SUB, Type.LONG_TYPE);

            int local = newLocal(Type.LONG_TYPE);
            storeLocal(local);


//            LINENUMBER 21 L4
//            GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
//            NEW java/lang/StringBuilder
//            DUP
//            INVOKESPECIAL java/lang/StringBuilder.<init> ()V
//            LDC "processData cost time : "
//            INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
//            LLOAD 5
//            INVOKEVIRTUAL java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
//            LDC " ms"
//            INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
//            INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
//            INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V

            visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            newInstance(Type.getType("Ljava/lang/StringBuilder;"));
            dup();
            invokeConstructor(Type.getType("Ljava/lang/StringBuilder;"), new Method("<init>", "()V"));
            visitLdcInsn("processData cost time : ");
            invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            loadLocal(local);
            invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("append", "(J)Ljava/lang/StringBuilder;"));
            visitLdcInsn(" ms");

            invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
            invokeVirtual(Type.getType("Ljava/lang/StringBuilder;"), new Method("toString", "()Ljava/lang/String;"));
            invokeVirtual(Type.getType("Ljava/io/PrintStream;"), new Method("println", "(Ljava/lang/Object;)V"));

        }
    }

}
