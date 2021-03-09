package com.oneliang.ktx.util.math.algebra

import kotlin.math.pow

object LossFunction {
    fun loss(calculateY: Double, realY: Double): Double {
        return (calculateY - realY).pow(2)
    }
}