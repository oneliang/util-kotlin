package com.oneliang.ktx.util.math.matrix

import com.oneliang.ktx.util.common.doubleIteration
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

fun matrixMultiply(aMatrix: Array<Double>, bMatrix: Array<Array<Double>>, transform: (result: Double) -> Double = { it }): Array<Double> {
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray()
    }

    val resultMatrix = Array(bMatrix[0].size) { 0.0 }
    for (x in resultMatrix.indices) {
        for (bY in bMatrix.indices) {
            resultMatrix[x] += aMatrix[bY] * bMatrix[bY][x]
        }
        resultMatrix[x] = transform(resultMatrix[x])//transform result
    }
    return resultMatrix
}

fun matrixMultiply(aMatrix: Array<Array<Double>>, bMatrix: Array<Array<Double>>, transform: (result: Double) -> Double = { it }): Array<Array<Double>> {
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray()
    }
    val resultMatrix = Array(aMatrix.size) { Array(bMatrix[0].size) { 0.0 } }
    for (y in resultMatrix.indices) {
        resultMatrix[y] = aMatrix[y].multiply(bMatrix, transform)
    }
    return resultMatrix
}

fun Array<Double>.multiply(bMatrix: Array<Array<Double>>, transform: (result: Double) -> Double = { it }): Array<Double> = matrixMultiply(this, bMatrix, transform)

fun Array<Array<Double>>.multiply(bMatrix: Array<Array<Double>>, transform: (result: Double) -> Double = { it }): Array<Array<Double>> = matrixMultiply(this, bMatrix, transform)

fun Array<Double>.innerProduct(bMatrix: Array<Double>, columnOffset: Int = 0): Double {
    if (this.isEmpty() || bMatrix.isEmpty()) {
        return 0.0
    }
    if ((this.size - columnOffset) != bMatrix.size) {
        error("matrix size not match, this size:%s, column offset:%s, matrix size:%s".format(this.size, columnOffset, bMatrix.size))
    }
    return this.sumByDoubleIndexed { index, item ->
        item * bMatrix[index]
    }
}

fun Array<Array<Double>>.innerProduct(bMatrix: Array<Array<Double>>, rowOffset: Int = 0, columnOffset: Int = 0): Double {
    if (this.isEmpty() || bMatrix.isEmpty()) {
        return 0.0
    }
    if (this.size < bMatrix.size || this[0].size < bMatrix[0].size) {
        error("this size is smaller than matrix size, this size:%s, matrix size:%s, this[0] size:%s, matrix[0] size:%s".format(this.size, bMatrix.size, this[0].size, bMatrix[0].size))
    }
    if (rowOffset > (this.size - bMatrix.size)) {
        error("row offset out of range, index:%s, this size:%s, matrix size:%s".format(rowOffset, this.size, bMatrix.size))
    }
    if (columnOffset > (this[0].size - bMatrix[0].size)) {
        error("row offset out of range, index:%s, this size:%s, matrix size:%s".format(rowOffset, this.size, bMatrix.size))
    }
    var result = 0.0
    doubleIteration(bMatrix.size, bMatrix[0].size) { rowIndex, columnIndex ->
        result += this[rowIndex + rowOffset][columnIndex + columnOffset] * bMatrix[rowIndex][columnIndex]
    }
    return result
}

fun main() {
//    f:-0.733928,w:1.0
//    f:9.098687,w:1.0
//    f:1.0,w:1.0
//    val aMatrix = arrayOf(-0.733928, 9.098687, 1.0)
    val aMatrix = arrayOf(arrayOf(1.0, 2.0, 3.0), arrayOf(1.0, 2.0, 3.0), arrayOf(1.0, 2.0, 3.0))
    val bMatrix = arrayOf(arrayOf(1.0, 2.0), arrayOf(1.0, 2.0))
    val resultMatrix = matrixMultiply(aMatrix, bMatrix)
//    resultMatrix.forEach { row ->
//        row.forEach {
//            print(it.toString() + Constants.String.TAB_STRING)
//        }
//        println(row)
//    }
    println(aMatrix.innerProduct(bMatrix,1,1))

}