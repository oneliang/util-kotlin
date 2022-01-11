package com.oneliang.ktx.util.common

inline fun <T, R> T.transform(block: (t: T) -> R): R {
    return block(this)
}

inline fun <T> T.sink(block: (t: T) -> Unit) {
    block(this)
}

inline fun <T> T.convertByCondition(conditionBlock: () -> Boolean, failureValue: T): T {
    return this.convertByCondition(conditionBlock, this, failureValue)
}

inline fun <T> T.convertByCondition(conditionBlock: () -> Boolean, successValue: T, failureValue: T): T {
    return if (conditionBlock()) {
        successValue
    } else {
        failureValue
    }
}

fun <T : Any?> T.ifNull(block: (t: T) -> Unit) {
    if (this == null) {
        block(this)
    }
}

fun <T : Any> T?.ifNotNull(block: (t: T) -> Unit) {
    if (this != null) {
        block(this)
    }
}