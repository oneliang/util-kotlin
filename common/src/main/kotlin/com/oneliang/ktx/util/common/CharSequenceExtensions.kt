package com.oneliang.ktx.util.common

fun CharSequence?.toDefaultWhenIsNullOrBlank(defaultValue: CharSequence): CharSequence {
    return if (this.isNullOrBlank()) {
        defaultValue
    } else {
        this
    }
}

inline fun CharSequence.ifNotBlank(block: (CharSequence) -> Unit) {
    if (this.isNotBlank()) {
        block(this)
    }
}

inline fun CharSequence.ifNotEmpty(block: (CharSequence) -> Unit) {
    if (this.isNotEmpty()) {
        block(this)
    }
}
