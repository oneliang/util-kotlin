package com.oneliang.ktx.test.util.concurrent.atomic

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.calculateCompose
import com.oneliang.ktx.util.common.calculatePermutation
import com.oneliang.ktx.util.concurrent.atomic.LRUCacheMap

fun main() {
//    val atomicTreeSet = AtomicTreeSet(Comparator<String> { o1, o2 -> o1.compareTo(o2) })
//    atomicTreeSet + "1"
//    atomicTreeSet + "2"
//    atomicTreeSet + "3"
//    atomicTreeSet + "4"
//    atomicTreeSet + "5"
//    atomicTreeSet + "6"
//    println(atomicTreeSet.size())
//    println(atomicTreeSet.joinToString { it })
//    atomicTreeSet - "6"
//    println(atomicTreeSet.size())
//    println(atomicTreeSet.joinToString { it })
//    return
    val lruCacheMap = LRUCacheMap<String, String>(10)
//    val lruCacheMap = LRUCacheSet<String>(10)
    val array = arrayOf("A", "B", "C", "D", "E")
//    lruCacheMap += "B"
//    lruCacheMap += "A"
//    lruCacheMap += "C"
//    lruCacheMap += "D"
//    lruCacheMap += "E"
//    lruCacheMap += "F"
////    lruCacheMap += "G"
//    println(lruCacheMap.size)
//    return
    var totalSize = 0
    val set = mutableListOf<String>()
    for (i in 1..array.size) {
        val composeList = array.calculateCompose(i)
        composeList.forEach {
            val permutationList = it.calculatePermutation()
            totalSize += permutationList.size
            permutationList.forEach { permutation ->
                set += permutation.joinToString(separator = Constants.String.BLANK)
            }
        }
    }
    println(set.size)
    set.forEach { value ->
//        lruCacheMap += value
        lruCacheMap.operate(value, create = { value }, removeWhenFull = { itemCounter ->
            println("value file size:%s, value remove item:%s".format(lruCacheMap.size, itemCounter.hashCode().toString() + "," + itemCounter.key))
        })
    }
    println(lruCacheMap.size)
    var sum = 0
    lruCacheMap.forEach { sum++ }
    println(sum)
    lruCacheMap.forEach {
//        println("${it.value}, last used time:" + it.lastUsedTime.toUtilDate().toFormatString() + ", count:" + it.count)
    }
}