package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun ByteArray.gzip(): ByteArray = GZipUtil.compress(this)
fun ByteArray.unGzip(): ByteArray = GZipUtil.uncompress(this)

object GZipUtil {
    private val logger = LoggerManager.getLogger(GZipUtil::class)
    fun compress(unCompressByteArray: ByteArray): ByteArray {
        return try {
            val outputStream = ByteArrayOutputStream()
            val gzipOutputStream = GZIPOutputStream(outputStream)
            gzipOutputStream.write(unCompressByteArray)
            gzipOutputStream.close()
            outputStream.toByteArray()
        } catch (e: Throwable) {
            logger.error(String.format("gzip compress exception:%s", e, e.message))
            ByteArray(0)
        }
    }

    fun uncompress(compressByteArray: ByteArray): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val byteArrayInputStream = ByteArrayInputStream(compressByteArray)
        return try {
            val gZIPInputStream = GZIPInputStream(byteArrayInputStream)
            val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
            var length: Int = gZIPInputStream.read(buffer)
            while (length >= 0) {
                byteArrayOutputStream.write(buffer, 0, length)
                length = gZIPInputStream.read(buffer)
            }
            byteArrayOutputStream.toByteArray()
        } catch (e: Throwable) {
            logger.error(String.format("gzip uncompress error. message:%s", e, e.message))
            ByteArray(0)
        }
    }
}