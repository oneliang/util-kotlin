package com.oneliang.ktx.test

import com.oneliang.ktx.Constants
import com.oneliang.ktx.pojo.ByteArrayWrapper

fun main() {
    val byteArrayWrapper = ByteArrayWrapper(4)
    val buffer = ByteArray(4)
    buffer[0] = 1
    buffer[1] = 2
    buffer[2] = 3
    buffer[3] = 4
    byteArrayWrapper.write(0, buffer)
    val readBuffer = byteArrayWrapper.read(0, 4)
    println(readBuffer.joinToString(Constants.String.BLANK) { String.format("%02X", (it.toInt() and 0xFF)) })
}