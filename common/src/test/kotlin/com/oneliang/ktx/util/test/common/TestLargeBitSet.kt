package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.LargeBitSet
import com.oneliang.ktx.util.common.toBinaryString
import com.oneliang.ktx.util.common.toByteArray

fun main() {
//    println((64 - 1 shr 6).toInt() + 1)
//    println((1L shl 100).toByteArray().toBinaryString())
//    println((1L shl (100 % 64)).toByteArray().toBinaryString())
//    return
    val begin = System.nanoTime()
    val bitSetCount = 100000000L
    val largeBitSet = LargeBitSet(bitSetCount)
    for (i in 0 until bitSetCount) {
        largeBitSet.set(i)
    }
    println("cost:%s".format(System.nanoTime() - begin))
    println((100L).toByteArray().toBinaryString())
}