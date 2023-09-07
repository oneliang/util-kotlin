package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.util.common.toByteArray
import com.oneliang.ktx.util.common.toInt

class AtomicBinaryInt(
    initializeSize: Int,
    expandSize: Int = 10000
) : AtomicBinary<Int>(
    initializeSize,
    expandSize,
    LENGTH_BODY,
    byteArrayToData = { byteArray: ByteArray -> byteArray.toInt() },
    dataToByteArray = { data: Int -> data.toByteArray() }
) {

    companion object {
        private const val LENGTH_BODY = 4
    }
}