package com.bamboo.canary

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class ByteCodeInsertTransform extends Transform {

    private static final String TAG = 'ByteCodeInsert'
    @Override
    String getName() {
        return "ByteCodeInsertTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental)

            throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)


        println TAG +'=======================transform called======================='


        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //文件夹
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                println TAG +' directoryInput.name -> ' + directoryInput.name
                //TODO byte Code insert with .class

                File dir = directoryInput.file;
                if (dir) {
                    dir.traverse (type: FileType.FILES, nameFilter:~/.*\.class/){
                        File file->
                            def name = file.name
                            if(name.endsWith(".class")
                            && (!name.startsWith("R\$"))
                            && "R.class" != name
                            && "BuildConfig.class"!=name) {
                                println TAG +' find class:'+name
//                                ByteCodeInsert find class:Person.class
//                                ByteCodeInsert find class:MainActivity.class


                                ClassReader classReader = new ClassReader(file.bytes);
                                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

                                //开始插桩
                                classReader.accept(new MyClassVisitor(Opcodes.ASM7, classWriter), ClassReader.EXPAND_FRAMES);

                                byte[] bytes = classWriter.toByteArray();

                                FileOutputStream fileOutputStream =new FileOutputStream(file.path)
                                fileOutputStream.write(bytes)
                                fileOutputStream.close()

                            }
                    }
                }


                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { JarInput jarInput ->
                //第三方jar
                def jarName = jarInput.name
                println TAG +' jarName -> ' + jarName

                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if(jarName.endsWith('.jar'))
                {
                    jarName = jarName.substring(0, jarName.length()-4)
                }

                def dest = outputProvider.getContentLocation(jarName+md5Name, jarInput.contentTypes,jarInput.scopes, Format.JAR)
                //TODO byte Code insert with jar
                println TAG +' dest -> ' + dest
                FileUtils.copyFile(jarInput.file, dest)
            }

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
//            if (!inject) {
//                return;
//            }

            visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            visitVarInsn(LSTORE, 1);



        }


        @Override
        protected void onMethodExit(int opcode) {
//            if (!inject) {
//                return;
//            }
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
        }
    }

}