// Top-level build file where you can add configuration options common to all sub-projects/modules.
apply(from = "${rootDir}/gradle/dependencies.gradle.kts")
val GROUP by extra(Constants.group)
val VERSION by extra(Constants.version)

buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
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
        jcenter()
    }
}

tasks.register("clean", type = Delete::class, configurationAction = {
    this.delete(rootProject.buildDir)
})

fun getCurrentFormatTime(): String {
    val date = java.util.Date()
    val simpleDateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return simpleDateFormat.format(date)
}