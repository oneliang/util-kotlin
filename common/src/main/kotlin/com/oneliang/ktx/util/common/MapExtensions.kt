package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import com.oneliang.ktx.exception.MethodInvokeException
import kotlin.reflect.KClass

fun <T : Any> Map<String, Array<String>>.toObject(instance: T, classProcessor: KotlinClassUtil.KotlinClassProcessor = KotlinClassUtil.DEFAULT_KOTLIN_CLASS_PROCESSOR) {
    if (this.isEmpty()) {
        return
    }
    val methods = instance.javaClass.methods
    for (method in methods) {
        val methodName = method.name
        val fieldName = ObjectUtil.setMethodNameToFieldName(methodName)
        if (fieldName.isBlank()) {
            continue
        }
        if (!this.containsKey(fieldName)) {
            continue
        }
        val values = this[fieldName] ?: continue
        val classes = method.parameterTypes
        if (classes.size == 1) {
            val value = KotlinClassUtil.changeType(classes[0].kotlin, values, fieldName, classProcessor)
            try {
                method.invoke(instance, value)
            } catch (t: Throwable) {
                throw MethodInvokeException(t)
            }
        }
    }
}

@Throws(Exception::class)
fun <T : Any> Map<String, Array<String>>.toObjectList(kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = KotlinClassUtil.DEFAULT_KOTLIN_CLASS_PROCESSOR): List<T> {
    if (this.isEmpty()) {
        return emptyList()
    }
    val methods = kClass.java.methods
    val list = mutableListOf<T>()
    for (method in methods) {
        val methodName = method.name
        val fieldName = ObjectUtil.setMethodNameToFieldName(methodName)
        if (fieldName.isBlank()) {
            continue
        }
        if (!this.containsKey(fieldName)) {
            continue
        }
        val values = this[fieldName] ?: continue
        val classes = method.parameterTypes
        if (classes.size != 1) {
            continue
        }
        for ((i, parameterValue) in values.withIndex()) {
            val instance: T
            if (i < list.size) {
                instance = list[i]
            } else {
                try {
                    instance = kClass.java.newInstance()
                    list.add(instance)
                } catch (t: Throwable) {
                    throw t
                }
            }
            val value = KotlinClassUtil.changeType(classes[0].kotlin, arrayOf(parameterValue), fieldName, classProcessor)
            try {
                method.invoke(instance, value)
            } catch (t: Throwable) {
                throw MethodInvokeException(t)
            }
        }
    }
    return list
}

fun <K, V, R> Map<K, V>.toList(filter: (key: K, value: V) -> Boolean = { _, _ -> true }, transform: (key: K, value: V) -> R): List<R> {
    val list = mutableListOf<R>()
    this.forEach { (key, value) ->
        if (filter(key, value)) {
            list.add(transform(key, value))
        }
    }
    return list
}

fun <K, V> Map<K, V>.matchesBy(keyValueArrayMap: Map<K, Array<V>>): List<K> {
    val keyList = mutableListOf<K>()
    for (keyValueArray in keyValueArrayMap) {
        val (dataKey, valueArray) = keyValueArray
        if (this.containsKey(dataKey)) {//contains the key
            keyList += if (valueArray.isEmpty()) {//if value array is empty, success
                dataKey
            } else {
                val dataValue = this[dataKey]
                if (valueArray.contains(dataValue)) {
                    dataKey
                } else {
                    return emptyList()
                }
            }
        } else {//not include data key, so do not match
            return emptyList()
        }
    }
    return keyList
}

inline fun <K, V> Map<K, V>.forEachWithIndex(block: (index: Int, key: K, value: V) -> Unit) {
    var index = 0
    this.forEach { (key, value) ->
        block(index, key, value)
        index++
    }
}

fun <K, V> Map<K, V>.differs(map: Map<K, V>, valueComparator: (key: K, value: V, mapValue: V) -> Boolean = { _, value, mapValue -> value == mapValue }): List<K> {
    val list = mutableListOf<K>()
    this.forEach { (key, value) ->
        val mapValue = map[key]
        if (mapValue == null || !valueComparator(key, value, mapValue)) {
            list += key
        }
    }
    return list
}

fun <K, V> Map<K, V>.sameAs(map: Map<K, V>, valueComparator: (key: K, value: V, mapValue: V) -> Boolean = { _, value, mapValue -> value == mapValue }): Boolean = this.size == map.size && this.differs(map, valueComparator).isEmpty()

fun <K, V> Map<K, V>.includes(map: Map<K, V>, valueComparator: (key: K, value: V, mapValue: V) -> Boolean = { _, value, mapValue -> value == mapValue }): Boolean = map.differs(this, valueComparator).isEmpty()

fun <K, V> Map<K, V>.matches(map: Map<K, V>, valueComparator: (key: K, value: V, mapValue: V) -> Boolean = { _, value, mapValue -> value == mapValue }): Boolean = this.includes(map, valueComparator)

inline fun <K, reified V> Map<K, V>.toArray(indexMapping: Map<K, Int>, defaultValue: V): Array<V> = this.toArray(indexMapping, defaultValue) { _, value -> value }

