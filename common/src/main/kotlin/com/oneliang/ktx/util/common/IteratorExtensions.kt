package com.oneliang.ktx.util.common

inline fun <T, K, V> Iterator<T>.toMap(transform: (t: T) -> Pair<K, V>): Map<K, V> {
    return this.toMapWithIndex { _, t ->
        transform(t)
    }
}

inline fun <T, K, V> Iterator<T>.toMapWithIndex(transform: (index: Int, t: T) -> Pair<K, V>): Map<K, V> {
    val map = mutableMapOf<K, V>()
    var index = 0
    this.forEach {
        map += transform(index++, it)
    }
    return map
}