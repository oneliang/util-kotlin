package com.oneliang.ktx.util.common

inline fun <T, K> Array<T>.toMapBy(keySelector: (t: T) -> K): Map<K, T> = this.associateBy { keySelector(it) }

inline fun <T, K, V> Array<T>.toMap(transform: (t: T) -> Pair<K, V>): Map<K, V> = this.associate(transform)

inline fun <T, K, V> Array<out T>.toMapWithIndex(transform: (index: Int, t: T) -> Pair<K, V>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    this.forEachIndexed { index, t ->
        map += transform(index, t)
    }
    return map
}

/**
 * find the different value which in this but not in compare array
 */
fun <T> Array<T>.differs(compareArray: Array<T>, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): List<T> {
    return this.differs(compareArray, { it }, valueComparator)
}

/**
 * find the different value which in this but not in compare array
 */
fun <T, K> Array<T>.differs(compareArray: Array<T>, keySelector: (value: T) -> K, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): List<T> {
    val map = this.toMap { keySelector(it) to it }
    val compareMap = compareArray.toMap { keySelector(it) to it }
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

/**
 * find the add value list which in this but not in compare array and find compare failure value list
 */
fun <T> Array<T>.differsAccurate(compareArray: Array<T>, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): Pair<List<T>, List<T>> {
    return this.differsAccurate(compareArray, { it }, valueComparator)
}

/**
 * find the add value list which in this but not in compare array and find compare failure value list
 */
fun <T, K> Array<T>.differsAccurate(compareArray: Array<T>, keySelector: (value: T) -> K, valueComparator: (value: T, compareValue: T) -> Boolean = { value, compareValue -> value == compareValue }): Pair<List<T>, List<T>> {
    val map = this.toMap { keySelector(it) to it }
    val compareMap = compareArray.toMap { keySelector(it) to it }
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

fun <T> Array<T>.sameAs(array: Array<T>): Boolean = this.size == array.size && this.differs(array).isEmpty()

fun <T> Array<T>.includes(array: Array<T>): Boolean = array.differs(this).isEmpty()

fun <T> Array<T>.matches(array: Array<T>): Boolean = this.includes(array)

inline fun <T, reified R> Array<T>.toNewArrayWithIndex(transform: (index: Int, t: T) -> R): Array<R> {
    return Array(this.size) { index -> transform(index, this[index]) }
}

inline fun <T, reified R> Array<T>.toNewArray(transform: (T) -> R): Array<R> {
    return this.toNewArrayWithIndex { _, t -> transform(t) }
}

inline fun <T> Array<T>.sumByIndexed(selector: (index: Int, item: T) -> Int): Int {
    var sum = 0
    this.forEachIndexed { index, item ->
        sum += selector(index, item)
    }
    return sum
}

inline fun <T> Array<T>.sumByDoubleIndexed(selector: (index: Int, item: T) -> Double): Double {
    var sum = 0.0
    this.forEachIndexed { index, item ->
        sum += selector(index, item)
    }
    return sum
}

inline fun <T> Array<T>.reset(block: (index: Int, item: T) -> T) {
    this.forEachIndexed { index, item ->
        this[index] = block(index, item)
    }
}

fun Array<Double>.reset(value: Double) = this.reset { _, _ -> value }

fun Array<Array<Double>>.reset(value: Double) = this.reset { _, item -> item.reset(value);item }

fun Array<Array<Array<Double>>>.reset(value: Double) = this.reset { _, item -> item.reset(value);item }

fun Array<Array<Array<Array<Double>>>>.reset(value: Double) = this.reset { _, item -> item.reset(value);item }

inline fun <T> Array<T>.compareWithIndexed(selector: (item: T) -> Double, valueReplaceComparator: (value: Double, itemValue: Double) -> Boolean): Pair<Int, T> {
    if (this.isEmpty()) throw NoSuchElementException()
    var valueItem = this[0]
    var value = selector(valueItem)
    var valueIndex = 0
    for (i in 1..lastIndex) {
        val item = this[i]
        val itemValue = selector(item)
        if (valueReplaceComparator(value, itemValue)) {
            value = itemValue
            valueIndex = i
            valueItem = item
        }
    }
    return valueIndex to valueItem
}

inline fun <T> Array<T>.maxOfWithIndexed(selector: (item: T) -> Double): Pair<Int, T> = this.compareWithIndexed(selector) { value: Double, itemValue: Double -> value < itemValue }

inline fun <T> Array<T>.minOfWithIndexed(selector: (item: T) -> Double): Pair<Int, T> = this.compareWithIndexed(selector) { value: Double, itemValue: Double -> value > itemValue }