package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.LargeBitSet
import com.oneliang.ktx.util.common.TimeRecord
import com.oneliang.ktx.util.common.toBinaryString
import com.oneliang.ktx.util.common.toByteArray
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    val arraySize = 100000000
    var begin = System.nanoTime()
    var beginTotalMemory = Runtime.getRuntime().totalMemory()
    val originalArray = Array(arraySize) { it ->
        arraySize - it
    }
    println("cost:%s".format(System.nanoTime() - begin))
    println("memory:%s".format(Runtime.getRuntime().totalMemory() - beginTotalMemory))
    begin = System.nanoTime()
    val bitSetSortedArray = sort(originalArray)
    println("sort cost:%s".format(System.nanoTime() - begin))
    println(bitSetSortedArray[0])
    begin = System.nanoTime()
    val sortedArray = originalArray.sortedArray()
    println("java sort cost:%s".format(System.nanoTime() - begin))
    println(sortedArray[0])
    return
    val dictionaryIndexMap = mutableMapOf<String, Int>(
        "你" to 1,
        "好" to 2,
        "吗" to 3
    )
    val atomicInteger = AtomicInteger(dictionaryIndexMap.size)
    var word = "你"
    if (!dictionaryIndexMap.containsKey(word)) {
        dictionaryIndexMap[word] = atomicInteger.incrementAndGet()
    }
    word = "?"
    if (!dictionaryIndexMap.containsKey(word)) {
        dictionaryIndexMap[word] = atomicInteger.incrementAndGet()
    }
    println(dictionaryIndexMap)
    println(Int.MAX_VALUE)
    val aIndex = 1
    val bIndex = 2345
    val aLayer = aIndex shr 5
    val bLayer = bIndex shr 5
    println((1 shl aIndex).toByteArray().toBinaryString())
    println((1 shl bIndex).toByteArray().toBinaryString())
    val nextIndexArray = arrayOf(2, 2345, 3, 4, 5)

//    insertSort(arrayOf(1), 1)
//
//    val emptyList = mutableListOf<Int>()
//    val dataList = mutableListOf<Int>()
}

//fun insertSort(originArray: Array<Int>, insertValue: Int) {
//    val middleValue = originArray[originArray.size / 2]
//    if (insertValue > middleValue) {
//        // insert to right
//    } else {
//        // insert to left
//    }
//}

fun sort(originalArray: Array<Int>): IntArray {
    val timerRecord = TimeRecord(System::nanoTime, System::nanoTime) { category, recordTime ->
        println("category:%s, cost time:%s".format(category, recordTime))
    }
    timerRecord.start()
    var max: Int = Int.MIN_VALUE
    for (it in originalArray) {
        if (it >= max) {
            max = it
        }
    }
    timerRecord.stepRecord()
    val largeBitSet = LargeBitSet((max + 1).toLong())
    for (it in originalArray) {
        largeBitSet.set(it.toLong())
    }
    timerRecord.stepRecord()
    val sortedArray = IntArray(originalArray.size)
    timerRecord.stepRecord()
    var sortedArrayIndex = 0
    for (it in 1..max) {
        if (largeBitSet.get(it.toLong())) {
            sortedArray[sortedArrayIndex] = it
            sortedArrayIndex++
        }
    }
    timerRecord.stepRecord()
    timerRecord.record()
    return sortedArray
}