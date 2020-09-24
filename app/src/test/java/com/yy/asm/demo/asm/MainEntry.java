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
            "/Users/dw/StudioProjects/Demo/ASMDemo/app/src/test/java/com/yy/asm/demo/asm/InjectClass.class";

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
            if (!inject) {
                return;
            }

            visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            visitVarInsn(LSTORE, 1);



        }


        @Override
        protected void onMethodExit(int opcode) {
            if (!inject) {
                return;
            }

//            visitLdcInsn(new Long(2000L));
//            visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
            visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            visitVarInsn(LSTORE, 3);
            visitVarInsn(LLOAD, 3);
            visitVarInsn(LLOAD, 1);
            visitInsn(LSUB);
            visitVarInsn(LSTORE, 5);
            visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            visitTypeInsn(NEW, "java/lang/StringBuilder");
            visitInsn(DUP);
            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            visitLdcInsn("processData cost time : ");
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            visitVarInsn(LLOAD, 5);
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            visitLdcInsn(" ms");
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//            visitInsn(RETURN);
//            visitMaxs(4, 7);
//            visitEnd();
        }
    }

}