inline fun <K, V, reified R> Map<K, V>.toArray(indexMapping: Map<K, Int>, defaultValue: R, transform: (key: K, value: V) -> R): Array<R> = this.toArray(indexMapping.size, indexMapping, defaultValue, transform)

inline fun <K, V, reified R> Map<K, V>.toArray(arrayMaxSize: Int, indexMapping: Map<K, Int>, defaultValue: R, transform: (key: K, value: V) -> R): Array<R> {
    val array = Array(arrayMaxSize) { defaultValue }
    this.forEach { (key, value) ->
        val index = indexMapping[key] ?: return@forEach//continue
        array[index] = transform(key, value)
    }
    return array
}

fun <K, V, NK, NV> Map<K, V>.toMap(transform: (key: K, value: V) -> Pair<NK, NV>): Map<NK, NV> {
    val mutableMap = mutableMapOf<NK, NV>()
    this.forEach { (key, value) ->
        mutableMap += transform(key, value)
    }
    return mutableMap
}

fun <K, V, NK> Map<K, V>.toMapWithNewKey(transformKey: (key: K) -> NK): Map<NK, V> = this.toMap { key, value -> transformKey(key) to value }

fun <K, V, NV> Map<K, V>.toMapWithNewValue(transformValue: (value: V) -> NV): Map<K, NV> = this.toMap { key, value -> key to transformValue(value) }

fun <K, V, RK, RV, T> Map<K, V>.relateBy(slaveMap: Map<RK, RV>, relationList: List<T>, relationDataKeySelector: (T) -> K, relationDataSlaveKeySelector: (T) -> RK): Map<V, List<RV>> {
    val map = mutableMapOf<V, MutableList<RV>>()
    relationList.forEach {
        val key = relationDataKeySelector(it)
        val slaveKey = relationDataSlaveKeySelector(it)
        val value = this[key] ?: return@forEach
        val list = map.getOrPut(value) { mutableListOf() }
        val relationValue = slaveMap[slaveKey]
        if (relationValue != null) {
            list += relationValue
        }
    }
    return map
}

fun <K, V, NK> Map<K, V>.groupByKey(keySelector: (K) -> NK): Map<NK, Map<K, V>> {
    return this.groupByKeyTo(keySelector = keySelector)
}

fun <K, V, NK> Map<K, V>.groupByKeyTo(destinationMap: MutableMap<NK, MutableMap<K, V>> = mutableMapOf(), keySelector: (K) -> NK): Map<NK, Map<K, V>> {
    this.forEach { (key, value) ->
        val newKey = keySelector(key)
        val valueMap = destinationMap.getOrPut(newKey) { mutableMapOf() }
        valueMap += key to value
    }
    return destinationMap
}

fun <K, V> Map<out K, V>.joinToString(separator: CharSequence = Constants.Symbol.COMMA, prefix: CharSequence = Constants.String.BLANK, postfix: CharSequence = Constants.String.BLANK, limit: Int = -1, truncated: CharSequence = "...", transform: ((K, V) -> CharSequence)? = null): String {
    return joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()
}

fun <K, V, A : Appendable> Map<out K, V>.joinTo(buffer: A, separator: CharSequence = Constants.Symbol.COMMA, prefix: CharSequence = Constants.String.BLANK, postfix: CharSequence = Constants.String.BLANK, limit: Int = -1, truncated: CharSequence = "...", transform: ((K, V) -> CharSequence)? = null): A {
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            if (transform != null)
                buffer.append(transform(element.key, element.value))
            else
                buffer.append(element.toString())
        } else break
    }
    if (limit in 0 until count) buffer.append(truncated)
    buffer.append(postfix)
    return buffer
}

fun <K, V> Map<K, V>.merge(mergeMap: Map<K, V>, existBlock: (key: K, value: V, mergeValue: V) -> V = { _, value, _ -> value },
                           mergeNotExistBlock: (key: K, value: V) -> V = { _, value -> value },
                           notExistBlock: (key: K, mergeValue: V) -> V = { _, mergeValue -> mergeValue }): Map<K, V> {
    return this.mergeToNewValue(mergeMap, existBlock, mergeNotExistBlock, notExistBlock)
}

fun <K, V, NV> Map<K, V>.mergeToNewValue(mergeMap: Map<K, V>, existBlock: (key: K, value: V, mergeValue: V) -> NV,
                                         mergeNotExistBlock: (key: K, value: V) -> NV,
                                         notExistBlock: (key: K, mergeValue: V) -> NV): Map<K, NV> {
    val mutableMap = mutableMapOf<K, NV>()
    this.forEach { (key, value) ->
        val mergeValue = mergeMap[key]
        val newValue = if (mergeValue != null) {
            existBlock(key, value, mergeValue)
        } else {
            mergeNotExistBlock(key, value)
        }
        mutableMap[key] = newValue
    }
    mergeMap.forEach { (key, mergeValue) ->
        val value = this[key]
        if (value == null) {
            val newValue = notExistBlock(key, mergeValue)
            mutableMap[key] = newValue
        } else {
            //it merged
        }
    }
    return mutableMap
}