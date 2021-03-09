package com.oneliang.ktx.util.math.algebra

import com.oneliang.ktx.util.common.sumByDoubleIndexed

object LinearFunction {

    fun linear(xArray: Array<Double>, weightArray: Array<Double> = emptyArray()): Double {
        if (xArray.isEmpty()) {
            error("x array can not be empty, it have one x at least")
        }
        val fixWeightArray = when {
            weightArray.isEmpty() -> {
                Array(xArray.size) { 0.0 }
            }
            xArray.size == weightArray.size -> {
                weightArray
            }
            else -> {
                error("x array size must be equal weight array size")
            }
        }
        return xArray.sumByDoubleIndexed { index, item ->
            fixWeightArray[index] * item
        }
    }
}