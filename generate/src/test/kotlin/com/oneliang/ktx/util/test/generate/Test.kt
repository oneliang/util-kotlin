package com.oneliang.ktx.util.test.generate

import com.oneliang.ktx.util.common.toFile
import com.oneliang.ktx.util.generate.Template

fun main() {
    val templateContent = "D:/Dandelion/java/githubWorkspace/util-kotlin/generate/src/test/resources/template.tmpl".toFile().readText()
    val option = Template.Option()
    option.json = "{name:'aaa'}"
    println(Template.generate(templateContent, option))
}