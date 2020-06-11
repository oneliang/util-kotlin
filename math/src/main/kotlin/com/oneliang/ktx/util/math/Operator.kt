package com.oneliang.ktx.util.math

import com.oneliang.ktx.util.common.parseRegexGroup
import com.oneliang.ktx.util.common.toDoubleSafely

object Operator {

    enum class Operate(val value: String, val regex: String) {
        ADD("ADD", "^ADD\\(([\\w]+)\\+([\\w]+)\\)$"),
        MINUS("MINUS", "^MINUS\\(([\\w]+)\\-([\\w]+)\\)$"),
        MULTIPLY("MULTIPLY", "^MULTIPLY\\(([\\w]+)\\*([\\w]+)\\)$"),
        DIVIDE("DIVIDE", "^DIVIDE\\(([\\w]+)/([\\w]+)\\)$")
    }

    fun <K : Any, V : Any> operate(dataMap: Map<K, V>, operateString: String, operateKeyTransform: (key: String) -> K): Double {
        when {
            operateString.startsWith(Operate.ADD.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.ADD.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = operateKeyTransform(keyList[0])
                    val secondDataKey = operateKeyTransform(keyList[1])
                    dataMap[firstDataKey].toString().toDoubleSafely() + dataMap[secondDataKey].toString().toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.MINUS.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.MINUS.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = operateKeyTransform(keyList[0])
                    val secondDataKey = operateKeyTransform(keyList[1])
                    dataMap[firstDataKey].toString().toDoubleSafely() - dataMap[secondDataKey].toString().toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.MULTIPLY.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.MULTIPLY.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = operateKeyTransform(keyList[0])
                    val secondDataKey = operateKeyTransform(keyList[1])
                    dataMap[firstDataKey].toString().toDoubleSafely() * dataMap[secondDataKey].toString().toDoubleSafely()
                }
            }
            operateString.startsWith(Operate.DIVIDE.value) -> {
                val keyList = operateString.parseRegexGroup(Operate.DIVIDE.regex)
                return if (keyList.size != 2) {
                    0.0
                } else {
                    val firstDataKey = operateKeyTransform(keyList[0])
                    val secondDataKey = operateKeyTransform(keyList[1])
                    dataMap[firstDataKey].toString().toDoubleSafely() / dataMap[secondDataKey].toString().toDoubleSafely()
                }
            }
        }
        return 0.0
    }
}