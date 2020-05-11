package com.oneliang.ktx.util.common

fun Short.toUnsigned(): Int = this.toInt() and 0xFFFF

fun Short.toByteArray(): ByteArray = ByteArray(2) {
    (this.toInt() shr (8 * it) and 0xFF).toByte()
}.apply { this.reverse() }