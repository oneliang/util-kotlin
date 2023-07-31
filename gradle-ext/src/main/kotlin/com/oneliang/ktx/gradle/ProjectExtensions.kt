package com.oneliang.ktx.gradle

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import java.io.File

fun Project.generateJarName(includeRootProject:Boolean = true): String {
    val jarName = StringBuilder()
    val parentProject = this.parent
    if (parentProject != null) {//&& parentProject != rootProject) {
        if(includeRootProject) {
            jarName.append(parentProject.generateJarName(includeRootProject) + "-")
        }else if(parentProject != rootProject){
            jarName.append(parentProject.generateJarName(includeRootProject) + "-")
        }
    }
    jarName.append(this.name)
    return jarName.toString()
}

fun Project.applyDependencies(configurationList: List<Pair<String, Any>>) {
    for (configuration in configurationList) {
        this.dependencies.add(configuration.first, configuration.second)
    }
}

fun Project.applyMavenPublishPlugin(groupId: String, artifactId: String, version: String) {
    val project = this
    project.apply<MavenPublishPlugin>()
    project.extensions.configure<PublishingExtension>(PublishingExtension.NAME) {
        publications {
            create("mavenJava", MavenPublication::class.java) {
                this.groupId = groupId
                this.artifactId = artifactId
                this.version = version
                from(components.getByName("java"))
            }
        }
    }
}

fun Project.applyCheckKotlinCode() {
    val project = this
    val checkKotlinCodeTask = project.tasks.register("checkKotlinCode") {
        println("----------check kotlin code, project:" + project.name + "----------")
        val kotlinMainSourceSets = project.the<SourceSetContainer>().getByName("main").allSource
        val wrongPackageKotlinFileList = mutableListOf<File>()
        for (kotlinFile in kotlinMainSourceSets) {
            if (kotlinFile.name.lastIndexOf(".kt") < 0) {
                continue//not kotlin file, so next
            }
            val lines = kotlinFile.readLines()
            if (lines.isNotEmpty()) {
                var packageName = ""
                for (line in lines) {
                    if (line.indexOf("package ") == 0) {
                        packageName = line
                        break
                    }
                }
                if (packageName.isNotBlank()) {
                    packageName = packageName.replace("package ", "").replace(";", "")
                    val fileString = kotlinFile.absolutePath.replace("\\", ".").replace("/", ".")
                    val kotlinPackageFilePath = "$packageName.${kotlinFile.name}"
                    if (fileString.lastIndexOf(kotlinPackageFilePath) < 0) {
                        wrongPackageKotlinFileList += kotlinFile
                    }
                } else {
                    println("Kotlin package is empty, file:$kotlinFile")
                }
            } else {
                println("File is empty, file:$kotlinFile")
            }
        }
        if (wrongPackageKotlinFileList.size > 0) {
            for (wrongKotlinFile in wrongPackageKotlinFileList) {
                println("Kotlin package is wrong, please check package, file:$wrongKotlinFile")
            }
            throw RuntimeException("Kotlin package error, please see above.")
        }
    }.get()
    project.afterEvaluate {
        val buildTask = project.tasks.findByName("build")
        buildTask?.dependsOn(checkKotlinCodeTask)
    }
}