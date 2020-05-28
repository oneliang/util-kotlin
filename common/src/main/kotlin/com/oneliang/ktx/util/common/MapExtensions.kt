package com.oneliang.ktx.util.common

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
        keyList += if (valueArray.isEmpty()) {
            dataKey
        } else {//is not empty then check value
            val dataValue = this[dataKey]
            if (valueArray.contains(dataValue)) {
                dataKey
            } else {
                return emptyList()
            }
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

fun <K, V> Map<K, V>.differs(map: Map<K, V>, valueComparator: (key: K, value: V, mapValue: V) -> Boolean = { key, value, mapValue -> value == mapValue }): List<K> {
    val list = mutableListOf<K>()
    this.forEach { (key, value) ->
        val mapValue = map[key]
        if (mapValue == null || !valueComparator(key, value, mapValue)) {
            list += key
        }
    }
    return list
}

fun <K, V> Map<K, V>.sameAs(map: Map<K, V>, valueComparator: (key: K, value: V, mapValue: V) -> Boolean = { key, value, mapValue -> value == mapValue }): Boolean = this.size == map.size && this.differs(map, valueComparator).isEmpty()

fun <K, V> Map<K, V>.includes(map: Map<K, V>, valueComparator: (key: K, value: V, mapValue: V) -> Boolean = { key, value, mapValue -> value == mapValue }): Boolean = map.differs(this, valueComparator).isEmpty()

fun <K, V> Map<K, V>.matches(map: Map<K, V>): Boolean = this.includes(map)

inline fun <K, reified V> Map<K, V>.toArray(indexMapping: Map<K, Int>, defaultValue: V): Array<V> = this.toArray(indexMapping, defaultValue) { _, value -> value }

inline fun <K, V, reified R> Map<K, V>.toArray(indexMapping: Map<K, Int>, defaultValue: R, transform: (key: K, value: V) -> R): Array<R> {
    val array = Array(this.size) { defaultValue }
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