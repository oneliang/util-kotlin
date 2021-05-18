package com.oneliang.ktx.util.math.tensor

import com.oneliang.ktx.util.common.doubleIteration
import com.oneliang.ktx.util.common.singleIteration

fun Array<Array<Array<Float>>>.innerOperate(bTensor: Array<Array<Array<Float>>>, depthOffset: Int = 0, rowOffset: Int = 0, columnOffset: Int = 0, operate: (aValue: Float, bValue: Float) -> Float): Float {
    if (this.isEmpty() || this[0].isEmpty() || this[0][0].isEmpty()) {
        error("this tensor is empty")
    }
    if (bTensor.isEmpty() || bTensor[0].isEmpty() || bTensor[0][0].isEmpty()) {
        error("b tensor is empty")
    }
    if (this.size < bTensor.size || this[0].size < bTensor[0].size || this[0][0].size < bTensor[0][0].size) {
        error("this size is smaller than tensor size, this size:%s, tensor size:%s, this[0] size:%s, tensor[0] size:%s, this[0][0] size:%s, tensor[0][0] size:%s".format(this.size, bTensor.size, this[0].size, bTensor[0].size, this[0][0].size, bTensor[0][0].size))
    }
    if (depthOffset > (this.size - bTensor.size)) {
        error("depth offset out of range, index:%s, this size:%s, tensor size:%s".format(depthOffset, this.size, bTensor.size))
    }
    if (rowOffset > (this[0].size - bTensor[0].size)) {
        error("row offset out of range, index:%s, this[0] size:%s, matrix[0] size:%s".format(rowOffset, this[0].size, bTensor[0].size))
    }
    if (columnOffset > (this[0][0].size - bTensor[0][0].size)) {
        error("column offset out of range, index:%s, this[0][0] size:%s, matrix[0][0] size:%s".format(columnOffset, this[0][0].size, bTensor[0][0].size))
    }
    var result = 0.0f
    singleIteration(bTensor.size) { depthIndex ->
        doubleIteration(bTensor[0].size, bTensor[0][0].size) { rowIndex, columnIndex ->
            result += operate(this[depthIndex + depthOffset][rowIndex + rowOffset][columnIndex + columnOffset], bTensor[depthIndex][rowIndex][columnIndex])
        }
    }
    return result
}

fun Array<Array<Array<Float>>>.innerProduct(bMatrix: Array<Array<Array<Float>>>, depthOffset: Int = 0, rowOffset: Int = 0, columnOffset: Int = 0): Float = this.innerOperate(bMatrix, depthOffset, rowOffset, columnOffset) { aValue: Float, bValue: Float -> aValue * bValue }