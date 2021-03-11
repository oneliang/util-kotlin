package com.oneliang.ktx.util.math.algebra

abstract class Batching(open val batchSize: Int) {

    abstract fun reset()

    abstract fun fetch(): Result

    class Result(var finished: Boolean = false, var dataList: List<Pair<Double, Array<Double>>> = emptyList())
}