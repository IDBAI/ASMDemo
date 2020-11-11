package com.bamboo.canary
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/***
 * 日志：
 * > Configure project :app
 * ===================CanaryPlugin start====================
 * ===================CanaryPlugin end======================
 * start insert task
 * TaskListener projectsEvaluated
 *
 * > Task :app:loggerTask
 * TaskListener beforeExecute -> task :loggerTask
 * -------------LoggerTask Start run3-------------
 * -------------LoggerTask Start run2-------------
 * -------------LoggerTask Start run-------------
 * TaskListener afterExecute -> task :loggerTask, taskState :org.gradle.api.internal.tasks.TaskStateInternal@66899194
 *
 * > Task :app:preBuild
 * TaskListener beforeExecute -> task :preBuild
 * TaskListener afterExecute -> task :preBuild, taskState :org.gradle.api.internal.tasks.TaskStateInternal@1e9ceca6
 *
 * 调试：
 * ./gradlew app:loggerTask -Dorg.gradle.debug=true
 */
class LoggerTask extends DefaultTask {



    @TaskAction
    void start1() {
        println "-------------LoggerTask Start run1-------------"
    }

    @TaskAction
    void start2() {
        println "-------------LoggerTask Start run2-------------"
    }
    @TaskAction
    void start3() {
        println "-------------LoggerTask Start run3-------------"
    }


}