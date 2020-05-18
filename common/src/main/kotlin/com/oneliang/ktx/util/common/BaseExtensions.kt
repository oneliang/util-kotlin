package com.oneliang.ktx.util.common

/**
 * for try catch without return value
 */
inline fun perform(block: () -> Unit) = perform(block, {})

/**
 * for try finally without return value
 */
inline fun perform(block: () -> Unit, finally: () -> Unit) = perform(block, {}, finally)

/**
 * for try catch with return value, you can return unit, and it support without return value
 */
inline fun <R> perform(block: () -> R, failure: (t: Throwable) -> R): R {
    return perform(block, failure, {})
}

/**
 * for try catch finally with return value
 */
inline fun <R> perform(block: () -> R, failure: (t: Throwable) -> R, finally: () -> Unit): R {
    return try {
        block()
    } catch (t: Throwable) {
        failure(t)
    } finally {
        finally()
    }
}