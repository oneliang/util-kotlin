package com.oneliang.ktx.util.section

import java.io.InputStream

open class UnitBlock constructor(val endian: Endian = Endian.BIG) : Block {
    enum class Endian {
        BIG, LITTLE
    }

    override var initialSize: Int = 0
    override var value: ByteArray = ByteArray(0)

    /**
     * @return the totalSize
     */
    override var totalSize = 0
        protected set

    @Throws(Exception::class)
    override fun parse(inputStream: InputStream) {
        var buffer = ByteArray(initialSize)
        val length = inputStream.read(buffer)
        if (length == buffer.size) {
            when (endian) {
                Endian.LITTLE -> buffer = buffer.reversedArray()
                else -> {
                }
            }
            value = buffer
        } else {
            value = ByteArray(0)
        }
        totalSize = value.size
    }

    /**
     * to byte array
     * @return byte[]
     */
    override fun toByteArray(): ByteArray {
        return when (endian) {
            Endian.LITTLE -> value.reversedArray()
            else -> value
        }
    }
}