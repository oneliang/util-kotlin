package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import javax.crypto.Cipher


fun String.RSAEncrypt(key: Key): String = Rsa.encrypt(this.toByteArray(Charsets.UTF_8), key).toHexString()
fun ByteArray.RSAEncrypt(key: Key): ByteArray = Rsa.encrypt(this, key)
fun InputStream.RSAEncrypt(key: Key): ByteArray = Rsa.encrypt(this, key)

fun String.RSADecrypt(key: Key): String = Rsa.decrypt(this.toByteArray(Charsets.UTF_8), key).toHexString()
fun ByteArray.RSADecrypt(key: Key): ByteArray = Rsa.decrypt(this, key)
fun InputStream.RSADecrypt(key: Key): ByteArray = Rsa.decrypt(this, key)

object Rsa {
    private const val ALGORITHM_RSA = "RSA"
    private const val RSA_MODE_1 = "RSA/ECB/PKCS1Padding"
    private const val ENCRYPT_MAX_LENGTH = 117
    private const val DECRYPT_MAX_LENGTH = 128

    fun generateKeyPair(): KeyPair {
        val secureRandom = SecureRandom()
        val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA)
        keyPairGenerator.initialize(Constants.Capacity.BYTES_PER_KB, secureRandom)
        return keyPairGenerator.generateKeyPair()
    }

    private val rsaEncryptBlock: (key: Key) -> Cipher = { key ->
        val cipher = Cipher.getInstance(ALGORITHM_RSA)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        cipher
    }

    private val rsaDecryptBlock: (key: Key) -> Cipher = { key ->
        val cipher = Cipher.getInstance(RSA_MODE_1)
        cipher.init(Cipher.DECRYPT_MODE, key)
        cipher
    }

    fun encrypt(inputStream: InputStream, key: Key): ByteArray {
        return try {
            return cryptoDigest(inputStream, ENCRYPT_MAX_LENGTH) {
                rsaEncryptBlock(key)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    fun encrypt(byteArray: ByteArray, key: Key): ByteArray {
        return try {
            return cryptoDigest(byteArray, ENCRYPT_MAX_LENGTH) {
                rsaEncryptBlock(key)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    fun decrypt(inputStream: InputStream, key: Key): ByteArray {
        return try {
            return cryptoDigest(inputStream, DECRYPT_MAX_LENGTH) {
                rsaDecryptBlock(key)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun decrypt(byteArray: ByteArray, key: Key): ByteArray {
        return try {
            return cryptoDigest(byteArray, DECRYPT_MAX_LENGTH) {
                rsaDecryptBlock(key)
            }
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun cryptoDigest(inputStream: InputStream, maxLength: Int, firstBlock: () -> Cipher): ByteArray {
        return try {
            return simplePipeline(firstBlock, {
                cryptoDigest(it, inputStream, maxLength)
            }, { _, byteArray ->
                byteArray
            })
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    fun cryptoDigest(byteArray: ByteArray, maxLength: Int, firstBlock: () -> Cipher): ByteArray {
        return try {
            return simplePipeline(firstBlock, {
                cryptoDigest(it, byteArray, maxLength)
            }, { _, inputByteArray ->
                inputByteArray
            })
        } catch (e: Throwable) {
            ByteArray(0)
        }
    }

    private fun cryptoDigest(cipher: Cipher, inputStream: InputStream, maxLength: Int): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        inputStream.use {
            var leftLength: Int = it.available()
            while (leftLength > 0) {
                var bufferLength: Int = maxLength
                if (leftLength < maxLength) {
                    bufferLength = leftLength
                }
                val buffer = ByteArray(bufferLength)
                val length = it.read(buffer, 0, buffer.size)
                leftLength -= length
                byteArrayOutputStream.write(cipher.doFinal(buffer))
            }
        }
        return byteArrayOutputStream.toByteArray()
    }

    private fun cryptoDigest(cipher: Cipher, byteArray: ByteArray, maxLength: Int): ByteArray {
        return cryptoDigest(cipher, ByteArrayInputStream(byteArray), maxLength)
    }
}

fun main() {
    val keyPair = Rsa.generateKeyPair()
    val privateKey = keyPair.private
    val publicKey = keyPair.public
    val string = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    println(string.toByteArray(Charsets.UTF_8).size)
    val encryptByteArray: ByteArray = Rsa.encrypt(string.toByteArray(Charsets.UTF_8), privateKey)
    println(encryptByteArray.toHexString())
    println(String(Rsa.decrypt(encryptByteArray, publicKey), Charsets.UTF_8))
}