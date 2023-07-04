package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.toElementRelativeMap

fun main() {
    val list = listOf<Int>(1, 2)
    val map = list.toElementRelativeMap {
        if (it % 2 == 0) {
            return@toElementRelativeMap false
        }
        true
    }
    println(map)
}