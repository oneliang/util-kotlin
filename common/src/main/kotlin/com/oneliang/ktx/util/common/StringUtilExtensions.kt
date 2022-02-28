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

fun String.fillSameCharPrefix(char: Char, length: Int): String {
    return StringUtil.generateSameCharString(char, length) + this
}

fun String.fillSameCharPrefixFix(char: Char, maxLength: Int): String {
    if (maxLength - this.length < 0) return this
    return this.fillSameCharPrefix(char, maxLength - this.length)
}

fun String.fillZeroSuffix(length: Int): String {
    return this + generateZeroString(length)
}

fun String.fillSameCharSuffix(char: Char, length: Int): String {
    return this + StringUtil.generateSameCharString(char, length)
}

fun String.fillSameCharSuffixFix(char: Char, maxLength: Int): String {
    if (maxLength - this.length < 0) return this
    return this.fillSameCharSuffix(char, maxLength - this.length)
}