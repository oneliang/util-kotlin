// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    kotlin("jvm") version Constants.kotlinVersion
    `maven-publish`
    `kotlin-dsl`
}
val GROUP = "com.oneliang.ktx"
val VERSION = "1.0"
group = GROUP
version = VERSION

buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Constants.kotlinVersion}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

dependencies {
    implementation(gradleApi())
    implementation("com.oneliang.ktx:util-kotlin-base:1.0")
    implementation("com.oneliang.ktx:util-kotlin-common:1.0")
    implementation("com.oneliang.ktx:util-kotlin-file:1.0")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])
            version = version
        }
    }
    repositories {
        maven {
        }
    }
}
