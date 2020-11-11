package com.bamboo.canary

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class CanaryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println '===================CanaryPlugin start===================='


        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new ByteCodeInsertTransform())
        project.gradle.addListener(new TaskListener())

        println '===================CanaryPlugin end======================'

        println 'start insert task'
        project.tasks.create("loggerTask", LoggerTask.class)
        project.afterEvaluate {
            def preBuild = project.tasks.getByName("preBuild")
            preBuild.dependsOn project.tasks.getByName("loggerTask")
        }

    }
}