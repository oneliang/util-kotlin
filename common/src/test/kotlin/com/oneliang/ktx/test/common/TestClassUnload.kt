package com.oneliang.ktx.test.common

import com.oneliang.ktx.Constants

fun main() {
    val code = "STEEL_F_INITIALIZE"
    val tempClassName = code.replace("STEEL" + "_F_", Constants.String.BLANK)
    val className = tempClassName.toLowerCase().let {
        it.substring(0, 1).toUpperCase() + it.substring(1)
    } + "Kt"
    println(className)
    return
    var loader1: TestClassLoader? = TestClassLoader()
    var clazz1: Class<*>? = loader1?.loadClass("TestClass")
    println("class: " + clazz1.hashCode())
    println("classLoaderName: " + clazz1!!.classLoader)
    var object1 = clazz1.newInstance()
    println(object1)
    loader1 = null
    clazz1 = null
    object1 = null
    System.gc()
    loader1 = TestClassLoader()
    clazz1 = loader1.loadClass("TestClass")
    println("class: " + clazz1.hashCode())
    println("classLoaderName: " + clazz1.classLoader)
    object1 = clazz1.newInstance()
    println(object1)
}