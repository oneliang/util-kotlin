package com.oneliang.ktx.util.common

fun Float.roundToFix(value: Int): String {
    return this.toDouble().roundToFix(value)
}

fun Float.fixNaN(defaultValue: Float = 0f): Float {
    return if (this.isNaN()) {
        defaultValue
    } else {
        this
    }
}