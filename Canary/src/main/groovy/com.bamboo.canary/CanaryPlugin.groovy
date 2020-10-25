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

    }
}