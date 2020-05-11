package com.oneliang.ktx.util.common

import com.oneliang.ktx.util.logging.LoggerManager
import kotlin.math.ceil

object PieceUtil {
    private val logger = LoggerManager.getLogger(PieceUtil::class)
    fun split(byteArray: ByteArray, pieceSize: Int, pieceIndex: Int): Pair<ByteArray, Int> {
        val byteArrayTotalSize = byteArray.size
        val pieceCount = ceil(byteArrayTotalSize.toDouble() / pieceSize).toInt()
        return if (pieceCount <= 1) {
            Pair(byteArray, pieceCount)
        } else {
            var tempPieceIndex = pieceIndex
            val length = if (tempPieceIndex < pieceCount - 1) {
                pieceSize
            } else {//last piece
                tempPieceIndex = pieceCount - 1
                byteArrayTotalSize - tempPieceIndex * pieceSize
            }
            val pieceByteArray = ByteArray(length)
            System.arraycopy(byteArray, tempPieceIndex * pieceSize, pieceByteArray, 0, length)
            logger.debug(String.format("piece count:%s, piece index:%s, length:%s, piece md5:%s", pieceCount, tempPieceIndex, length, pieceByteArray.MD5String()))
            Pair(pieceByteArray, pieceCount)
        }
    }

    fun split(byteArray: ByteArray, pieceSize: Int, splitProcessor: (pieceByteArray: ByteArray, pieceCount: Int, pieceIndex: Int) -> Unit) {
        val byteArrayTotalSize = byteArray.size
        val pieceCount = ceil(byteArrayTotalSize.toDouble() / pieceSize).toInt()
        if (pieceCount <= 1) {
            splitProcessor(byteArray, 1, 0)
        } else {
            for (i in 0 until pieceCount) {
                val length = if (i < pieceCount - 1) {
                    pieceSize
                } else {
                    byteArrayTotalSize - i * pieceSize
                }
                val pieceByteArray = ByteArray(length)
                System.arraycopy(byteArray, i * pieceSize, pieceByteArray, 0, length)
                logger.debug(String.format("piece count:%s, piece index:%s, length:%s, piece md5:%s", pieceCount, i, length, pieceByteArray.MD5String()))
                splitProcessor(pieceByteArray, pieceCount, i)
            }
        }
//        Log.i(TAG, " byte array to response result:%s, cost:%s, md5:%s", result, (System.currentTimeMillis() - begin), byteArray.MD5String())
    }

    fun merge(appendedByteArray: ByteArray, pieceByteArray: ByteArray, pieceCount: Int, pieceIndex: Int): Pair<Boolean, ByteArray> {
        logger.debug(String.format("piece count:%s, piece index:%s, length:%s, piece md5:%s", pieceCount, pieceIndex, pieceByteArray.size, pieceByteArray.MD5String()))
        return if (pieceCount == 1) {
//            wholeByteArray = pieceByteArray
            Pair(true, pieceByteArray)
        } else {
            val currentSize = appendedByteArray.size
            val newResponseByteArray = ByteArray(currentSize + pieceByteArray.size)
            System.arraycopy(appendedByteArray, 0, newResponseByteArray, 0, currentSize)
            System.arraycopy(pieceByteArray, 0, newResponseByteArray, currentSize, pieceByteArray.size)
//            wholeByteArray = newResponseByteArray
//            pieceCount - 1 == pieceIndex
            Pair(pieceCount - 1 == pieceIndex, newResponseByteArray)
        }
    }
}