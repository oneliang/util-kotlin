package com.oneliang.ktx.util.section

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayOutputStream
import java.io.InputStream

interface Block : Section {
    companion object {
        internal val logger = LoggerManager.getLogger(Block::class)
    }

    /**
     * set initial size
     */
    var initialSize: Int
    /**
     * @return the value
     */
    /**
     * set value
     */
    var value: ByteArray

    /**
     * @return the totalSize
     */
    val totalSize: Int

    /**
     * parse
     *
     * @param inputStream
     * @throws Exception
     */
    @Throws(Exception::class)
    fun parse(inputStream: InputStream)
}

fun List<Block>.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    try {
        for (block in this) {
            byteArrayOutputStream.write(block.toByteArray())
            byteArrayOutputStream.flush()
        }
    } catch (e: Exception) {
        Block.logger.error(Constants.String.EXCEPTION, e)
    } finally {
        try {
            byteArrayOutputStream.close()
        } catch (e: Exception) {
            Block.logger.error(Constants.String.EXCEPTION, e)
        }
    }
    return byteArrayOutputStream.toByteArray()
}