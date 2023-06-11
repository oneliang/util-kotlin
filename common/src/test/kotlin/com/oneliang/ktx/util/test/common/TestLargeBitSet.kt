package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.LargeBitSet
import com.oneliang.ktx.util.common.toBinaryString
import com.oneliang.ktx.util.common.toByteArray

fun main(){
    println((64 - 1 shr 6).toInt() + 1)
    val largeBitSet = LargeBitSet(1000)
    largeBitSet.set(1)
    largeBitSet.set(64)
    largeBitSet.set(101)
    largeBitSet.set(500)
    println( (100L).toByteArray().toBinaryString())
}