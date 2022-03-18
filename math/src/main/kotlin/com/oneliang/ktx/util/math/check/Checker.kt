package com.oneliang.ktx.util.math.check

object Checker {
    enum class CheckType(val value: Int) {
        RANGE_LEFT_OPEN_RIGHT_OPEN(0),//(,)
        RANGE_LEFT_OPEN_RIGHT_CLOSED(1),//(,]
        RANGE_LEFT_CLOSED_RIGHT_OPEN(2),//[,)
        RANGE_LEFT_CLOSED_RIGHT_CLOSED(3)//[,]
    }

    enum class ParameterCheckType(val value: Int) {
        PERCENT(0), DIFF(1)
    }

    fun check(value: Double, consultValue: Double, parameters: Array<Double>, checkType: CheckType, parameterCheckType: ParameterCheckType): Boolean {
        if (parameters.isEmpty() || parameters.size != 2) {
            error("match rule parameter size error, $checkType need two parameter")
        }
        val left = when (parameterCheckType) {
            ParameterCheckType.PERCENT -> value * parameters[0]
            else -> value + parameters[0]
        }
        val right = when (parameterCheckType) {
            ParameterCheckType.PERCENT -> value * parameters[1]
            else -> value + parameters[1]
        }
        when (checkType) {
            CheckType.RANGE_LEFT_OPEN_RIGHT_OPEN -> {
                if (!(left < consultValue && consultValue < right)) {
                    return false
                }
            }
            CheckType.RANGE_LEFT_OPEN_RIGHT_CLOSED -> {
                if (!(left < consultValue && consultValue <= right)) {
                    return false
                }
            }
            CheckType.RANGE_LEFT_CLOSED_RIGHT_OPEN -> {
                if (!(left <= consultValue && consultValue < right)) {
                    return false
                }
            }
            CheckType.RANGE_LEFT_CLOSED_RIGHT_CLOSED -> {
                if (!(left <= consultValue && consultValue <= right)) {
                    return false
                }
            }
        }
        return true
    }
}