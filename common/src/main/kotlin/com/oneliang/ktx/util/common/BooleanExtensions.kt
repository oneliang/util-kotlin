package com.oneliang.ktx.util.common

fun Boolean?.nullToFalse(): Boolean {
    return this ?: false
}