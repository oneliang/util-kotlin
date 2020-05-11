package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * read blocking
 */
fun InputStream.readWithBuffer(readLength: Int, outputStream: OutputStream) {
    var last = readLength
    while (last > 0) {
        val buffer = if (last >= Constants.Capacity.BYTES_PER_KB) {
            ByteArray(Constants.Capacity.BYTES_PER_KB)
        } else {
            ByteArray(last)
        }
        val length = this.read(buffer, 0, buffer.size)
        if (length > 0) {
            outputStream.write(buffer, 0, length)
            outputStream.flush()
            last -= length
        }
    }
}

/**
 * read blocking
 */
fun InputStream.readWithBuffer(readLength: Int): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.readWithBuffer(readLength, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

/**
 * write blocking and use buffer
 */
fun OutputStream.writeWithBuffer(byteArray: ByteArray) {
    val totalLength = byteArray.size
    var offset = 0
    var last = totalLength
    while (last > 0) {
        offset += if (last >= Constants.Capacity.BYTES_PER_KB) {
            this.write(byteArray, offset, Constants.Capacity.BYTES_PER_KB)
            this.flush()
            Constants.Capacity.BYTES_PER_KB
        } else {
            this.write(byteArray, offset, last)
            this.flush()
            last
        }
        last = totalLength - offset
    }
}