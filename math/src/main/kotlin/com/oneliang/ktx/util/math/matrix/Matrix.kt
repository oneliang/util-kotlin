package com.oneliang.ktx.util.math.matrix

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.sumByDoubleIndexed

fun matrixAdd(aMatrix: Array<Array<Double>>, bMatrix: Array<Array<Double>>, negative: Boolean = false): Array<Array<Double>> {
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray()
    }
    if (aMatrix.size != bMatrix.size || aMatrix[0].size != bMatrix[0].size) {
        error("matrix size not match")
    }

    val resultMatrix = Array(aMatrix.size) { Array(aMatrix[0].size) { 0.0 } }
    for (i in resultMatrix.indices) {
        for (j in resultMatrix[i].indices) {
            if (negative) {
                resultMatrix[i][j] = aMatrix[i][j] - bMatrix[i][j]
            } else {
                resultMatrix[i][j] = aMatrix[i][j] + bMatrix[i][j]
            }
        }
    }
    return resultMatrix
}

fun matrixMinus(aMatrix: Array<Array<Double>>, bMatrix: Array<Array<Double>>): Array<Array<Double>> = matrixAdd(aMatrix, bMatrix, true)

fun Array<Array<Double>>.add(bMatrix: Array<Array<Double>>): Array<Array<Double>> = matrixAdd(this, bMatrix)

fun Array<Array<Double>>.minus(bMatrix: Array<Array<Double>>): Array<Array<Double>> = matrixMinus(this, bMatrix)

fun matrixMultiply(aMatrix: Array<Double>, bMatrix: Array<Array<Double>>): Array<Double> {
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray()
    }

    val resultMatrix = Array(bMatrix[0].size) { 0.0 }
    for (x in resultMatrix.indices) {
        for (bY in bMatrix.indices) {
            resultMatrix[x] += aMatrix[bY] * bMatrix[bY][x]
        }
    }
    return resultMatrix
}

fun matrixMultiply(aMatrix: Array<Array<Double>>, bMatrix: Array<Array<Double>>): Array<Array<Double>> {
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray()
    }
    val resultMatrix = Array(aMatrix.size) { Array(bMatrix[0].size) { 0.0 } }
    for (y in resultMatrix.indices) {
        resultMatrix[y] = aMatrix[y].multiply(bMatrix)
    }
    return resultMatrix
}

fun Array<Double>.multiply(bMatrix: Array<Array<Double>>): Array<Double> = matrixMultiply(this, bMatrix)

fun Array<Array<Double>>.multiply(bMatrix: Array<Array<Double>>): Array<Array<Double>> = matrixMultiply(this, bMatrix)

fun Array<Double>.innerProduct(bMatrix: Array<Double>): Double {
    if (this.isEmpty() || bMatrix.isEmpty()) {
        return 0.0
    }
    if (this.size != bMatrix.size) {
        error("matrix size not match")
    }
    return this.sumByDoubleIndexed { index, item ->
        item * bMatrix[index]
    }
}

fun Array<Array<Double>>.innerProduct(bMatrix: Array<Array<Double>>): Double {
    if (this.isEmpty() || bMatrix.isEmpty()) {
        return 0.0
    }
    if (this.size != bMatrix.size || this[0].size != bMatrix[0].size) {
        error("matrix size not match")
    }
    return this.sumByDoubleIndexed { rowIndex, row ->
        row.sumByDoubleIndexed { columnIndex, item ->
            item * bMatrix[rowIndex][columnIndex]
        }
    }
}

fun main() {
    val aMatrix = arrayOf(arrayOf(1.0, 3.0, 2.0), arrayOf(4.0, 0.0, 1.0))
    val bMatrix = arrayOf(arrayOf(1.0, 3.0), arrayOf(0.0, 1.0), arrayOf(5.0, 2.0))
    val resultMatrix = matrixMultiply(aMatrix, bMatrix)
    resultMatrix.forEach { row ->
        row.forEach {
            print(it.toString() + Constants.String.TAB_STRING)
        }
        println()
    }
}