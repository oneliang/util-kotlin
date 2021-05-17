package com.oneliang.ktx.util.math.matrix

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.doubleIteration
import com.oneliang.ktx.util.common.singleIteration
import java.util.stream.IntStream

fun matrixOperate(
    aMatrix: Array<Array<Float>>,
    bMatrix: Array<Array<Float>>,
    results: Array<Array<Float>>? = null,
    parallel: Boolean = false,
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result },
    operate: (aValue: Float, bValue: Float) -> Float
): Pair<Array<Array<Float>>, Float> {
    var result = 0.0f
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Array<Float>>() to result
    }
    if (aMatrix.size != bMatrix.size || aMatrix[0].size != bMatrix[0].size) {
        error("this size not equal matrix size, this size:%s, matrix size:%s, this[0] size:%s, matrix[0] size:%s".format(aMatrix.size, bMatrix.size, aMatrix[0].size, bMatrix[0].size))
    }

    val newResults = results ?: Array(aMatrix.size) { Array(aMatrix[0].size) { 0.0f } }
    val dataProcessor: (i: Int, j: Int) -> Unit = { i, j ->
        newResults[i][j] = operate(aMatrix[i][j], bMatrix[i][j])
        result = resultOperate(result, newResults[i][j])
    }
    if (parallel) {
        IntStream.range(0, newResults.size).parallel().forEach { i ->
            IntStream.range(0, newResults[i].size).parallel().forEach { j ->
                dataProcessor(i, j)
            }
        }
    } else {
        singleIteration(newResults.size) { i ->
            singleIteration(newResults[i].size) { j ->
                dataProcessor(i, j)
            }
        }
    }
    return newResults to result
}

fun Array<Array<Float>>.operate(
    bMatrix: Array<Array<Float>>,
    results: Array<Array<Float>>? = null,
    parallel: Boolean = false,
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result },
    operate: (aValue: Float, bValue: Float) -> Float
): Pair<Array<Array<Float>>, Float> = matrixOperate(this, bMatrix, results, parallel, resultOperate, operate)

fun matrixAdd(
    aMatrix: Array<Array<Float>>,
    bMatrix: Array<Array<Float>>,
    results: Array<Array<Float>>? = null,
    parallel: Boolean = false
): Array<Array<Float>> = matrixOperate(aMatrix, bMatrix, results, parallel, operate = { aValue, bValue -> aValue + bValue }).first

fun matrixMinus(
    aMatrix: Array<Array<Float>>,
    bMatrix: Array<Array<Float>>,
    results: Array<Array<Float>>? = null,
    parallel: Boolean = false
): Array<Array<Float>> = matrixOperate(aMatrix, bMatrix, results, parallel, operate = { aValue, bValue -> aValue - bValue }).first

fun Array<Array<Float>>.add(bMatrix: Array<Array<Float>>, results: Array<Array<Float>>? = null, parallel: Boolean = false): Array<Array<Float>> = matrixAdd(this, bMatrix, results, parallel)

fun Array<Array<Float>>.minus(bMatrix: Array<Array<Float>>, results: Array<Array<Float>>? = null, parallel: Boolean = false): Array<Array<Float>> = matrixMinus(this, bMatrix, results, parallel)

fun matrixMultiply(
    aMatrix: Array<Float>,
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it },
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result }
): Pair<Array<Float>, Float> {
    var result = 0.0f
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Float>() to result
    }

    if (aMatrix.size != bMatrix.size) {//aMatrix columns must equal b matrix rows
        error("matrix size not match, a matrix column size:%s, b matrix row size:%s".format(aMatrix.size, bMatrix.size))
    }

    val resultMatrix = Array(bMatrix[0].size) { 0.0f }
    if (parallel) {
        IntStream.range(0, resultMatrix.size).parallel().forEach { column ->
            singleIteration(bMatrix.size) { bRow ->
                resultMatrix[column] += aMatrix[bRow] * bMatrix[bRow][column]
            }
            resultMatrix[column] = transform(resultMatrix[column])//transform result
            result = resultOperate(result, resultMatrix[column])
        }
    } else {
        singleIteration(resultMatrix.size) { column ->
            singleIteration(bMatrix.size) { bRow ->
                resultMatrix[column] += aMatrix[bRow] * bMatrix[bRow][column]
            }
            resultMatrix[column] = transform(resultMatrix[column])//transform result
            result = resultOperate(result, resultMatrix[column])
        }
    }
//    for (column in resultMatrix.indices) {
//        for (bRow in bMatrix.indices) {
//            resultMatrix[column] += aMatrix[bRow] * bMatrix[bRow][column]
//        }
//        resultMatrix[column] = transform(resultMatrix[column])//transform result
//        result = resultOperate(result, resultMatrix[column])
//    }
    return resultMatrix to result
}

