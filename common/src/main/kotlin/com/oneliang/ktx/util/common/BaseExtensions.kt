package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.lang.management.ManagementFactory
import java.net.InetAddress
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

private fun splitPidFromRuntimeName(): String {
    val runtimeName = ManagementFactory.getRuntimeMXBean().name.nullToBlank()
    val runtimeNameSplit = runtimeName.split(Constants.Symbol.AT)
    return when {
        runtimeNameSplit.isNotEmpty() -> {
            runtimeNameSplit[0]
        }
        else -> {
            Constants.String.BLANK
        }
    }
}

val PID = splitPidFromRuntimeName()

private val localhost = InetAddress.getLocalHost()

val HOST_ADDRESS = localhost?.hostAddress ?: Constants.String.BLANK
val HOST_NAME = localhost?.hostName ?: Constants.String.BLANK