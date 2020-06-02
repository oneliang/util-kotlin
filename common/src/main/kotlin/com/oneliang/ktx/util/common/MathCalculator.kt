package com.oneliang.ktx.util.common

object MathCalculator {
    enum class CheckType(val value: Int) {
        RANGE_LEFT_OPEN_RIGHT_OPEN(0),//(,)
        RANGE_LEFT_OPEN_RIGHT_CLOSED(1),//(,]
        RANGE_LEFT_CLOSED_RIGHT_OPEN(2),//[,)
        RANGE_LEFT_CLOSED_RIGHT_CLOSED(3)//[,]
    }

    enum class ParameterCheckType(val value: Int) {
        PERCENT(0), DIFF(1)
    }

    fun checkValue(value: Double, compareValue: Double, parameterArray: Array<Double>, checkType: CheckType, parameterCheckType: ParameterCheckType): Boolean {
        if (parameterArray.isEmpty() || parameterArray.size != 2) {
            error("match rule parameter size error, $checkType need two parameter")
        }
        val left = when (parameterCheckType) {
            ParameterCheckType.PERCENT -> value * parameterArray[0]
            else -> value + parameterArray[0]
        }
        val right = when (parameterCheckType) {
            ParameterCheckType.PERCENT -> value * parameterArray[1]
            else -> value + parameterArray[1]
        }
        when (checkType) {
            CheckType.RANGE_LEFT_OPEN_RIGHT_OPEN -> {
                if (!(left < compareValue && compareValue < right)) {
                    return false
                }
            }
            CheckType.RANGE_LEFT_OPEN_RIGHT_CLOSED -> {
                if (!(left < compareValue && compareValue <= right)) {
                    return false
                }
            }
            CheckType.RANGE_LEFT_CLOSED_RIGHT_OPEN -> {
                if (!(left <= compareValue && compareValue < right)) {
                    return false
                }
            }
            CheckType.RANGE_LEFT_CLOSED_RIGHT_CLOSED -> {
                if (!(left <= compareValue && compareValue <= right)) {
                    return false
                }
            }
        }
        return true
    }

    enum class Operate(val value: String, val regex: String) {
        ADD("ADD", "^ADD\\(([\\w]+)\\+([\\w]+)\\)$"),
        MINUS("MINUS", "^MINUS\\(([\\w]+)\\-([\\w]+)\\)$"),
        MULTIPLY("MULTIPLY", "^MULTIPLY\\(([\\w]+)\\*([\\w]+)\\)$"),
        DIVIDE("DIVIDE", "^DIVIDE\\(([\\w]+)/([\\w]+)\\)$")
    }

    fun operateValue(dataMap: Map<String, String>, operateString: String): Double {
        when {
            operateString.startsWith(Operate.ADD.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.ADD.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = keyList[0]
                    val secondDataKey = keyList[1]
                    dataMap[firstDataKey].toDoubleSafely() + dataMap[secondDataKey].toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.MINUS.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.MINUS.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = keyList[0]
                    val secondDataKey = keyList[1]
                    dataMap[firstDataKey].toDoubleSafely() - dataMap[secondDataKey].toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.MULTIPLY.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.MULTIPLY.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = keyList[0]
                    val secondDataKey = keyList[1]
                    dataMap[firstDataKey].toDoubleSafely() * dataMap[secondDataKey].toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.DIVIDE.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.DIVIDE.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = keyList[0]
                    val secondDataKey = keyList[1]
                    dataMap[firstDataKey].toDoubleSafely() / dataMap[secondDataKey].toDoubleSafely()
                }
            }
        }
        return 0.0
    }
}