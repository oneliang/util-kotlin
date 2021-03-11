package com.oneliang.ktx.util.test

import com.oneliang.ktx.util.math.algebra.LinearMachine

fun main() {
//    val weightArray = Array(3) { 0.0 }
    val weightArray = Array(17) { 0.0 }
//    val learningRate = 0.00000000001
    val learningRate = 0.001
    val times = 500000
    val batching = TestTrendBatching(100)
//    val batching = TestStableDataBatching(100)
    val newWeightArray = LinearMachine.study(batching, weightArray, learningRate, times, 1000)
    batching.reset()
    LinearMachine.test(batching, newWeightArray)
}