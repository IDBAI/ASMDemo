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
import com.bamboo.canary.asm.CrashLibFixedClassVisitor
import com.bamboo.canary.asm.JarUtils
import com.bamboo.canary.asm.TimeCostClassVisitor
import groovy.io.FileType
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

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
                                ClassReader classReader = new ClassReader(file.bytes);
                                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                                //开始插桩
                                classReader.accept(new TimeCostClassVisitor(Opcodes.ASM7, classWriter), ClassReader.EXPAND_FRAMES)

                                byte[] bytes = classWriter.toByteArray()
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
                fixedCrashLibNPException(jarInput)
                def dest = outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }

        }
    }

    void fixedCrashLibNPException(JarInput jarInput ) {
        if (jarInput.getName().contains("ReportSDKLib.jar")) {
            File file = jarInput.getFile()
            JarFile jar = new JarFile(file)
            Enumeration<JarEntry> list =  jar.entries()
            while (list.hasMoreElements()) {
                JarEntry entry = list.nextElement()
                if (entry.getName() == "com/bamboo/report/DoReport.class") {
                    InputStream ins = jar.getInputStream(entry)
                    byte[] bytes = IOUtils.read(ins)
                    ins.close()
                    ClassReader reader =  new ClassReader(bytes)
                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    reader.accept(new CrashLibFixedClassVisitor(Opcodes.ASM7,classWriter),ClassReader.EXPAND_FRAMES)
                    byte[] outBytes = classWriter.toByteArray()
                    JarUtils.writeJarFile(jarInput, entry.getName(), outBytes)

                    FileOutputStream fo = new FileOutputStream(new File("/Users/dw/StudioProjects/Demo/ASMDemo/Canary/fixedNP/DoReport.class"))
                    fo.write(outBytes)
                    fo.close()
                }
            }
        }
    }

}