package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.util.common.toByteArray
import com.oneliang.ktx.util.common.toInt

class AtomicBinaryInt(
    maxSize: Int,
    indexOffset: Long = 0L
) : AtomicBinary<Int>(
    maxSize, LENGTH_BODY,
    indexOffset,
    byteArrayToData = { byteArray: ByteArray -> byteArray.toInt() },
    dataToByteArray = { data: Int -> data.toByteArray() }
) {

    companion object {
        private const val LENGTH_BODY = 4
    }
}