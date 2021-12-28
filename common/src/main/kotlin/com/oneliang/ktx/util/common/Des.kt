package com.oneliang.ktx.util.common

import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun String.DESEncrypt(keyByteArray: ByteArray, ivParameterByteArray: ByteArray): String = Des.encrypt(this.toByteArray(Charsets.UTF_8), keyByteArray, ivParameterByteArray).toHexString()
fun ByteArray.DESEncrypt(keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray = Des.encrypt(this, keyByteArray, ivParameterByteArray)
fun InputStream.DESEncrypt(keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray = Des.encrypt(this, keyByteArray, ivParameterByteArray)

fun String.DESDecrypt(keyByteArray: ByteArray, ivParameterByteArray: ByteArray): String = Des.decrypt(this.toByteArray(Charsets.UTF_8), keyByteArray, ivParameterByteArray).toHexString()
fun ByteArray.DESDecrypt(keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray = Des.decrypt(this, keyByteArray, ivParameterByteArray)
fun InputStream.DESDecrypt(keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray = Des.decrypt(this, keyByteArray, ivParameterByteArray)

object Des {
    private const val DES = "DES"
    private const val DES_CBC_PKC5PADDING = "DES/CBC/PKCS5Padding"

    private val desFirstBlock: (encrypt: Boolean, keyByteArray: ByteArray, ivParameterByteArray: ByteArray) -> Cipher = { encrypt, keyByteArray, ivParameterByteArray ->
        val secretKeySpec = SecretKeySpec(keyByteArray, DES)
        val ivParameterSpec = IvParameterSpec(ivParameterByteArray)
        val cipher = Cipher.getInstance(DES_CBC_PKC5PADDING)
        if (encrypt) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        }
        cipher
    }

    fun encrypt(inputStream: InputStream, keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(inputStream) {
                desFirstBlock(true, keyByteArray, ivParameterByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun encrypt(byteArray: ByteArray, keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(byteArray) {
                desFirstBlock(true, keyByteArray, ivParameterByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun decrypt(inputStream: InputStream, keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(inputStream) {
                desFirstBlock(false, keyByteArray, ivParameterByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun decrypt(byteArray: ByteArray, keyByteArray: ByteArray, ivParameterByteArray: ByteArray): ByteArray {
        return try {
            return CryptoUtil.cryptoDigest(byteArray) {
                desFirstBlock(false, keyByteArray, ivParameterByteArray)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }
}