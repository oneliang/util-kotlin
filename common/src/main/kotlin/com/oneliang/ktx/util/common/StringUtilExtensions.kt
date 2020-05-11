package com.oneliang.ktx.util.common

fun Long.toFillZeroString(maxLength: Int): String {
    return StringUtil.fillZero(maxLength - this.toString().length) + this.toString()
}

fun Int.toFillZeroString(maxLength: Int): String {
    return this.toLong().toFillZeroString(maxLength)
}