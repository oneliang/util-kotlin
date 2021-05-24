package com.oneliang.ktx.util.math.dimension

import com.oneliang.ktx.util.common.get
import com.oneliang.ktx.util.common.to2DArray


class Array2DT1D<T : Any>(val array: Array<T>, val xSize: Int = 0, val ySize: Int = 0) {

    operator fun get(x: Int, y: Int): T {
        return this.array.get(this.xSize, this.ySize, x = x, y = y)
    }
}

fun Array2DT1D<Int>.to2DArray(): Array<Array<Int>> = this.array.to2DArray(this.xSize, this.ySize)
fun Array2DT1D<Float>.to2DArray(): Array<Array<Float>> = this.array.to2DArray(this.xSize, this.ySize)
fun Array2DT1D<Double>.to2DArray(): Array<Array<Double>> = this.array.to2DArray(this.xSize, this.ySize)