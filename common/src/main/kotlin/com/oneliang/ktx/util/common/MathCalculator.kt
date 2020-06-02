package com.oneliang.ktx.util.common

import com.oneliang.ktx.util.logging.LoggerManager
import java.util.*

object MathCalculator {
    enum class OperateType(val value: Int) {
        RANGE_LEFT_OPEN_RIGHT_OPEN(0),//(,)
        RANGE_LEFT_OPEN_RIGHT_CLOSED(1),//(,]
        RANGE_LEFT_CLOSED_RIGHT_OPEN(2),//[,)
        RANGE_LEFT_CLOSED_RIGHT_CLOSED(3)//[,]
    }

    enum class OperateParameterType(val value: Int) {
        PERCENT(0), DIFF(1)
    }

    fun checkValue(value: Double, compareValue: Double, parameterArray: Array<Double>, operateType: OperateType, operateParameterType: OperateParameterType): Boolean {
        if (parameterArray.isEmpty() || parameterArray.size != 2) {
            error("match rule parameter size error, $operateType need two parameter")
        }
        val left = when (operateParameterType) {
            OperateParameterType.PERCENT -> value * parameterArray[0]
            else -> value + parameterArray[0]
        }
        val right = when (operateParameterType) {
            OperateParameterType.PERCENT -> value * parameterArray[1]
            else -> value + parameterArray[1]
        }
        when (operateType) {
            OperateType.RANGE_LEFT_OPEN_RIGHT_OPEN -> {
                if (!(left < compareValue && compareValue < right)) {
                    return false
                }
            }
            OperateType.RANGE_LEFT_OPEN_RIGHT_CLOSED -> {
                if (!(left < compareValue && compareValue <= right)) {
                    return false
                }
            }
            OperateType.RANGE_LEFT_CLOSED_RIGHT_OPEN -> {
                if (!(left <= compareValue && compareValue < right)) {
                    return false
                }
            }
            OperateType.RANGE_LEFT_CLOSED_RIGHT_CLOSED -> {
                if (!(left <= compareValue && compareValue <= right)) {
                    return false
                }
            }
        }
        return true
    }
}