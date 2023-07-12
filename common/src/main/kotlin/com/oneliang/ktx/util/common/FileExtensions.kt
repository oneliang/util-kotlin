package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.File
import java.io.RandomAccessFile


fun File.read(start: Long, end: Long, afterReadBlock: (buffer: ByteArray, length: Int) -> Unit) {
    val randomAccessFile = RandomAccessFile(this, "r")
    val fileLength = randomAccessFile.length()
    if (start > fileLength || end > fileLength) {
        throw IndexOutOfBoundsException("file length:%s, but start:%s, end:%s".format(fileLength, start, end))
    }
    randomAccessFile.use {
        var remainLength = (end - start).toInt()
        randomAccessFile.seek(start)
        while (remainLength > 0) {
            val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
            val readLength = minOf(remainLength, buffer.size)
            val length = randomAccessFile.read(buffer, 0, readLength)
            afterReadBlock(buffer, length)
            remainLength -= length
        }
    }
}

fun File.replace(start: Long, end: Long, data: ByteArray) {
    val newFile = File(this.parent, "%s_%s".format(this.name, "replacing"))
    val newFileOutputStream = newFile.outputStream()
    newFileOutputStream.use {
        this.read(0, start) { buffer, length ->
            it.write(buffer, 0, length)
        }
        it.write(data)
        this.read(end, this.length()) { buffer, length ->
            it.write(buffer, 0, length)
        }
    }
    this.delete()
    newFile.renameTo(this)
}