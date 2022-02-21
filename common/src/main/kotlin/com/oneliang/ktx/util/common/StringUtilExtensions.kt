package com.oneliang.ktx.util.common

fun Long.toFillZeroString(maxLength: Int): String {
    val longString = this.toString()
    return generateZeroString(maxLength - longString.length) + longString
}

fun Int.toFillZeroString(maxLength: Int): String {
    return this.toLong().toFillZeroString(maxLength)
}

fun String.fillZeroPrefix(length: Int): String {
    return generateZeroString(length) + this
}

fun String.fillZeroSuffix(length: Int): String {
    return this + generateZeroString(length)
}