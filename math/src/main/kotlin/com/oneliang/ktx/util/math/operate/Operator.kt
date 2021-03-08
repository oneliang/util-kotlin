package com.oneliang.ktx.util.math.operate

import com.oneliang.ktx.util.common.parseRegexGroup
import com.oneliang.ktx.util.common.toDoubleSafely

object Operator {

    enum class Operate(val value: String, val regex: String) {
        ADD("ADD", "^ADD\\(([\\w]+)\\+([\\w]+)\\)$"),
        MINUS("MINUS", "^MINUS\\(([\\w]+)\\-([\\w]+)\\)$"),
        MULTIPLY("MULTIPLY", "^MULTIPLY\\(([\\w]+)\\*([\\w]+)\\)$"),
        DIVIDE("DIVIDE", "^DIVIDE\\(([\\w]+)/([\\w]+)\\)$")
    }

    fun <K : Any, V : Any, R : Any> operate(dataMap: Map<K, V>, operateString: String, operateKeyTransform: (key: String) -> K, operateValueTransform: (value: V) -> R): Double {
        when {
            operateString.startsWith(Operate.ADD.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.ADD.regex)
                val parseKeyPair = parseKeyList(dataMap, keyList, operateKeyTransform, operateValueTransform)
                return if (parseKeyPair == null) {
                    0.0
                } else {
                    parseKeyPair.first.toString().toDoubleSafely() + parseKeyPair.second.toString().toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.MINUS.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.MINUS.regex)
                val parseKeyPair = parseKeyList(dataMap, keyList, operateKeyTransform, operateValueTransform)
                return if (parseKeyPair == null) {
                    0.0
                } else {
                    parseKeyPair.first.toString().toDoubleSafely() - parseKeyPair.second.toString().toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.MULTIPLY.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.MULTIPLY.regex)
                val parseKeyPair = parseKeyList(dataMap, keyList, operateKeyTransform, operateValueTransform)
                return if (parseKeyPair == null) {
                    0.0
                } else {
                    parseKeyPair.first.toString().toDoubleSafely() * parseKeyPair.second.toString().toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.DIVIDE.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.DIVIDE.regex)
                val parseKeyPair = parseKeyList(dataMap, keyList, operateKeyTransform, operateValueTransform)
                return if (parseKeyPair == null) {
                    0.0
                } else {
                    parseKeyPair.first.toString().toDoubleSafely() / parseKeyPair.second.toString().toDoubleSafely()
                }
            }
        }
        return 0.0
    }

    private fun <K : Any, V : Any, R : Any> parseKeyList(dataMap: Map<K, V>, keyList: List<String>, operateKeyTransform: (key: String) -> K, operateValueTransform: (value: V) -> R): Pair<R, R>? {
        if (keyList.size != 2) {
            return null
        } else {
            val firstDataKey = operateKeyTransform(keyList[0])
            val secondDataKey = operateKeyTransform(keyList[1])
            val firstDataValue = dataMap[firstDataKey] ?: return null
            val secondDataValue = dataMap[secondDataKey] ?: return null
            val fixFirstDataValue = operateValueTransform(firstDataValue)
            val fixSecondDataValue = operateValueTransform(secondDataValue)
            return fixFirstDataValue to fixSecondDataValue
        }
    }
}