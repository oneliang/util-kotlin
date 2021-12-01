package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

fun String.MD5(): ByteArray = MessageDigestUtil.digest(MessageDigestUtil.Algorithm.MD5, this.toByteArray(Charsets.UTF_8))
fun String.MD5String(): String = this.MD5().toHexString()

fun String.SHA1(): ByteArray = MessageDigestUtil.digest(MessageDigestUtil.Algorithm.SHA1, this.toByteArray(Charsets.UTF_8))
fun String.SHA1String(): String = this.SHA1().toHexString()

fun ByteArray.SHA1(): ByteArray = MessageDigestUtil.digest(MessageDigestUtil.Algorithm.SHA1, this)
fun ByteArray.SHA1String(): String = this.SHA1().toHexString()

fun ByteArray.MD5(): ByteArray = MessageDigestUtil.digest(MessageDigestUtil.Algorithm.MD5, this)
fun ByteArray.MD5String(): String = this.MD5().toHexString()

fun InputStream.digest(algorithm: MessageDigestUtil.Algorithm): ByteArray = MessageDigestUtil.digest(algorithm, this)
fun InputStream.MD5(): ByteArray = this.digest(MessageDigestUtil.Algorithm.MD5)
fun InputStream.MD5String(): String = this.MD5().toHexString()

fun InputStream.SHA1(): ByteArray = this.digest(MessageDigestUtil.Algorithm.SHA1)
fun InputStream.SHA1String(): String = this.SHA1().toHexString()

fun File.MD5(): ByteArray = if (!this.exists()) ByteArray(0) else FileInputStream(this).MD5()
fun File.MD5String(): String = if (!this.exists()) Constants.String.BLANK else this.MD5().toHexString()

fun File.SHA1(): ByteArray = if (!this.exists()) ByteArray(0) else FileInputStream(this).SHA1()
fun File.SHA1String(): String = if (!this.exists()) Constants.String.BLANK else this.SHA1().toHexString()

object MessageDigestUtil {

    enum class Algorithm(val value: String) {
        MD5("MD5"), SHA1("SHA-1")
    }

    fun digest(algorithm: Algorithm, block: (messageDigest: MessageDigest) -> Unit): ByteArray {
        return try {
            val messageDigest = MessageDigest.getInstance(algorithm.value)
            block(messageDigest)
            val byteArray = messageDigest.digest()
            return byteArray ?: ByteArray(0)
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun digest(algorithm: Algorithm, inputStream: InputStream): ByteArray {
        try {
            return digest(algorithm) {
                val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
                var readCount: Int
                readCount = inputStream.read(buffer, 0, buffer.size)
                while (readCount != -1) {
                    it.update(buffer, 0, readCount)
                    readCount = inputStream.read(buffer, 0, buffer.size)
                }
            }
        } catch (e: Throwable) {
            return ByteArray(0)
        } finally {
            try {
                inputStream.close()
            } catch (e: Throwable) {
                return ByteArray(0)
            }
        }
    }

    fun digest(algorithm: Algorithm, byteArray: ByteArray): ByteArray {
        return try {
            return digest(algorithm) {
                it.update(byteArray, 0, byteArray.size)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }
}