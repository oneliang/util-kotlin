package com.oneliang.ktx.util.common

import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashSet


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
    return this.toKeyListAndMap(keySelector, mapKeySelector) { it }
}

inline fun <T, K, MK, MV> Iterable<T>.toKeyListAndMap(keySelector: (item: T) -> K, mapKeySelector: (item: T) -> MK, mapValueTransform: (item: T) -> MV): Pair<List<K>, Map<MK, MV>> {
    val keyList = mutableListOf<K>()
    val map = mutableMapOf<MK, MV>()
    this.forEach {
        val key = keySelector(it)
        val mapKey = mapKeySelector(it)
        keyList += key
        map[mapKey] = mapValueTransform(it)
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

fun <T> Iterable<T>.differsAccurate(compareIterable: Iterable<T>, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): Pair<List<T>, List<T>> {
    return this.differsAccurate(compareIterable, { it }, valueComparator)
}

fun <T, K> Iterable<T>.differsAccurate(compareIterable: Iterable<T>, keySelector: (value: T) -> K, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): Pair<List<T>, List<T>> {
    val map = this.toMap { keySelector(it) to it }
    val compareMap = compareIterable.toMap { keySelector(it) to it }
    val (addKeyList, valueNotMatchKeyList) = map.differsAccurate(compareMap) { _: K, value: T, mapValue: T ->
        valueComparator(value, mapValue)
    }
    val addValueList = mutableListOf<T>()
    val valueNotMatchValueList = mutableListOf<T>()
    addKeyList.forEach {
        val value = map[it] ?: return@forEach//continue, impossible null
        addValueList += value
    }
    valueNotMatchKeyList.forEach {
        val value = map[it] ?: return@forEach//continue, impossible null
        valueNotMatchValueList += value
    }
    return addValueList to valueNotMatchValueList
}


inline fun <T, R> Iterable<T>.toHashSet(transform: (item: T) -> R): Set<R> {
    val hashSet = HashSet<R>()
    this.forEach {
        hashSet += transform(it)
    }
    return hashSet
}

inline fun <T, R> Iterable<T>.mapWithFilter(filter: (item: T) -> Boolean, transform: (item: T) -> R): List<R> {
    return this.mapNotNull {
        if (!filter(it)) {
            null
        } else {
            transform(it)
        }
    }
}

inline fun <T> Iterable<T>.sumByFloat(selector: (item: T) -> Float): Float {
    var sum = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByFloatIndexed(selector: (index: Int, item: T) -> Float): Float {
    var sum = 0.0f
    for ((index, element) in this.withIndex()) {
        sum += selector(index, element)
    }
    return sum
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

fun <T, K> Iterable<T>.groupByMultiKeySelector(keySelectors: Array<(item: T) -> K>): Array<Map<K, List<T>>> {
    val mapArray = Array<MutableMap<K, MutableList<T>>>(keySelectors.size) { mutableMapOf() }
    this.forEach { element: T ->
        mapArray.forEachIndexed { index, mutableMap ->
            val key = keySelectors[index](element)
            val list = mutableMap.getOrPut(key) { mutableListOf() }
            list += element
        }
    }
    return mapArray.toNewArray { it }
}

inline fun <T, K, R, SK> Iterable<T>.groupByDistinctWithIndex(keySelector: (item: T) -> K, subKeySelector: (item: T) -> SK, valueTransform: (index: Int, T) -> R): Map<K, Map<SK, R>> {
    return groupByDistinctToWithIndex(mutableMapOf(), keySelector, subKeySelector, valueTransform)
}

inline fun <T, K, R, SK, M : MutableMap<in K, MutableMap<SK, R>>> Iterable<T>.groupByDistinctToWithIndex(destination: M, keySelector: (item: T) -> K, subKeySelector: (item: T) -> SK, valueTransform: (index: Int, T) -> R): M {
    this.forEachIndexed { index: Int, element: T ->
        val key = keySelector(element)
        val subMap = destination.getOrPut(key) { mutableMapOf() }
        val subKey = subKeySelector(element)
        subMap[subKey] = valueTransform(index, element)
    }
    return destination
}

inline fun <T, K, R, SK> Iterable<T>.groupByDistinct(keySelector: (item: T) -> K, subKeySelector: (item: T) -> SK, valueTransform: (T) -> R): Map<K, Map<SK, R>> {
    return groupByDistinctTo(mutableMapOf(), keySelector, subKeySelector, valueTransform)
}

inline fun <T, K, R, SK, M : MutableMap<in K, MutableMap<SK, R>>> Iterable<T>.groupByDistinctTo(destination: M, keySelector: (item: T) -> K, subKeySelector: (item: T) -> SK, valueTransform: (T) -> R): M {
    this.forEach { element: T ->
        val key = keySelector(element)
        val subMap = destination.getOrPut(key) { mutableMapOf() }
        val subKey = subKeySelector(element)
        subMap[subKey] = valueTransform(element)
    }
    return destination
}

fun <T> Iterable<T>.countBySelf(): Map<T, Int> = this.countByKey { it }

fun <T, K> Iterable<T>.countByKey(keySelector: (item: T) -> K): Map<K, Int> = this.countByKeyTo(mutableMapOf(), keySelector)

fun <T, K, M : MutableMap<in K, Int>> Iterable<T>.countByKeyTo(destination: M, keySelector: (item: T) -> K): M {
    this.countByKeyAndCheckTo(destination, keySelector)
    return destination
}

fun <T, K> Iterable<T>.countByKeyAndCheck(keySelector: (item: T) -> K, threshold: Int = 0) = this.countByKeyAndCheckTo(mutableMapOf(), keySelector, threshold)

fun <T, K, M : MutableMap<in K, Int>> Iterable<T>.countByKeyAndCheckTo(destination: M, keySelector: (item: T) -> K, threshold: Int = 0): Boolean {
    this.forEach { element: T ->
        val key = keySelector(element)
        var keyCount = destination.getOrPut(key) { 0 }
        keyCount++
        if (threshold > 0) {
            if (keyCount > threshold) {
                return false//break and return
            }
        } else {
            //default simple count
        }
        destination[key] = keyCount
    }
    return true
}


/**
 * for tree node data
 */
fun <T : Any> Iterable<T>.findAllChild(isChild: (T) -> Boolean, hasChild: (T) -> Boolean, whenHasChild: (T) -> T): List<T> {
    val list = mutableListOf<T>()
    val queue = LinkedList<T>()
    queue += this
    while (queue.isNotEmpty()) {
        val item = queue.poll()
        if (isChild(item)) {
            list += item
        } else if (hasChild(item)) {
            queue += whenHasChild(item)
        }
    }
    return list
}