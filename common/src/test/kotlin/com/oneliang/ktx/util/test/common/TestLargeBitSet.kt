package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.LargeBitSet
import com.oneliang.ktx.util.common.toBinaryString
import com.oneliang.ktx.util.common.toByteArray

fun main(){
    val largeBitSet = LargeBitSet(1000)
    largeBitSet.set(1)
    largeBitSet.set(100)
    largeBitSet.set(101)
    println( (100L).toByteArray().toBinaryString())
}