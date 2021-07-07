package com.oneliang.ktx.util.common

fun Float.roundToFix(value: Int): String {
    return this.toDouble().roundToFix(value)
}