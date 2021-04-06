package com.oneliang.ktx.util.math.matrix

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.doubleIteration
import com.oneliang.ktx.util.common.sumByDoubleIndexed

fun matrixOperate(
    aMatrix: Array<Array<Double>>,
    bMatrix: Array<Array<Double>>,
    results: Array<Array<Double>>? = null,
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result },
    operate: (aValue: Double, bValue: Double) -> Double
): Pair<Array<Array<Double>>, Double> {
    var result = 0.0
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Array<Double>>() to result
    }
    if (aMatrix.size != bMatrix.size || aMatrix[0].size != bMatrix[0].size) {
        error("this size not equal matrix size, this size:%s, matrix size:%s, this[0] size:%s, matrix[0] size:%s".format(aMatrix.size, bMatrix.size, aMatrix[0].size, bMatrix[0].size))
    }

    val newResults = results ?: Array(aMatrix.size) { Array(aMatrix[0].size) { 0.0 } }
    for (i in newResults.indices) {
        for (j in newResults[i].indices) {
            newResults[i][j] = operate(aMatrix[i][j], bMatrix[i][j])
            result = resultOperate(result, newResults[i][j])
        }
    }
    return newResults to result
}

fun Array<Array<Double>>.operate(
    bMatrix: Array<Array<Double>>,
    results: Array<Array<Double>>? = null,
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result },
    operate: (aValue: Double, bValue: Double) -> Double
): Pair<Array<Array<Double>>, Double> = matrixOperate(this, bMatrix, results, resultOperate, operate)

fun matrixAdd(
    aMatrix: Array<Array<Double>>,
    bMatrix: Array<Array<Double>>,
    results: Array<Array<Double>>? = null
): Array<Array<Double>> = matrixOperate(aMatrix, bMatrix, results, operate = { aValue, bValue -> aValue + bValue }).first

fun matrixMinus(
    aMatrix: Array<Array<Double>>,
    bMatrix: Array<Array<Double>>,
    results: Array<Array<Double>>? = null
): Array<Array<Double>> = matrixOperate(aMatrix, bMatrix, results, operate = { aValue, bValue -> aValue - bValue }).first

fun Array<Array<Double>>.add(bMatrix: Array<Array<Double>>, results: Array<Array<Double>>? = null): Array<Array<Double>> = matrixAdd(this, bMatrix, results)

fun Array<Array<Double>>.minus(bMatrix: Array<Array<Double>>, results: Array<Array<Double>>? = null): Array<Array<Double>> = matrixMinus(this, bMatrix, results)

fun matrixMultiply(
    aMatrix: Array<Double>,
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it },
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result }
): Pair<Array<Double>, Double> {
    var result = 0.0
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Double>() to result
    }

    val resultMatrix = Array(bMatrix[0].size) { 0.0 }
    for (column in resultMatrix.indices) {
        for (bRow in bMatrix.indices) {
            resultMatrix[column] += aMatrix[bRow] * bMatrix[bRow][column]
        }
        resultMatrix[column] = transform(resultMatrix[column])//transform result
        result = resultOperate(result, resultMatrix[column])
    }
    return resultMatrix to result
}

fun matrixMultiply(
    aMatrix: Array<Array<Double>>,
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it },
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result }
): Pair<Array<Array<Double>>, Double> {
    var result = 0.0
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Array<Double>>() to result
    }
    val resultMatrix = Array(aMatrix.size) { Array(bMatrix[0].size) { 0.0 } }
    for (row in resultMatrix.indices) {
        val (subResultMatrix, subResult) = aMatrix[row].multiply(bMatrix, transform, resultOperate)
        resultMatrix[row] = subResultMatrix
        result = resultOperate(result, subResult)
    }
    return resultMatrix to result
}

fun Array<Double>.multiply(
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it },
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result }
): Pair<Array<Double>, Double> = matrixMultiply(this, bMatrix, transform, resultOperate)

fun Array<Double>.multiply(
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it }
): Array<Double> = matrixMultiply(this, bMatrix, transform).first

fun Array<Double>.multiplyAndOperate(
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it },
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result }
): Double = matrixMultiply(this, bMatrix, transform, resultOperate).second

fun Array<Array<Double>>.multiply(
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it },
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result }
): Pair<Array<Array<Double>>, Double> = matrixMultiply(this, bMatrix, transform, resultOperate)

fun Array<Array<Double>>.multiply(
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it }
): Array<Array<Double>> = matrixMultiply(this, bMatrix, transform).first

