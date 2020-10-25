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
import com.bamboo.canary.asm.MyClassVisitor
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


        println TAG + '=======================transform called======================='


        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //文件夹
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)

                println TAG + ' directoryInput.name -> ' + directoryInput.name
                //TODO byte Code insert with .class

                File dir = directoryInput.file;
                if (dir) {
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) {
                        File file ->
                            def name = file.name
                            if (name.endsWith(".class")
                                    && (!name.startsWith("R\$"))
                                    && "R.class" != name
                                    && "BuildConfig.class" != name) {
                                println TAG + ' find class:' + name
//                                ByteCodeInsert find class:Person.class
//                                ByteCodeInsert find class:MainActivity.class


                                ClassReader classReader = new ClassReader(file.bytes);
                                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

                                //开始插桩
                                classReader.accept(new MyClassVisitor(Opcodes.ASM7, classWriter), ClassReader.EXPAND_FRAMES);

                                byte[] bytes = classWriter.toByteArray();

                                FileOutputStream fileOutputStream = new FileOutputStream(file.path)
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
                println TAG + ' jarName -> ' + jarName

                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith('.jar')) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //TODO byte Code insert with jar
                println TAG + ' dest -> ' + dest
                FileUtils.copyFile(jarInput.file, dest)
            }

        }


    }


}