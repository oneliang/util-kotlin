val dependencies by extra {
    mapOf("junit" to "junit:junit:4.12",
            "kotlin-reflect" to "org.jetbrains.kotlin:kotlin-reflect:${Constants.kotlinVersion}",
            "kotlin-stdlib-js" to "org.jetbrains.kotlin:kotlin-stdlib-js:${Constants.kotlinVersion}",
            "kotlin-test" to "org.jetbrains.kotlin:kotlin-test",
            "kotlin-test-junit" to "org.jetbrains.kotlin:kotlin-test-junit",
            "kotlinx-coroutines-core" to "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Constants.kotlinxCoroutinesVersion}",
            "jexcelapi-jxl" to "net.sourceforge.jexcelapi:jxl:2.6.10",
            "log4j" to "log4j:log4j:1.2.14")
}