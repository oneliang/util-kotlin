package com.oneliang.ktx.util.math

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.*

object Stater {
    enum class StatFunction(val value: String, val regex: String) {
        NONE("NONE", "^NONE\\(([\\w]+)\\)$"),
        COUNT("COUNT", "^COUNT\\(([\\w]+)\\)$"),
        DISTINCT("DISTINCT", "^DISTINCT\\(([\\w]+)\\)$"),
        SUM("SUM", "^SUM\\(([\\w]+)\\)$")
    }

    class StatKey {
        var newKey = Constants.String.BLANK
        var function = Constants.String.BLANK
        var format = Constants.String.BLANK
    }

    class Result {
        companion object

        var value = Constants.String.BLANK
        var valueSet = hashSetOf<String>()
        var function = Constants.String.BLANK
    }

    internal fun generateNoneStatResult(): Result {
        return Result.build(Constants.String.BLANK, function = StatFunction.NONE.value)
    }

    fun <K, V> stat(dataMap: Map<K, V>, functionString: String, statKeyTransform: (statKey: String) -> K): Result {
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
                    val value = dataMap[dataStatKey]
                    if (value == null) {
                        Result.build(valueSet = hashSetOf(), function = StatFunction.DISTINCT.value)
                    } else {
                        Result.build(valueSet = hashSetOf(value.toString()), function = StatFunction.DISTINCT.value)
                    }
                }
            }
            functionString.startsWith(StatFunction.SUM.value) -> {
                val keyList = functionString.parseRegexGroup(StatFunction.SUM.regex)
                if (keyList.size != 1) {
                    generateNoneStatResult()
                } else {
                    val dataStatKey = statKeyTransform(keyList[0])
                    val value = dataMap[dataStatKey]
                    if (value == null) {
                        Result.build(value = Constants.String.ZERO, function = StatFunction.SUM.value)
                    } else {
                        Result.build(value = value.toString(), function = StatFunction.SUM.value)
                    }
                }
            }
            else -> {
                generateNoneStatResult()
            }
        }
    }

    fun <K, V> stat(dataMap: Map<K, V>, statKeyArray: Array<StatKey>, statKeyTransform: (statKey: String) -> K): Map<String, Result> {
        val statResultMap = mutableMapOf<String, Result>()
        statKeyArray.forEach { statKey ->
            statResultMap[statKey.newKey] = stat(dataMap, statKey.function, statKeyTransform)
        }
        return statResultMap
    }

    fun <K, V> stat(dataMapIterable: Iterable<Map<K, V>>, statKeyArray: Array<StatKey>, statKeyTransform: (statKey: String) -> K): Map<String, Result> {
        val statResultMap = mutableMapOf<String, Result>()
        val statKeyMap = statKeyArray.toMapBy { it.newKey }
        dataMapIterable.forEach { dataMap ->
            val currentStatResultMap = stat(dataMap, statKeyArray, statKeyTransform)
            statResultMap.merge(currentStatResultMap, statKeyMap)
        }
        return statResultMap
    }
}

fun Stater.Result.Companion.build(value: String = Constants.String.BLANK, valueSet: HashSet<String> = hashSetOf(), function: String): Stater.Result {
    return Stater.Result().also {
        it.value = value
        it.valueSet = valueSet
        it.function = function
    }
}

fun Stater.Result.Companion.mergeToNew(currentResult: Stater.Result, previousResult: Stater.Result, format: String = Constants.String.BLANK): Stater.Result {
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

fun MutableMap<String, Stater.Result>.merge(statResultMap: Map<String, Stater.Result>, statKeyMap: Map<String, Stater.StatKey>) {
    statResultMap.forEach { (statResultKey, statResult) ->
        val statKey = statKeyMap[statResultKey] ?: return@forEach //no include in stat key map
        val originalStatResult = this[statResultKey]
        this[statResultKey] = if (originalStatResult == null) {
            statResult
        } else {
            Stater.Result.mergeToNew(statResult, originalStatResult, statKey.format)
        }
    }
}