fun matrixMultiply(
    aMatrix: Array<Array<Float>>,
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it },
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result }
): Pair<Array<Array<Float>>, Float> {
    var result = 0.0f
    if (aMatrix.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Array<Float>>() to result
    }

    if (aMatrix[0].size != bMatrix.size) {//aMatrix columns must equal b matrix rows
        error("matrix size not match, a matrix column size:%s, b matrix row size:%s".format(aMatrix[0].size, bMatrix.size))
    }

    val resultMatrix = Array(aMatrix.size) { Array(bMatrix[0].size) { 0.0f } }
    val rowProcessor: (i: Int) -> Unit = { row ->
        val (subResultMatrix, subResult) = aMatrix[row].multiply(bMatrix, parallel, transform, resultOperate)
        resultMatrix[row] = subResultMatrix
        result = resultOperate(result, subResult)
    }
    if (parallel) {
        IntStream.range(0, resultMatrix.size).parallel().forEach(rowProcessor)
    } else {
        singleIteration(resultMatrix.size, rowProcessor)
    }
    return resultMatrix to result
}

fun Array<Float>.multiply(
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it },
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result }
): Pair<Array<Float>, Float> = matrixMultiply(this, bMatrix, parallel, transform, resultOperate)

fun Array<Float>.multiply(
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it }
): Array<Float> = matrixMultiply(this, bMatrix, parallel, transform).first

fun Array<Float>.multiplyAndOperate(
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it },
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result }
): Float = matrixMultiply(this, bMatrix, parallel, transform, resultOperate).second

fun Array<Array<Float>>.multiply(
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it },
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result }
): Pair<Array<Array<Float>>, Float> = matrixMultiply(this, bMatrix, parallel, transform, resultOperate)

fun Array<Array<Float>>.multiply(
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it }
): Array<Array<Float>> = matrixMultiply(this, bMatrix, parallel, transform).first

fun Array<Array<Float>>.multiplyAndOperate(
    bMatrix: Array<Array<Float>>,
    parallel: Boolean = false,
    transform: (result: Float) -> Float = { it },
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result }
): Float = matrixMultiply(this, bMatrix, parallel, transform, resultOperate).second

fun Array<Array<Float>>.dotMultiply(
    bMatrix: Array<Array<Float>>,
    results: Array<Array<Float>>? = null,
    parallel: Boolean = false,
    resultOperate: (result: Float, matrixValue: Float) -> Float = { result, _ -> result },
): Pair<Array<Array<Float>>, Float> = matrixOperate(this, bMatrix, results, parallel, resultOperate) { aValue, bValue -> aValue * bValue }

fun Array<Array<Float>>.dotMultiply(
    bMatrix: Array<Array<Float>>,
    results: Array<Array<Float>>? = null
): Array<Array<Float>> = matrixOperate(this, bMatrix, results, operate = { aValue, bValue -> aValue * bValue }).first

fun Array<Float>.innerProduct(bArray: Array<Float>, columnOffset: Int = 0): Float {
    if (this.isEmpty() || bArray.isEmpty()) {
        return 0.0f
    }
    if (columnOffset > (this.size - bArray.size)) {
        error("column offset out of range, index:%s, this size:%s, array size:%s".format(columnOffset, this.size, bArray.size))
    }
    var result = 0.0f
    singleIteration(bArray.size) { columnIndex ->
        result += this[columnIndex + columnOffset] * bArray[columnIndex]
    }
    return result
}

fun Array<Array<Float>>.innerOperate(bMatrix: Array<Array<Float>>, rowOffset: Int = 0, columnOffset: Int = 0, operate: (aValue: Float, bValue: Float) -> Float): Float {
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
    var result = 0.0f
    doubleIteration(bMatrix.size, bMatrix[0].size) { rowIndex, columnIndex ->
        result += operate(this[rowIndex + rowOffset][columnIndex + columnOffset], bMatrix[rowIndex][columnIndex])
    }
    return result
}

fun Array<Array<Float>>.innerProduct(bMatrix: Array<Array<Float>>, rowOffset: Int = 0, columnOffset: Int = 0): Float = this.innerOperate(bMatrix, rowOffset, columnOffset) { aValue: Float, bValue: Float -> aValue * bValue }

fun Array<Array<Float>>.scaleToSmall(scale: Int): Array<Array<Float>> {
    if (this.size % scale != 0 || this[0].size % scale != 0) {
        error("size/scale is not a integer, this size:%s, this[0].size:%s, scale:%s".format(this.size, this[0].size, scale))
    }
    val rows = this.size / scale
    val columns = this[0].size / scale
    val result = Array(rows) { Array(columns) { 0.0f } }
    doubleIteration(rows, columns) { row, column ->
        doubleIteration(scale, scale) { x, y ->
            result[row][column] += this[row * scale + x][column * scale + y] / scale / scale
        }
    }
    return result
}

