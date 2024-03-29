package com.oneliang.ktx.util.common

import java.util.*

fun Long.toByteArray(): ByteArray = ByteArray(8) {
    (this shr (8 * it) and 0xFF).toByte()
}.apply { this.reverse() }

fun Long.toUtilDate(): Date {
    return Date(this)
}

fun Long.bitsToDouble(): Double {
    return java.lang.Double.longBitsToDouble(this)
}

infix fun Long.bitContains(other: Long): Boolean {
    return (this and other == other)
}

fun Long.toBinaryString(): String {
    return java.lang.Long.toBinaryString(this)
}