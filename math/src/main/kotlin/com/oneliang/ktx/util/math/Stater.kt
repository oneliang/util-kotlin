package com.oneliang.ktx.util.math

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.nullToBlank
import com.oneliang.ktx.util.common.parseRegexGroup
import com.oneliang.ktx.util.common.toDoubleSafely
import com.oneliang.ktx.util.common.toIntSafely

object Stater {
    enum class StatFunction(val value: String, val regex: String) {
        NONE("NONE", "^NONE\\(([\\w]+)\\)$"),
        COUNT("COUNT", "^COUNT\\(([\\w]+)\\)$"),
        DISTINCT("DISTINCT", "^DISTINCT\\(([\\w]+)\\)$"),
        SUM("SUM", "^SUM\\(([\\w]+)\\)$")
    }

    class Result {
        companion object

        var value = Constants.String.BLANK
        var valueSet = hashSetOf<String>()
        var function = Constants.String.BLANK
    }

    private fun generateNoneStatResult(): Result {
        return Result.build(Constants.String.BLANK, function = StatFunction.NONE.value)
    }

    fun <K : Any, V : Any> stat(dataMap: Map<K, V>, functionString: String, statKeyTransform: (statKey: String) -> K): Result {
        return when {
            functionString.startsWith(StatFunction.COUNT.value) -> {
                val keyList = functionString.parseRegexGroup(StatFunction.COUNT.regex)
                if (keyList.isEmpty()) {
                    generateNoneStatResult()
                } else {
                    Result.build(value = 1.toString(), function = StatFunction.COUNT.value)
                }
            }
            functionString.startsWith(StatFunction.DISTINCT.value) -> {
                val keyList = functionString.parseRegexGroup(StatFunction.DISTINCT.regex)
                if (keyList.size != 1) {
                    generateNoneStatResult()
                } else {
                    val dataStatKey = statKeyTransform(keyList[0])
                    Result.build(valueSet = hashSetOf(dataMap[dataStatKey].toString().nullToBlank()), function = StatFunction.DISTINCT.value)
                }
            }
            functionString.startsWith(StatFunction.SUM.value) -> {
                val keyList = functionString.parseRegexGroup(StatFunction.SUM.regex)
                if (keyList.size != 1) {
                    generateNoneStatResult()
                } else {
                    val dataStatKey = statKeyTransform(keyList[0])
                    Result.build(value = dataMap[dataStatKey]?.toString() ?: Constants.String.ZERO, function = StatFunction.SUM.value)
                }
            }
            else -> {
                generateNoneStatResult()
            }
        }
    }
}

fun Stater.Result.Companion.build(value: String = Constants.String.BLANK, valueSet: HashSet<String> = hashSetOf(), function: String): Stater.Result {
    return Stater.Result().also {
        it.value = value
        it.valueSet = valueSet
        it.function = function
    }
}

fun Stater.Result.Companion.merge(currentResult: Stater.Result, previousResult: Stater.Result, format: String = Constants.String.BLANK): Stater.Result {
    return Stater.Result().also {
        it.function = previousResult.function
        when {
            it.function.startsWith(Stater.StatFunction.COUNT.value) -> {
                it.value = (currentResult.value.toIntSafely() + previousResult.value.toIntSafely()).toString()
            }
            it.function.startsWith(Stater.StatFunction.DISTINCT.value) -> {
                val hashSet = hashSetOf<String>()
                hashSet.addAll(currentResult.valueSet)
                hashSet.addAll(previousResult.valueSet)
                it.valueSet = hashSet
            }
            it.function.startsWith(Stater.StatFunction.SUM.value) -> {
                val sumResult = currentResult.value.toDoubleSafely() + previousResult.value.toDoubleSafely()
                it.value = if (format.isBlank()) {
                    sumResult.toString()
                } else {
                    format.format(sumResult)
                }
            }
        }
    }
}