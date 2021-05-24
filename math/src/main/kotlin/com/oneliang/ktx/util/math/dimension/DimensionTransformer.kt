package com.oneliang.ktx.util.math.dimension

import com.oneliang.ktx.util.common.to1DArray

fun Array<Array<Array<Int>>>.to1D(): Array3DT1D<Int> {
    if (this.isEmpty() || this[0].isEmpty() || this[0][0].isEmpty()) {
        return Array3DT1D(emptyArray())
    }
    val xSize = this.size
    val ySize = this[0].size
    val zSize = this[0][0].size
    val array = this.to1DArray()
    return Array3DT1D(array, xSize, ySize, zSize)
}

fun Array<Array<Array<Float>>>.to1D(): Array3DT1D<Float> {
    if (this.isEmpty() || this[0].isEmpty() || this[0][0].isEmpty()) {
        return Array3DT1D(emptyArray())
    }
    val xSize = this.size
    val ySize = this[0].size
    val zSize = this[0][0].size
    val array = this.to1DArray()
    return Array3DT1D(array, xSize, ySize, zSize)
}

fun Array<Array<Array<Double>>>.to1D(): Array3DT1D<Double> {
    if (this.isEmpty() || this[0].isEmpty() || this[0][0].isEmpty()) {
        return Array3DT1D(emptyArray())
    }
    val xSize = this.size
    val ySize = this[0].size
    val zSize = this[0][0].size
    val array = this.to1DArray()
    return Array3DT1D(array, xSize, ySize, zSize)
}

fun Array<Array<Int>>.to1D(): Array2DT1D<Int> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return Array2DT1D(emptyArray())
    }
    val xSize = this.size
    val ySize = this[0].size
    val array = this.to1DArray()
    return Array2DT1D(array, xSize, ySize)
}

fun Array<Array<Float>>.to1D(): Array2DT1D<Float> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return Array2DT1D(emptyArray())
    }
    val xSize = this.size
    val ySize = this[0].size
    val array = this.to1DArray()
    return Array2DT1D(array, xSize, ySize)
}

fun Array<Array<Double>>.to1D(): Array2DT1D<Double> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return Array2DT1D(emptyArray())
    }
    val xSize = this.size
    val ySize = this[0].size
    val array = this.to1DArray()
    return Array2DT1D(array, xSize, ySize)
}

fun main() {
    val array3D = Array(10) { x ->
        Array(10) { y ->
            Array(10) { z ->
                x * 10 * 10 + y * 10 + z
            }
        }
    }
    array3D.forEach { xArray ->
        xArray.forEach { yArray ->
            yArray.forEach {
                println(it)
            }
        }
    }
    println("----------")
    val array3DT1D = array3D.to1D()
    array3DT1D.array.forEach {
        println(it)
    }
    println(array3DT1D[0, 9, 9])

    val newArray3D = array3DT1D.to3DArray()
    array3D.forEachIndexed { x, xArray ->
        xArray.forEachIndexed { y, yArray ->
            yArray.forEachIndexed { z, value ->
                if (value != array3DT1D[x, y, z]) {
                    error("value:%s, x:%s, y:%s, z:%s".format(value, x, y, z))
                }
                if (value != newArray3D[x][y][z]) {
                    error("value:%s, x:%s, y:%s, z:%s".format(value, x, y, z))
                }
            }
        }
    }
}