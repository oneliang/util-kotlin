package com.oneliang.ktx.util.test.concurrent.atomic

import com.oneliang.ktx.util.common.toFormatString
import com.oneliang.ktx.util.common.toUtilDate
import com.oneliang.ktx.util.concurrent.atomic.LRUCacheSet

fun main() {
    val lruSet = LRUCacheSet<String>(2)
    lruSet.operate("B")
    lruSet.operate("A")
    lruSet.operate("A")
    lruSet.operate("A")
    lruSet.operate("B")
    lruSet.operate("A")
    lruSet.forEach {
        println("${it.value}, last used time:" + it.lastUsedTime.toUtilDate().toFormatString() + ", count:" + it.count)
    }
}