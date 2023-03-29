// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.oneliang.gradle.applyFeatureDependencies
import com.oneliang.gradle.applyTestFeatureDependencies
import com.oneliang.ktx.gradle.applyMavenPublishPlugin
import com.oneliang.ktx.gradle.generateJarName
import java.text.SimpleDateFormat
import java.util.*

plugins {
    kotlin("jvm") version Constants.kotlinVersion
}

val GROUP by extra(Constants.group)
val VERSION by extra(Constants.version)

buildscript {
    repositories {
        mavenLocal()
//        maven("https://maven.aliyun.com/repository/public/")
        google()
//        jcenter()//will be deprecated
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Constants.kotlinVersion}")
//        classpath("org.jetbrains.kotlin:kotlin-reflect:${Constants.kotlinVersion}")
//        classpath("org.jetbrains.kotlin:kotlin-stdlib:${Constants.kotlinVersion}")
        classpath("com.oneliang.ktx:gradle-ext:1.0")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenLocal()
//        maven("https://maven.aliyun.com/repository/public/")
        google()
//        jcenter()//will be deprecated
        mavenCentral()
    }
}

subprojects {
    if (this.subprojects.isEmpty()) {
        apply(plugin = "java")
        apply(plugin = "kotlin")
        applyFeatureDependencies()
        applyTestFeatureDependencies()
        applyMavenPublishPlugin(Constants.group, this.generateJarName(), Constants.version)
        java {
            sourceSets {
                main {
                    java {
//                    setSrcDirs(listOf("src"))
                    }
                }

                test {
                    java {
//                    setSrcDirs(listOf("test"))
                    }
                }
            }
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
            withSourcesJar()
//            withJavadocJar()
        }
        kotlin {
            sourceSets {
                main {
                }

                test {
                }
            }
        }
    } else {
        //project directory
    }
}

val cleanTask = tasks.findByName("clean")
if (cleanTask == null) {
    tasks.register("clean", type = Delete::class, configurationAction = {
        this.delete(rootProject.buildDir)
    })
}

fun getCurrentFormatTime(): String {
    val date = Date()
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return simpleDateFormat.format(date)
}