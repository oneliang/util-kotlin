package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.InputStream
import javax.crypto.Cipher

object CryptoUtil {

    fun cryptoDigest(inputStream: InputStream, firstBlock: () -> Cipher): ByteArray {
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

    fun cryptoDigest(byteArray: ByteArray, firstBlock: () -> Cipher): ByteArray {
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

    private fun cryptoDigest(cipher: Cipher, inputStream: InputStream) {
        inputStream.use {
            val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
            var readCount: Int
            readCount = inputStream.read(buffer, 0, buffer.size)
            while (readCount != -1) {
                cipher.update(buffer, 0, readCount)
                readCount = inputStream.read(buffer, 0, buffer.size)
            }
        }
    }

    private fun cryptoDigest(cipher: Cipher, byteArray: ByteArray) {
        cipher.update(byteArray, 0, byteArray.size)
    }
}