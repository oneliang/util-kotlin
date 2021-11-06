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

/**
 * the same as toNewArray, just for quick use, because keyword "map"
 */
inline fun <T, reified R> Array<T>.mapToNewArray(transform: (T) -> R): Array<R> = this.toNewArray(transform)

/**
 * the same as toNewArray, just for quick use, because keyword "map"
 */
inline fun <T, reified R> Array<T>.mapToNewArrayWithIndex(transform: (index: Int, t: T) -> R): Array<R> = this.toNewArrayWithIndex(transform)

inline fun <T> Array<out T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Array<T>.sumByFloatIndexed(selector: (index: Int, item: T) -> Float): Float {
    var sum = 0.0f
    this.forEachIndexed { index, item ->
        sum += selector(index, item)
    }
    return sum
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

fun Array<Short>.toShortArray(): ShortArray {
    val shortArray = ShortArray(this.size) { 0 }
    for (index in shortArray.indices) {
        shortArray[index] = this[index]
    }
    return shortArray
}

fun Array<Int>.toIntArray(): IntArray {
    val intArray = IntArray(this.size) { 0 }
    for (index in intArray.indices) {
        intArray[index] = this[index]
    }
    return intArray
}

fun Array<Long>.toLongArray(): LongArray {
    val longArray = LongArray(this.size) { 0L }
    for (index in longArray.indices) {
        longArray[index] = this[index]
    }
    return longArray
}

fun Array<Float>.toLongArray(): FloatArray {
    val floatArray = FloatArray(this.size) { 0.0f }
    for (index in floatArray.indices) {
        floatArray[index] = this[index]
    }
    return floatArray
}

fun Array<Double>.toDoubleArray(): DoubleArray {
    val doubleArray = DoubleArray(this.size) { 0.0 }
    for (index in doubleArray.indices) {
        doubleArray[index] = this[index]
    }
    return doubleArray
}

fun <T : Any> Array<T>.get(xSize: Int, ySize: Int = 0, zSize: Int = 0, x: Int, y: Int = 0, z: Int = 0): T {
    if (x >= xSize || y >= ySize || z >= zSize) {
        error("out of index, size:[%s,%s,%s], range:[0~%s,0~%s,0~%s], (x,y,z):[%s,%s,%s]".format(xSize, ySize, zSize, xSize - 1, ySize - 1, zSize - 1, x, y, z))
    }
    val index = x * ySize * zSize + y * zSize + z
    return this[index]
}

private inline fun <reified T : Any> Array<T>.to2DArray(defaultValue: T, xSize: Int, ySize: Int): Array<Array<T>> {
    val array = Array(xSize) { Array(ySize) { defaultValue } }
    for (x in 0 until xSize) {
        for (y in 0 until ySize) {
            array[x][y] = this[x * ySize + y]
        }
    }
    return array
}

fun Array<Int>.to2DArray(xSize: Int, ySize: Int): Array<Array<Int>> = this.to2DArray(0, xSize, ySize)
fun Array<Float>.to2DArray(xSize: Int, ySize: Int): Array<Array<Float>> = this.to2DArray(0.0f, xSize, ySize)
fun Array<Double>.to2DArray(xSize: Int, ySize: Int): Array<Array<Double>> = this.to2DArray(0.0, xSize, ySize)

private inline fun <reified T : Any> Array<T>.to3DArray(defaultValue: T, xSize: Int, ySize: Int, zSize: Int): Array<Array<Array<T>>> {
    val array = Array(xSize) { Array(ySize) { Array(zSize) { defaultValue } } }
    for (x in 0 until xSize) {
        for (y in 0 until ySize) {
            for (z in 0 until zSize) {
                array[x][y][z] = this[x * ySize * zSize + y * zSize + z]
            }
        }
    }
    return array
}

fun Array<Int>.to3DArray(xSize: Int, ySize: Int, zSize: Int): Array<Array<Array<Int>>> = this.to3DArray(0, xSize, ySize, zSize)
fun Array<Float>.to3DArray(xSize: Int, ySize: Int, zSize: Int): Array<Array<Array<Float>>> = this.to3DArray(0.0f, xSize, ySize, zSize)
fun Array<Double>.to3DArray(xSize: Int, ySize: Int, zSize: Int): Array<Array<Array<Double>>> = this.to3DArray(0.0, xSize, ySize, zSize)

private inline fun <reified T : Any> Array<Array<T>>.to1DArray(defaultValue: T): Array<T> {
    if (this.isEmpty() || this[0].isEmpty()) {
        return emptyArray()
    }
    val array = Array(this.size * this[0].size) { defaultValue }
    val xSize = this.size
    val ySize = this[0].size
    for (x in 0 until xSize) {
        for (y in 0 until ySize) {
            val index = x * ySize + y
            array[index] = this[x][y]
        }
    }
    return array
}

fun Array<Array<Int>>.to1DArray(): Array<Int> = this.to1DArray(0)
fun Array<Array<Float>>.to1DArray(): Array<Float> = this.to1DArray(0.0f)
fun Array<Array<Double>>.to1DArray(): Array<Double> = this.to1DArray(0.0)

private inline fun <reified T : Any> Array<Array<Array<T>>>.to1DArray(defaultValue: T): Array<T> {
    if (this.isEmpty() || this[0].isEmpty() || this[0][0].isEmpty()) {
        return emptyArray()
    }
    val xSize = this.size
    val ySize = this[0].size
    val zSize = this[0][0].size
    val array = Array(xSize * ySize * zSize) { defaultValue }
    for (x in 0 until xSize) {
        for (y in 0 until ySize) {
            for (z in 0 until zSize) {
                val index = x * ySize * zSize + y * zSize + z
                array[index] = this[x][y][z]
            }
        }
    }
    return array
}

fun Array<Array<Array<Int>>>.to1DArray(): Array<Int> = this.to1DArray(0)
fun Array<Array<Array<Float>>>.to1DArray(): Array<Float> = this.to1DArray(0.0f)
fun Array<Array<Array<Double>>>.to1DArray(): Array<Double> = this.to1DArray(0.0)

fun <T> Array<T>.swap(fromIndex: Int, toIndex: Int) {
    val t = this[fromIndex]
    this[fromIndex] = this[toIndex]
    this[toIndex] = t
}

fun <T> Array<T>.count(): Map<T, Int> = this.countKey { it }

fun <T, K> Array<T>.countKey(keySelector: (item: T) -> K): Map<K, Int> = this.countKeyTo(mutableMapOf(), keySelector)

fun <T, K, M : MutableMap<in K, Int>> Array<T>.countKeyTo(destination: M, keySelector: (item: T) -> K): M {
    this.countKeyAndCheckTo(destination, keySelector)
    return destination
}

fun <T, K> Array<T>.keyCountAndCheck(keySelector: (item: T) -> K, threshold: Int = 0) = this.countKeyAndCheckTo(mutableMapOf(), keySelector, threshold)

fun <T, K, M : MutableMap<in K, Int>> Array<T>.countKeyAndCheckTo(destination: M, keySelector: (item: T) -> K, threshold: Int = 0): Boolean {
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