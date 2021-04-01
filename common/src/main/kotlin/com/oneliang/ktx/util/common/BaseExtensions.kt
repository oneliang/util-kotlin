package com.oneliang.ktx.util.common

import java.util.*

/**
 * for try catch without return value
 */
inline fun <R> perform(block: () -> R): R {
    return try {
        block()
    } finally {
    }
}

/**
 * hash, use Objects.hash
 */
fun hash(vararg values: Any?) = Objects.hash(*values)

inline fun singleIteration(times: Int, block: (i: Int) -> Unit) {
    for (i in 0 until times) {
        block(i)
    }
}

inline fun doubleIteration(outerTimes: Int, innerTimes: Int, block: (outer: Int, inner: Int) -> Unit) {
    for (outer in 0 until outerTimes) {
        for (inner in 0 until innerTimes) {
            block(outer, inner)
        }
    }
}
