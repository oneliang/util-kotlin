package com.oneliang.ktx.util.common

import kotlin.math.pow

fun Int.toUnsigned(): Long = this.toLong() and 0xFFFFFFFFL

fun Int.toByteArray(): ByteArray = ByteArray(4) {
    (this shr (8 * it) and 0xFF).toByte()
}.apply { this.reverse() }

infix fun Int.remove(other: Int): Int {
    return this and (other.inv())
}

fun Int.bitsToFloat(): Float {
    return java.lang.Float.intBitsToFloat(this)
}

fun Int.pow(num: Int): Int {
    return this.toDouble().pow(num).toInt()
}