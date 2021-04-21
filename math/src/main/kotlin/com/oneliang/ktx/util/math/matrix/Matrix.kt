package com.oneliang.ktx.util.math.matrix

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.doubleIteration
import com.oneliang.ktx.util.common.singleIteration

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

    if (aMatrix.size != bMatrix.size) {//aMatrix columns must equal b matrix rows
        error("matrix size not match, a matrix column size:%s, b matrix row size:%s".format(aMatrix.size, bMatrix.size))
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

    if (aMatrix[0].size != bMatrix.size) {//aMatrix columns must equal b matrix rows
        error("matrix size not match, a matrix column size:%s, b matrix row size:%s".format(aMatrix[0].size, bMatrix.size))
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

fun Array<Double>.innerProduct(bArray: Array<Double>, columnOffset: Int = 0): Double {
    if (this.isEmpty() || bArray.isEmpty()) {
        return 0.0
    }
    if (columnOffset > (this.size - bArray.size)) {
        error("column offset out of range, index:%s, this size:%s, array size:%s".format(columnOffset, this.size, bArray.size))
    }
    var result = 0.0
    singleIteration(bArray.size) { columnIndex ->
        result += this[columnIndex + columnOffset] * bArray[columnIndex]
    }
    return result
}

fun Array<Array<Double>>.innerOperate(bMatrix: Array<Array<Double>>, rowOffset: Int = 0, columnOffset: Int = 0, operate: (aValue: Double, bValue: Double) -> Double): Double {
    if (this.isEmpty() || this[0].isEmpty()) {
        error("this matrix is empty")
    }
    if (bMatrix.isEmpty() || bMatrix[0].isEmpty()) {
        error("b matrix is empty")
    }
    if (this.size < bMatrix.size || this[0].size < bMatrix[0].size) {
        error("this size is smaller than matrix size, this size:%s, matrix size:%s, this[0] size:%s, matrix[0] size:%s".format(this.size, bMatrix.size, this[0].size, bMatrix[0].size))
    }
    if (rowOffset > (this.size - bMatrix.size)) {
        error("row offset out of range, index:%s, this size:%s, matrix size:%s".format(rowOffset, this.size, bMatrix.size))
    }
    if (columnOffset > (this[0].size - bMatrix[0].size)) {
        error("column offset out of range, index:%s, this[0] size:%s, matrix[0] size:%s".format(columnOffset, this[0].size, bMatrix[0].size))
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

fun Array<Array<Double>>.kroneckerProduct(bMatrix: Array<Array<Double>>): Array<Array<Double>> {
    if (this.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Array<Double>>()
    }
    val bRows = bMatrix.size
    val bColumns = bMatrix[0].size
    val newRows = this.size * bRows
    val newColumns = this[0].size * bColumns
    val results = Array(newRows) { Array(newColumns) { 0.0 } }

    doubleIteration(this.size, this[0].size) { aRow, aColumn ->
        doubleIteration(bMatrix.size, bMatrix[0].size) { bRow, bColumn ->
            results[aRow * bRows + bRow][aColumn * bColumns + bColumn] = this[aRow][aColumn] * bMatrix[bRow][bColumn]
        }
    }
    return results
}

fun Array<Double>.transpose(operate: (value: Double) -> Double = { it }): Array<Array<Double>> {
    val results = Array(this.size) { Array(1) { 0.0 } }
    singleIteration(results.size) { row ->
        results[row][0] = operate(this[row])
    }
    return results
}

fun Array<Array<Double>>.transpose(operate: (value: Double) -> Double = { it }): Array<Array<Double>> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return this
    }
    val results = Array(this[0].size) { Array(this.size) { 0.0 } }
    doubleIteration(results.size, results[0].size) { row, column ->
        results[row][column] = operate(this[column][row])
    }
    return results
}

fun main() {
//    f:-0.733928,w:1.0
//    f:9.098687,w:1.0
//    f:1.0,w:1.0
//    val aMatrix = arrayOf(-0.733928, 9.098687, 1.0)
    val aMatrix = arrayOf(arrayOf(2.0, 2.0), arrayOf(2.0, 2.0))
    val bMatrix = arrayOf(arrayOf(1.0, 2.0), arrayOf(1.0, 2.0))
//    val resultMatrix = matrixMultiply(aMatrix, bMatrix)
    var resultMatrix = aMatrix.scaleToSmall(2)
    resultMatrix.forEach { row ->
        row.forEach {
            print(it.toString() + Constants.String.TAB_STRING)
        }
        println(row)
    }
//    println(aMatrix.innerProduct(bMatrix, 1, 1))
    println("-----kronecker product-----")
    val kaMatrix = arrayOf(arrayOf(1.0, 2.0), arrayOf(3.0, 4.0))
    val kbMatrix = arrayOf(arrayOf(1.0, 3.0, 2.0), arrayOf(2.0, 4.0, 6.0))
    resultMatrix = kaMatrix.kroneckerProduct(kbMatrix)
    resultMatrix.forEach { row ->
        row.forEach {
            print(it.toString() + Constants.String.TAB_STRING)
        }
        println()
    }
    println("-----transpose-----")
    resultMatrix = kbMatrix.transpose()
    resultMatrix.forEach { row ->
        row.forEach {
            print(it.toString() + Constants.String.TAB_STRING)
        }
        println()
    }
}