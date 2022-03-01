package com.oneliang.ktx.util.test.validator

import com.oneliang.ktx.util.validator.validate

fun main() {
    val testModel = TestModel().apply {
        this.name = "n"
        this.email = "a@a.com"
    }
    testModel.validate().forEach {
        println(it.fieldName + "," + it.result)
    }
}