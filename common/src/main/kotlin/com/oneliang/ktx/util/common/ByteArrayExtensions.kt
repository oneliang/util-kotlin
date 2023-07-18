package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants

private val hexStringTransform: (byte: Byte) -> CharSequence = { String.format("%02X", (it.toInt() and 0xFF)) }
fun ByteArray.toHexString() = joinToString(separator = Constants.String.BLANK, transform = hexStringTransform)

fun Array<Byte>.toHexString() = joinToString(separator = Constants.String.BLANK, transform = hexStringTransform)

private val binaryStringTransform: (byte: Byte) -> CharSequence = { String.format("%8s", (it.toInt() and 0xFF).toString(radix = 2)).replace(' ', '0') }
fun ByteArray.toBinaryString() = joinToString(separator = Constants.String.BLANK, transform = binaryStringTransform)

fun Array<Byte>.toBinaryString() = joinToString(separator = Constants.String.BLANK, transform = binaryStringTransform)

private val toShort: ((bytes: Array<Byte>) -> Short) = {
    if (it.isNotEmpty() && it.size == 2) {
        var result: Short = 0
        for ((i, byte) in it.reversedArray().withIndex()) {
            result = (result.toInt() or (byte.toInt() and 0xFF shl 8 * i)).toShort()
        }
        result
    } else {
        0
    }
}

fun ByteArray.toShort(): Short = toShort(this.toTypedArray())
fun Array<Byte>.toShort(): Short = toShort(this)

private val toInt: ((bytes: Array<Byte>) -> Int) = {
    if (it.isNotEmpty() && it.size == 4) {
        var result = 0
        for ((i, byte) in it.reversedArray().withIndex()) {
            result = result or (byte.toInt() and 0xFF shl 8 * i)
        }
        result
    } else {
        0
    }
}

fun ByteArray.toInt(): Int = toInt(this.toTypedArray())
fun Array<Byte>.toInt(): Int = toInt(this)

fun ByteArray.toFloat(): Float = toInt(this.toTypedArray()).bitsToFloat()
fun Array<Byte>.toFloat(): Float = toInt(this).bitsToFloat()

private val toLong: ((bytes: Array<Byte>) -> Long) = {
    if (it.isNotEmpty() && it.size == 8) {
        var result = 0L
        for ((i, byte) in it.reversedArray().withIndex()) {
            result = result or (byte.toLong() and 0xFF shl 8 * i)
        }
        result
    } else {
        0
    }
}

fun ByteArray.toLong(): Long = toLong(this.toTypedArray())
fun Array<Byte>.toLong(): Long = toLong(this)

fun ByteArray.toDouble(): Double = this.toLong().bitsToDouble()
fun Array<Byte>.toDouble(): Double = this.toLong().bitsToDouble()
