package com.bamboo2.fix

import org.gradle.api.Plugin
import org.gradle.api.Project

class FixPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        println "hello , i am FixPlugin!"
    }
}