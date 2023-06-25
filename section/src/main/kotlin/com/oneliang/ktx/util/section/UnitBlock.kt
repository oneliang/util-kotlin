package com.oneliang.ktx.util.section

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayOutputStream
import java.io.InputStream

open class UnitBlock constructor(
    val endian: Endian = Endian.BIG,
    override var initialSize: Int = 0
) : Block {

    companion object {
        private val logger = LoggerManager.getLogger(UnitBlock::class)
    }

    enum class Endian {
        BIG, LITTLE
    }

    override var value: ByteArray = ByteArray(0)

    /**
     * @return the totalSize
     */
    override var totalSize = 0
        protected set

    @Throws(Exception::class)
    override fun parse(inputStream: InputStream) {
        var buffer = ByteArray(this.initialSize)
        val length = inputStream.read(buffer)
        if (length == buffer.size) {
            when (this.endian) {
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