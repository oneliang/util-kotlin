package com.oneliang.ktx.test.util.concurrent.atomic

import com.oneliang.ktx.util.common.toFormatString
import com.oneliang.ktx.util.common.toUtilDate
import com.oneliang.ktx.util.concurrent.atomic.LRUCacheMap
import com.oneliang.ktx.util.concurrent.atomic.LRUCacheSet

fun main() {
    val lruSet = LRUCacheMap<String,String>(2)
    lruSet["1"]= "B"
    lruSet["1"]= "A"
    lruSet["1"]= "A"
    lruSet["1"]= "B"
    lruSet["1"]= "B"
    lruSet["1"]= "B"
    lruSet.forEach {
        println("${it.value}, last used time:" + it.lastUsedTime.toUtilDate().toFormatString() + ", count:" + it.count)
    }
}