package com.oneliang.ktx.util.math.algebra

abstract class Batching(open val batchSize: Int) {

    abstract fun reset()

    abstract fun fetch(): List<Pair<Double, Array<Double>>>
}