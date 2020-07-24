package com.oneliang.ktx.util.common

fun <K, V> Map<K, V>.MD5String(): String {
    return MessageDigestUtil.digest(MessageDigestUtil.Algorithm.MD5) {
        this.forEach { (key, value) ->
            it.update(key.toString().toByteArray())
            it.update(value.toString().toByteArray())
        }
    }.toHexString()
}