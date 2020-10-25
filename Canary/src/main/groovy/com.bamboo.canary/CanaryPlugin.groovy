package com.bamboo.canary
import org.gradle.api.Plugin
import org.gradle.api.Project

class CanaryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        println 'hello canary plugin'
    }
}