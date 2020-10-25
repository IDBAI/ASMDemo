package com.bamboo.canary

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

class TaskListener implements TaskExecutionListener, BuildListener {

    private static final String TAG = 'TaskListener'

    @Override
    void buildStarted(Gradle gradle) {
        println TAG + ' buildStarted'
    }

    @Override
    void settingsEvaluated(Settings settings) {
        println TAG + ' settingsEvaluated'
    }

    @Override
    void projectsLoaded(Gradle gradle) {
        println TAG + ' projectsLoaded'
    }

    @Override
    void projectsEvaluated(Gradle gradle) {
        println TAG + ' projectsEvaluated'
    }

    @Override
    void buildFinished(BuildResult buildResult) {
        println TAG + ' buildFinished'
    }

    @Override
    void beforeExecute(Task task) {
        println TAG + ' beforeExecute -> task :'+task.name
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        println TAG + ' afterExecute -> task :' + task.name + ', taskState :' + taskState

        if (task.name == 'packageRelease') {

        }
    }
}