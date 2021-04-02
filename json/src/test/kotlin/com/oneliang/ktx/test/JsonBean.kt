package com.oneliang.ktx.test

import com.oneliang.ktx.Constants

class JsonBean {
    //    var a = 0
//    var b = Constants.String.BLANK

    //    var c = mapOf("c1" to 1, "c2" to 2)
//    var d = listOf(1, 2)
//    var e = listOf(JsonSubBean().apply { this.name = "1" })
//    var f = arrayOf(1, 2)
    var g = arrayOf(arrayOf(arrayOf(arrayOf(JsonSubBean().also { it.name = "1" }, JsonSubBean().also { it.name = "1" }))))
    var d: JsonSubBean? = null
}