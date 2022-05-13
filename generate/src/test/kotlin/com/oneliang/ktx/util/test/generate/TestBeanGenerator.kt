package com.oneliang.ktx.util.test.generate

import com.oneliang.ktx.util.generate.BeanGenerator

fun main() {
    val beanFileList = listOf("/D:/Dandelion/java/githubWorkspace/util-kotlin/generate/src/main/resources/config/kotlin-bean.txt")
    val beanTemplateFullFilename = "/D:/Dandelion/java/githubWorkspace/util-kotlin/generate/src/main/resources/template/kotlin-bean.tmpl"
    val baseOutputDirectory = "/D:/Dandelion/java/githubWorkspace/util-kotlin/generate/src/test/kotlin"
    BeanGenerator.generate(beanFileList, beanTemplateFullFilename, baseOutputDirectory)
}