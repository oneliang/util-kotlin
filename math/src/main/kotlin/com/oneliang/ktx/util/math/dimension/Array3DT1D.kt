package com.oneliang.ktx.util.math.dimension

import com.oneliang.ktx.util.common.get
import com.oneliang.ktx.util.common.to3DArray


class Array3DT1D<T : Any>(val array: Array<T>, val xSize: Int = 0, val ySize: Int = 0, val zSize: Int = 0) {

    operator fun get(x: Int, y: Int, z: Int): T {
        return this.array.get(this.xSize, this.ySize, this.zSize, x, y, z)
    }
}

fun Array3DT1D<Int>.to3DArray(): Array<Array<Array<Int>>> = this.array.to3DArray(this.xSize, this.ySize, this.zSize)

fun Array3DT1D<Float>.to3DArray(): Array<Array<Array<Float>>> = this.array.to3DArray(this.xSize, this.ySize, this.zSize)

fun Array3DT1D<Double>.to3DArray(): Array<Array<Array<Double>>> = this.array.to3DArray(this.xSize, this.ySize, this.zSize)