fun Array<Array<Double>>.multiplyAndOperate(
    bMatrix: Array<Array<Double>>,
    transform: (result: Double) -> Double = { it },
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result }
): Double = matrixMultiply(this, bMatrix, transform, resultOperate).second

fun Array<Array<Double>>.dotMultiply(
    bMatrix: Array<Array<Double>>,
    results: Array<Array<Double>>? = null,
    resultOperate: (result: Double, matrixValue: Double) -> Double = { result, _ -> result },
): Pair<Array<Array<Double>>, Double> = matrixOperate(this, bMatrix, results, resultOperate) { aValue, bValue -> aValue * bValue }

fun Array<Array<Double>>.dotMultiply(
    bMatrix: Array<Array<Double>>,
    results: Array<Array<Double>>? = null
): Array<Array<Double>> = matrixOperate(this, bMatrix, results, operate = { aValue, bValue -> aValue * bValue }).first

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

fun Array<Array<Double>>.innerOperate(bMatrix: Array<Array<Double>>, rowOffset: Int = 0, columnOffset: Int = 0, operate: (aValue: Double, bValue: Double) -> Double): Double {
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
        result += operate(this[rowIndex + rowOffset][columnIndex + columnOffset], bMatrix[rowIndex][columnIndex])
    }
    return result
}

fun Array<Array<Double>>.innerProduct(bMatrix: Array<Array<Double>>, rowOffset: Int = 0, columnOffset: Int = 0): Double = this.innerOperate(bMatrix, rowOffset, columnOffset) { aValue: Double, bValue: Double -> aValue * bValue }

fun Array<Array<Double>>.scaleToSmall(scale: Int): Array<Array<Double>> {
    if (this.size % scale != 0 || this[0].size % scale != 0) {
        error("size/scale is not a integer, this size:%s, this[0].size:%s, scale:%s".format(this.size, this[0].size, scale))
    }
    val rows = this.size / scale
    val columns = this[0].size / scale
    val result = Array(rows) { Array(columns) { 0.0 } }
    doubleIteration(rows, columns) { row, column ->
        doubleIteration(scale, scale) { x, y ->
            result[row][column] += this[row * scale + x][column * scale + y] / scale / scale
        }
    }
    return result
}

fun Array<Array<Double>>.scaleToBig(scale: Int): Array<Array<Double>> {
    if (scale <= 0) {
        error("scale must bigger than zero, scale:%s".format(scale))
    }
    val rows = this.size * scale
    val columns = this[0].size * scale
    val result = Array(rows) { Array(columns) { 0.0 } }
    doubleIteration(this.size, this[0].size) { row, column ->
        doubleIteration(scale, scale) { x, y ->
            result[row * scale + x][column * scale + y] = this[row][column]
        }
    }
    return result
}

fun Array<Array<Double>>.rotate180(results: Array<Array<Double>>? = null): Array<Array<Double>> {
    val rows = this.size
    val columns = this[0].size
    val newResults = results ?: Array(rows) { Array(columns) { 0.0 } }
    doubleIteration(rows / 2 + 1, columns / 2 + 1) { row, column ->
        newResults[row][column] = this[rows - row - 1][columns - column - 1]
        newResults[row][columns - column - 1] = this[rows - row - 1][column]
        newResults[rows - row - 1][column] = this[row][columns - column - 1]
        newResults[rows - row - 1][columns - column - 1] = this[row][column]
    }
    return newResults
}

fun Array<Array<Double>>.operate(transform: Array<Array<Double>>.(row: Int, column: Int, value: Double) -> Double): Array<Array<Double>> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return this
    }
    doubleIteration(this.size, this[0].size) { row, column ->
        this[row][column] = transform(row, column, this[row][column])
    }
    return this
}

fun main() {
//    f:-0.733928,w:1.0
//    f:9.098687,w:1.0
//    f:1.0,w:1.0
//    val aMatrix = arrayOf(-0.733928, 9.098687, 1.0)
    val aMatrix = arrayOf(arrayOf(2.0, 2.0), arrayOf(2.0, 2.0))
    val bMatrix = arrayOf(arrayOf(1.0, 2.0), arrayOf(1.0, 2.0))
//    val resultMatrix = matrixMultiply(aMatrix, bMatrix)
    val resultMatrix = aMatrix.scaleToSmall(2)
    resultMatrix.forEach { row ->
        row.forEach {
            print(it.toString() + Constants.String.TAB_STRING)
        }
        println(row)
    }
//    println(aMatrix.innerProduct(bMatrix, 1, 1))
}