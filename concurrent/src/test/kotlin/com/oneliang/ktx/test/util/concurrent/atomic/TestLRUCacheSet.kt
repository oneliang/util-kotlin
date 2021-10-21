package com.oneliang.ktx.test.util.concurrent.atomic

import com.oneliang.ktx.util.common.toFormatString
import com.oneliang.ktx.util.common.toUtilDate
import com.oneliang.ktx.util.concurrent.atomic.LRUCacheSet

fun main() {
    val lruSet = LRUCacheSet<String>(2)
    lruSet += "B"
    lruSet += "A"
    lruSet += "A"
    lruSet += "B"
    lruSet += "B"
    lruSet += "B"
    lruSet.forEach {
        println("${it.value}, last used time:" + it.lastUsedTime.toUtilDate().toFormatString() + ", count:" + it.count)
    }
}