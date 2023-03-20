package com.oneliang.gradle

import Dependencies
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply


fun Project.applyTestDependencies() {
    this.dependencies.add("implementation", Dependencies["kotlin-test"])
    this.dependencies.add("implementation", Dependencies["kotlin-test-junit"])
}

fun Project.applyFeatureDependencies() {
    this.dependencies.add("api", this.fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    this.dependencies.add("implementation", Dependencies["kotlin-reflect"])
}