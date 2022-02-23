package com.oneliang.ktx.util.bsdiff

import java.io.File
import java.io.RandomAccessFile

fun File.binaryPatch(diffFile: File, newFile: File, extLength: Int = 0) = BinaryPatch.patchFast(this, newFile, diffFile, extLength)

fun ByteArray.binaryPatch(diffByteArray: ByteArray, extLength: Int = 0): ByteArray = BinaryPatch.patchFast(this, this.size, diffByteArray, diffByteArray.size, extLength)

fun RandomAccessFile.binaryPatchLessMemory(diffFile: File, newFile: File, extLength: Int = 0) = BinaryPatch.patchLessMemory(this, newFile, diffFile, extLength)

fun RandomAccessFile.binaryPatchLessMemory(diffByteArray: ByteArray, newFile: File, extLength: Int = 0) = BinaryPatch.patchLessMemory(this, this.length().toInt(), diffByteArray, diffByteArray.size, newFile, extLength)