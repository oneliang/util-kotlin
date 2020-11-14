package com.oneliang.ktx.test.common

import com.oneliang.ktx.util.common.AbstractLRUCache

fun main() {
    val lruCache = object : AbstractLRUCache<String, String?>(10) {
        override fun create(key: String): String? {
            return null
        }
    }
    lruCache["a"]="b"
    println(lruCache["a"])
}