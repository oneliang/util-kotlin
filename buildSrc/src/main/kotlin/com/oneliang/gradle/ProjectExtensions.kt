package com.oneliang.gradle

import Dependencies
import org.gradle.api.Project


fun Project.applyTestFeatureDependencies() {
    this.dependencies.add("implementation", Dependencies["kotlin-test"])
    this.dependencies.add("implementation", Dependencies["kotlin-test-junit"])
}

fun Project.applyFeatureDependencies() {
    this.dependencies.add("api", this.fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    this.dependencies.add("implementation", Dependencies["kotlin-reflect"])
}