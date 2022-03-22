package com.oneliang.ktx.util.common

fun CharSequence?.toDefaultWhenIsNullOrBlank(defaultValue: CharSequence): CharSequence {
    return if (this.isNullOrBlank()) {
        defaultValue
    } else {
        this
    }
}