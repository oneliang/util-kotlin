package com.oneliang.ktx.util.common

import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun String.DESEncrypt(keyByteArray: ByteArray): String = Des.encrypt(this.toByteArray(Charsets.UTF_8), keyByteArray).toHexString()
fun ByteArray.DESEncrypt(keyByteArray: ByteArray): ByteArray = Des.encrypt(this, keyByteArray)
fun InputStream.DESEncrypt(keyByteArray: ByteArray): ByteArray = Des.encrypt(this, keyByteArray)

fun String.DESDecrypt(keyByteArray: ByteArray): String = Des.decrypt(this.toByteArray(Charsets.UTF_8), keyByteArray).toHexString()
fun ByteArray.DESDecrypt(keyByteArray: ByteArray): ByteArray = Des.decrypt(this, keyByteArray)
fun InputStream.DESDecrypt(keyByteArray: ByteArray): ByteArray = Des.decrypt(this, keyByteArray)

object Des {
    private const val DES = "DES"
    private const val DES_CBC_PKC5PADDING = "DES/CBC/PKCS5Padding"
    private val IV = byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8)

    private val desFirstBlock: (encrypt: Boolean, keyByteArray: ByteArray) -> Cipher = { encrypt, key ->
        val ivParameterSpec = IvParameterSpec(IV)
        val secretKeySpec = SecretKeySpec(key, DES)
        val cipher = Cipher.getInstance(DES_CBC_PKC5PADDING)
        if (encrypt) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        }
        cipher
    }

    fun encrypt(inputStream: InputStream, keyByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(inputStream) {
                desFirstBlock(true, keyByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun encrypt(byteArray: ByteArray, keyByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(byteArray) {
                desFirstBlock(true, keyByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun decrypt(inputStream: InputStream, keyByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(inputStream) {
                desFirstBlock(false, keyByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun decrypt(byteArray: ByteArray, keyByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(byteArray) {
                desFirstBlock(false, keyByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }
}