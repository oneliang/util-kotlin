package com.oneliang.ktx.util.common

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

inline fun <T, K, V> Iterable<T>.toMap(transform: (t: T) -> Pair<K, V>): Map<K, V> = this.associate(transform)

inline fun <T, K, V> Iterable<T>.toMapWithIndex(transform: (index: Int, t: T) -> Pair<K, V>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    this.forEachIndexed { index, t ->
        map += transform(index, t)
    }
    return map
}

fun Iterable<String>.toByteArray(charset: Charset = Charsets.UTF_8): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.forEach {
        byteArrayOutputStream.write(it.toByteArray(charset))
        byteArrayOutputStream.flush()
    }
    return byteArrayOutputStream.toByteArray()
}

inline fun <T, K> Iterable<T>.toKeyListAndMap(keySelector: (t: T) -> K): Pair<List<K>, Map<K, T>> {
    return this.toKeyListAndMap(keySelector, keySelector)
}

inline fun <T, K, MK> Iterable<T>.toKeyListAndMap(keySelector: (t: T) -> K, mapKeySelector: (t: T) -> MK): Pair<List<K>, Map<MK, T>> {
    val keyList = mutableListOf<K>()
    val map = mutableMapOf<MK, T>()
    this.forEach {
        val key = keySelector(it)
        val mapKey = mapKeySelector(it)
        keyList += key
        map[mapKey] = it
    }
    return keyList to map
}

inline fun <T, K> Iterable<T>.toKeySetAndGroupBy(keySelector: (t: T) -> K): Pair<Set<K>, Map<K, List<T>>> {
    return this.toKeySetAndGroupBy(keySelector, keySelector)
}

inline fun <T, K, MK> Iterable<T>.toKeySetAndGroupBy(keySelector: (t: T) -> K, mapKeySelector: (t: T) -> MK): Pair<Set<K>, Map<MK, List<T>>> {
    val keySet = mutableSetOf<K>()
    val map = mutableMapOf<MK, MutableList<T>>()
    this.forEach {
        val key = keySelector(it)
        val mapKey = mapKeySelector(it)
        keySet += key
        val list = map.getOrPut(mapKey) { mutableListOf() }
        list += it
    }
    return keySet to map
}

inline fun <T, K, R> Iterable<T>.groupByWithIndex(keySelector: (T) -> K, valueTransform: (index: Int, T) -> R) = groupByToWithIndex(mutableMapOf(), keySelector, valueTransform)

inline fun <T, K, R, M : MutableMap<in K, MutableList<R>>> Iterable<T>.groupByToWithIndex(destination: M, keySelector: (T) -> K, valueTransform: (index: Int, T) -> R): M {
    this.forEachIndexed { index: Int, element: T ->
        val key = keySelector(element)
        val list = destination.getOrPut(key) { mutableListOf() }
        list.add(valueTransform(index, element))
    }
    return destination
}

inline fun <T, K, V> Iterable<T>.matchInMap(matchKeySelector: (t: T) -> K, sourceMap: Map<K, V>, ifMatch: (t: T, sourceMapItem: V) -> Unit, ifNotMatch: (t: T) -> Unit = {}) {
    this.forEach {
        val key = matchKeySelector(it)
        val value = sourceMap[key]
        if (value != null) {
            ifMatch(it, value)
        } else {
            ifNotMatch(it)
        }
    }
}

inline fun <K, V, R> Iterable<Map<K, V>>.matchesByThenTransform(keyValueArrayMap: Map<K, Array<V>>, transform: (keyList: List<K>, itemMap: Map<K, V>) -> R): Iterable<R> {
    val list = mutableListOf<R>()
    this.forEach {
        val keyList = it.matchesBy(keyValueArrayMap)
        if (keyList.isEmpty()) return@forEach
        list += transform(keyList, it)
    }
    return list
}

fun <T> Iterable<T>.differs(compareIterable: Iterable<T>, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): List<T> {
    val map = this.toMap { it to it }
    val compareMap = compareIterable.toMap { it to it }
    return map.differs(compareMap) { _: T, value: T, mapValue: T ->
        valueComparator(value, mapValue)
    }
}