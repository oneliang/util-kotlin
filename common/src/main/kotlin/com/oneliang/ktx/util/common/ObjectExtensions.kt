package com.oneliang.ktx.util.common

inline fun <T, R> T.transform(block: (t: T) -> R): R {
    return block(this)
}

inline fun <T> T.sink(block: (t: T) -> Unit) {
    block(this)
}