fun Array<Array<Float>>.scaleToBig(scale: Int): Array<Array<Float>> {
    if (scale <= 0) {
        error("scale must bigger than zero, scale:%s".format(scale))
    }
    val rows = this.size * scale
    val columns = this[0].size * scale
    val result = Array(rows) { Array(columns) { 0.0f } }
    doubleIteration(this.size, this[0].size) { row, column ->
        doubleIteration(scale, scale) { x, y ->
            result[row * scale + x][column * scale + y] = this[row][column]
        }
    }
    return result
}

fun Array<Array<Float>>.rotate180(results: Array<Array<Float>>? = null): Array<Array<Float>> {
    val rows = this.size
    val columns = this[0].size
    val newResults = results ?: Array(rows) { Array(columns) { 0.0f } }
    doubleIteration(rows / 2 + 1, columns / 2 + 1) { row, column ->
        newResults[row][column] = this[rows - row - 1][columns - column - 1]
        newResults[row][columns - column - 1] = this[rows - row - 1][column]
        newResults[rows - row - 1][column] = this[row][columns - column - 1]
        newResults[rows - row - 1][columns - column - 1] = this[row][column]
    }
    return newResults
}

fun Array<Array<Float>>.operate(transform: Array<Array<Float>>.(row: Int, column: Int, value: Float) -> Float): Array<Array<Float>> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return this
    }
    doubleIteration(this.size, this[0].size) { row, column ->
        this[row][column] = transform(row, column, this[row][column])
    }
    return this
}

fun Array<Array<Float>>.kroneckerProduct(bMatrix: Array<Array<Float>>): Array<Array<Float>> {
    if (this.isEmpty() || bMatrix.isEmpty()) {
        return emptyArray<Array<Float>>()
    }
    val bRows = bMatrix.size
    val bColumns = bMatrix[0].size
    val newRows = this.size * bRows
    val newColumns = this[0].size * bColumns
    val results = Array(newRows) { Array(newColumns) { 0.0f } }

    doubleIteration(this.size, this[0].size) { aRow, aColumn ->
        doubleIteration(bMatrix.size, bMatrix[0].size) { bRow, bColumn ->
            results[aRow * bRows + bRow][aColumn * bColumns + bColumn] = this[aRow][aColumn] * bMatrix[bRow][bColumn]
        }
    }
    return results
}

fun Array<Float>.transpose(operate: (value: Float) -> Float = { it }): Array<Array<Float>> {
    val results = Array(this.size) { Array(1) { 0.0f } }
    singleIteration(results.size) { row ->
        results[row][0] = operate(this[row])
    }
    return results
}

fun Array<Array<Float>>.transpose(operate: (value: Float) -> Float = { it }): Array<Array<Float>> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return this
    }
    val results = Array(this[0].size) { Array(this.size) { 0.0f } }
    doubleIteration(results.size, results[0].size) { row, column ->
        results[row][column] = operate(this[column][row])
    }
    return results
}

fun main() {
//    f:-0.733928,w:1.0f
//    f:9.098687,w:1.0f
//    f:1.0f,w:1.0f
//    val aMatrix = arrayOf(-0.733928, 9.098687, 1.0f)
//    val aMatrix = arrayOf(arrayOf(2.0, 2.0), arrayOf(2.0, 2.0))
//    val bMatrix = arrayOf(arrayOf(1.0f, 2.0), arrayOf(1.0f, 2.0))
//    val resultMatrix = matrixMultiply(aMatrix, bMatrix)
//    var resultMatrix = aMatrix.scaleToSmall(2)
//    resultMatrix.forEach { row ->
//        row.forEach {
//            print(it.toString() + Constants.String.TAB_STRING)
//        }
//        println(row)
//    }
//    println(aMatrix.innerProduct(bMatrix, 1, 1))
//    println("-----kronecker product-----")
//    val kaMatrix = arrayOf(arrayOf(1.0f, 2.0), arrayOf(3.0, 4.0))
//    val kbMatrix = arrayOf(arrayOf(1.0f, 3.0, 2.0), arrayOf(2.0, 4.0, 6.0))
//    resultMatrix = kaMatrix.kroneckerProduct(kbMatrix)
//    resultMatrix.forEach { row ->
//        row.forEach {
//            print(it.toString() + Constants.String.TAB_STRING)
//        }
//        println()
//    }
//    println("-----transpose-----")
//    resultMatrix = kbMatrix.transpose()
//    resultMatrix.forEach { row ->
//        row.forEach {
//            print(it.toString() + Constants.String.TAB_STRING)
//        }
//        println()
//    }
    println("-----single multiply double-----")
    val oneDMatrix = arrayOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f)
    val towDMatrix = arrayOf(
        arrayOf(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(3.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(4.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(5.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(6.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(7.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(8.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(9.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f),
        arrayOf(10.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f)
    )
    val a = oneDMatrix.multiply(towDMatrix)
    a.forEach {
        print(it.toString() + Constants.String.TAB_STRING)
    }
}