package com.oneliang.ktx.util.common

class Hasher(private val module: Int) {

    init {
        if (this.module <= 0) {
            error("parameter[module] can not be negative, module:%s".format(this.module))
        }
    }

    fun <T> hash(key: Int, operateBlock: (Int) -> T): T {
        val hash = key % this.module
        return operateBlock(hash)
    }
}