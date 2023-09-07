package com.oneliang.ktx.util.test.concurrent.atomic

import com.oneliang.ktx.pojo.LongWrapper
import com.oneliang.ktx.util.common.toByteArray
import com.oneliang.ktx.util.common.toInt
import com.oneliang.ktx.util.concurrent.atomic.AtomicBinary

fun main() {
    val atomicBinary = object : AtomicBinary<Int>(1, 2, 4,
        byteArrayToData =
        { it.toInt() },
        dataToByteArray =
        { it.toByteArray() }) {}
    val index0 = LongWrapper(0)
    val index1 = LongWrapper(1)
    val index5 = LongWrapper(5)
    atomicBinary.operate(index0, create = { 100 }, update = {
        it - 1
    })
    atomicBinary.operate(index1, create = { 200 }, update = {
        it - 1
    })
    atomicBinary.operate(index5, create = { 500 }, update = {
        it - 1
    })

    println(atomicBinary[index0.value])
    println(atomicBinary[index1.value])
    println(atomicBinary[index5.value])
    println("----------iterable----------")
    atomicBinary.forEachIndexed { index, value ->
        println("index:%s, value:%s".format(index, value))
    }
}