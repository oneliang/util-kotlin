package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.InputStream
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun String.HmacSHA256(secretByteArray: ByteArray): ByteArray = CryptoMacUtil.hmacSHA256(this.toByteArray(Charsets.UTF_8), secretByteArray)
fun String.HmacSHA256String(secretByteArray: ByteArray): String = this.HmacSHA256(secretByteArray).toHexString()

fun ByteArray.HmacSHA256(secretByteArray: ByteArray): ByteArray = CryptoMacUtil.hmacSHA256(this, secretByteArray)
fun ByteArray.HmacSHA256String(secretByteArray: ByteArray): String = this.HmacSHA256(secretByteArray).toHexString()

fun InputStream.HmacSHA256(secretByteArray: ByteArray): ByteArray = CryptoMacUtil.hmacSHA256(this, secretByteArray)
fun InputStream.HmacSHA256String(secretByteArray: ByteArray): String = this.HmacSHA256(secretByteArray).toHexString()

object CryptoMacUtil {
    enum class Algorithm(val value: String) {
        HMACSHA256("HmacSHA256")
    }

    private val hmacSHA256FirstBlock: (secretByteArray: ByteArray) -> Mac = { secretByteArray ->
        val mac = Mac.getInstance(Algorithm.HMACSHA256.value)
        val secretKeySpec = SecretKeySpec(secretByteArray, Algorithm.HMACSHA256.value)
        mac.init(secretKeySpec)
        mac
    }

    fun hmacSHA256(inputStream: InputStream, secretByteArray: ByteArray): ByteArray {
        return try {
            return cryptoDigest(inputStream) {
                hmacSHA256FirstBlock(secretByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun hmacSHA256(byteArray: ByteArray, secretByteArray: ByteArray): ByteArray {
        return try {
            return cryptoDigest(byteArray) {
                hmacSHA256FirstBlock(secretByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun cryptoDigest(inputStream: InputStream, firstBlock: () -> Mac): ByteArray {
        return try {
            return simplePipeline(firstBlock, {
                cryptoDigest(it, inputStream)
            }, { it, _ ->
                it.doFinal() ?: ByteArray(0)
            })
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun cryptoDigest(byteArray: ByteArray, firstBlock: () -> Mac): ByteArray {
        return try {
            return simplePipeline(firstBlock, {
                cryptoDigest(it, byteArray)
            }, { it, _ ->
                it.doFinal() ?: ByteArray(0)
            })
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    private fun cryptoDigest(mac: Mac, inputStream: InputStream) {
        inputStream.use {
            val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
            var readCount: Int
            readCount = inputStream.read(buffer, 0, buffer.size)
            while (readCount != -1) {
                mac.update(buffer, 0, readCount)
                readCount = inputStream.read(buffer, 0, buffer.size)
            }
        }
    }

    private fun cryptoDigest(mac: Mac, byteArray: ByteArray) {
        mac.update(byteArray, 0, byteArray.size)
    }
}