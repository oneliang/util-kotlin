package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

fun String.MD5(): ByteArray = MessageDigestUtil.digest(this.toByteArray(Charsets.UTF_8), MessageDigestUtil.Algorithm.MD5)
fun String.MD5String(): String = this.MD5().toHexString()

fun String.SHA1(): ByteArray = MessageDigestUtil.digest(this.toByteArray(Charsets.UTF_8), MessageDigestUtil.Algorithm.SHA1)
fun String.SHA1String(): String = this.SHA1().toHexString()

fun ByteArray.SHA1(): ByteArray = MessageDigestUtil.digest(this, MessageDigestUtil.Algorithm.SHA1)
fun ByteArray.SHA1String(): String = this.SHA1().toHexString()

fun ByteArray.MD5(): ByteArray = MessageDigestUtil.digest(this, MessageDigestUtil.Algorithm.MD5)
fun ByteArray.MD5String(): String = this.MD5().toHexString()

fun InputStream.digest(algorithm: String): ByteArray = MessageDigestUtil.digest(this, algorithm)
fun InputStream.MD5(): ByteArray = this.digest(MessageDigestUtil.Algorithm.MD5)
fun InputStream.MD5String(): String = this.MD5().toHexString()

fun InputStream.SHA1(): ByteArray = this.digest(MessageDigestUtil.Algorithm.SHA1)
fun InputStream.SHA1String(): String = this.SHA1().toHexString()

fun File.MD5(): ByteArray = if (!this.exists()) ByteArray(0) else FileInputStream(this).MD5()
fun File.MD5String(): String = if (!this.exists()) Constants.String.BLANK else this.MD5().toHexString()

fun File.SHA1(): ByteArray = if (!this.exists()) ByteArray(0) else FileInputStream(this).SHA1()
fun File.SHA1String(): String = if (!this.exists()) Constants.String.BLANK else this.SHA1().toHexString()

object MessageDigestUtil {

    object Algorithm {
        const val MD5 = "MD5"
        const val SHA1 = "SHA-1"
    }

    fun digest(inputStream: InputStream, algorithm: String): ByteArray {
        try {
            val buffer = ByteArray(1024)
            val messageDigest = MessageDigest.getInstance(algorithm)
            var readCount: Int
            readCount = inputStream.read(buffer, 0, buffer.size)
            while (readCount != -1) {
                messageDigest.update(buffer, 0, readCount)
                readCount = inputStream.read(buffer, 0, buffer.size)
            }
            val byteArray = messageDigest.digest()
            return byteArray ?: ByteArray(0)
        } catch (e: Exception) {
            return ByteArray(0)
        } finally {
            try {
                inputStream.close()
            } catch (e: Exception) {
                return ByteArray(0)
            }
        }
    }

    fun digest(byteArray: ByteArray, algorithm: String): ByteArray {
        return try {
            val messageDigest = MessageDigest.getInstance(algorithm)
            messageDigest.update(byteArray, 0, byteArray.size)
            val digestByteArray = messageDigest.digest()
            digestByteArray ?: ByteArray(0)
        } catch (e: Exception) {
            ByteArray(0)
        }
    }
}