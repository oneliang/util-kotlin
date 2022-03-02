package com.oneliang.ktx.util.test.validator

import com.oneliang.ktx.util.validator.validate
import com.oneliang.ktx.util.validator.validateSimply

fun main() {
    val testModel = TestModel().apply {
        this.name = "n"
        this.email = "a@a.com"
    }
    println(testModel.validateSimply())
    testModel.validate().forEach {
        println(it.fieldName + "," + it.result)
    }
}