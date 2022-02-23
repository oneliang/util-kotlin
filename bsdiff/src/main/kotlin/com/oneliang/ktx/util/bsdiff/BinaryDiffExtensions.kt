package com.oneliang.ktx.util.bsdiff

import java.io.File

fun File.binaryDiff(newFile: File, diffFile: File) = BinaryDiff.binaryDiff(this, newFile, diffFile)

fun ByteArray.binaryDiff(newByteArray: ByteArray): ByteArray = BinaryDiff.binaryDiff(this, this.size, newByteArray, newByteArray.size)