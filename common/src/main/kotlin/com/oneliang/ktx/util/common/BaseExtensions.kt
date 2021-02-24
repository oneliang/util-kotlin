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