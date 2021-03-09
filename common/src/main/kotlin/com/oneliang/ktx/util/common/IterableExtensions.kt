package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.nio.charset.Charset


inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMap(destinationMap: M, transform: (item: T) -> Pair<K, V>): M = this.associateTo(destinationMap, transform)

inline fun <T, K, V> Iterable<T>.toMap(transform: (item: T) -> Pair<K, V>): Map<K, V> = this.toMap(mutableMapOf(), transform)

inline fun <T, K, M : MutableMap<in K, in T>> Iterable<T>.toMapBy(destinationMap: M, keySelector: (item: T) -> K): M = this.associateByTo(destinationMap, keySelector)

inline fun <T, K> Iterable<T>.toMapBy(keySelector: (item: T) -> K): Map<K, T> = this.toMapBy(mutableMapOf(), keySelector)

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithFilter(destinationMap: M, filter: (item: T) -> Boolean, transform: (item: T) -> Pair<K, V>): M {
    for (element in this) {
        if (filter(element)) {
            destinationMap += transform(element)
        }
    }
    return destinationMap
}

inline fun <T, K, V> Iterable<T>.toMapWithFilter(filter: (item: T) -> Boolean, transform: (item: T) -> Pair<K, V>): Map<K, V> = this.toMapWithFilter(mutableMapOf(), filter, transform)

inline fun <T, K, V> Iterable<T>.toMapWithIndex(transform: (index: Int, item: T) -> Pair<K, V>): Map<K, V> {
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

inline fun <T, K> Iterable<T>.toKeyListAndMap(keySelector: (item: T) -> K): Pair<List<K>, Map<K, T>> {
    return this.toKeyListAndMap(keySelector, keySelector)
}

inline fun <T, K, MK> Iterable<T>.toKeyListAndMap(keySelector: (item: T) -> K, mapKeySelector: (item: T) -> MK): Pair<List<K>, Map<MK, T>> {
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

inline fun <T, K> Iterable<T>.toKeySetAndGroupBy(keySelector: (item: T) -> K): Pair<Set<K>, Map<K, List<T>>> {
    return this.toKeySetAndGroupBy(keySelector, keySelector)
}

inline fun <T, K, MK> Iterable<T>.toKeySetAndGroupBy(keySelector: (item: T) -> K, mapKeySelector: (item: T) -> MK): Pair<Set<K>, Map<MK, List<T>>> {
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

inline fun <T, K, R, M : MutableMap<in K, MutableList<R>>> Iterable<T>.groupByToWithIndex(destination: M, keySelector: (item: T) -> K, valueTransform: (index: Int, T) -> R): M {
    this.forEachIndexed { index: Int, element: T ->
        val key = keySelector(element)
        val list = destination.getOrPut(key) { mutableListOf() }
        list += valueTransform(index, element)
    }
    return destination
}

inline fun <T, K, V> Iterable<T>.matchInMap(matchKeySelector: (item: T) -> K, sourceMap: Map<K, V>, ifMatch: (item: T, sourceMapItem: V) -> Unit, ifNotMatch: (item: T) -> Unit = {}) {
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
    return this.differs(compareIterable, { it }, valueComparator)
}

fun <T, K> Iterable<T>.differs(compareIterable: Iterable<T>, keySelector: (value: T) -> K, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): List<T> {
    val map = this.toMap { keySelector(it) to it }
    val compareMap = compareIterable.toMap { keySelector(it) to it }
    val keyList = map.differs(compareMap) { _: K, value: T, mapValue: T ->
        valueComparator(value, mapValue)
    }
    val valueList = mutableListOf<T>()
    keyList.forEach {
        val value = map[it] ?: return@forEach//continue, impossible null
        valueList += value
    }
    return valueList
}

inline fun <T, R> Iterable<T>.toHashSet(transform: (item: T) -> R): Set<R> {
    val hashSet = HashSet<R>()
    this.forEach {
        hashSet += transform(it)
    }
    return hashSet
}

inline fun <T, R : Any> Iterable<T>.mapWithFilter(filter: (item: T) -> Boolean, transform: (item: T) -> R): List<R> {
    return this.mapNotNull {
        if (!filter(it)) {
            null
        } else {
            transform(it)
        }
    }
}

inline fun <T> Iterable<T>.sumByDoubleIndexed(selector: (index: Int, item: T) -> Double): Double {
    var sum = 0.0
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByLong(selector: (item: T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByLongIndexed(selector: (index: Int, item: T) -> Long): Long {
    var sum = 0L
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByBigDecimal(selector: (item: T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal(0)
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByBigDecimalIndexed(selector: (index: Int, item: T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal(0)
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
}

inline fun <V, K, RV, RK, T> Iterable<V>.relateBy(keySelector: (V) -> K, slaveIterable: Iterable<RV>, slaveKeySelector: (RV) -> RK, relationList: List<T>, relationDataKeySelector: (T) -> K, relationDataSlaveKeySelector: (T) -> RK): Map<V, List<RV>> {
    val mainMap = this.toMap { keySelector(it) to it }
    val relationMap = slaveIterable.toMap { slaveKeySelector(it) to it }
    return mainMap.relateBy(relationMap, relationList, relationDataKeySelector, relationDataSlaveKeySelector)
}

inline fun <T, K, ST> Iterable<T>.fillWithSlaveIterable(keySelector: (T) -> K, slaveIterable: Iterable<ST>, slaveIterableMasterKeySelector: (ST) -> K, block: (T, List<ST>) -> Unit) {
    val masterIdSlaveListMap = slaveIterable.groupBy { slaveIterableMasterKeySelector(it) }
    this.forEach {
        val key = keySelector(it)
        val slaveList = masterIdSlaveListMap[key] ?: return@forEach//condition
        block(it, slaveList)
    }
}

fun <T, K> Iterable<T>.groupByMultiKeySelector(keySelectorArray: Array<(item: T) -> K>): Array<Map<K, List<T>>> {
    val mapArray = Array<MutableMap<K, MutableList<T>>>(keySelectorArray.size) { mutableMapOf() }
    this.forEach { element: T ->
        mapArray.forEachIndexed { index, mutableMap ->
            val key = keySelectorArray[index](element)
            val list = mutableMap.getOrPut(key) { mutableListOf() }
            list += element
        }
    }
    return mapArray.toNewArray { it }
}