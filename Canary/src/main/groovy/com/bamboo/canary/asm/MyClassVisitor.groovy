package com.bamboo.canary.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/***
 * 分析器
 */
class MyClassVisitor extends ClassVisitor {

    MyClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor method = super.visitMethod(access, name, descriptor, signature, exceptions)
        return new MyMethodVisitor(api, method, access, name, descriptor)
    }


/***
 * 方法访问器
 */
    static class MyMethodVisitor extends AdviceAdapter {
        private static final String TAG = "MyMethodVisitor"
        private static final String ANNOTATION_TIME_COST = "Lcom/yy/asm/demo/TimeCost;"

        protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor)
        }

        boolean inject = false

        @Override
        AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            System.out.println(TAG + " " + getName() + " -> " + descriptor)
            if (ANNOTATION_TIME_COST == descriptor) {
                inject = true
            }
            return super.visitAnnotation(descriptor, visible)
        }


        @Override
        protected void onMethodEnter() {
            if (!inject) {
                return
            }
            visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false)
            visitVarInsn(LSTORE, 1)
        }


        @Override
        protected void onMethodExit(int opcode) {
            if (!inject) {
                return
            }
            visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            visitVarInsn(LSTORE, 3);
            visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            visitTypeInsn(NEW, "java/lang/StringBuilder");
            visitInsn(DUP);
            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            visitLdcInsn("method called cost ");
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            visitVarInsn(LLOAD, 3);
            visitVarInsn(LLOAD, 1);
            visitInsn(LSUB);
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
            visitLdcInsn(" ms");
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            System.out.println(TAG + " method: " + getName() + " inject byteCode success!")
        }
    }
}
