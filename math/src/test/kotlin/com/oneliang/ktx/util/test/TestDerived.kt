package com.oneliang.ktx.util.test

import com.oneliang.ktx.util.math.algebra.LinearMachine

fun main() {
    val weightArray = arrayOf(0.1, 0.1)
    val learningRate = 0.001
    val times = 100
    val testBatching = TestBatching(6)
    val newWeightArray = LinearMachine.study(testBatching, weightArray, learningRate, times)
    testBatching.reset()
    LinearMachine.test(testBatching, newWeightArray)
}