package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.pojo.ByteArrayWrapper
import com.oneliang.ktx.pojo.LongWrapper
import com.oneliang.ktx.util.common.toByteArray
import com.oneliang.ktx.util.common.toInt

class AtomicBinary(maxSize: Int, private val indexOffset: Long = 0L) {

    companion object {
        private const val LENGTH_EXIST = 1//one byte, maybe one bit is better than one byte
        private const val LENGTH_BODY = 4
        private const val LENGTH = LENGTH_EXIST + LENGTH_BODY
    }

    init {
        if ((maxSize * LENGTH) <= 0) {
            error("max size maybe too large or equal 0, now max size is %s, %s * %s must be less than %s".format(maxSize, maxSize, LENGTH, Int.MAX_VALUE))
        }
    }

    private val byteArrayWrapper = ByteArrayWrapper(maxSize * LENGTH)

    /**
     * operate
     * @param index
     * @param create
     * @param update
     * @return Int
     */
    fun operate(index: LongWrapper, create: () -> Int, update: ((Int) -> Int)? = null): Int {
        val realIndex = (index.value - this.indexOffset)
        val byteOffset = (realIndex * LENGTH).toInt()
        return synchronized(index) {
            val existByte = this.byteArrayWrapper.read(byteOffset, LENGTH_EXIST)[0]
            if (existByte > 0) {//exist
                val oldData = this.byteArrayWrapper.read(byteOffset + LENGTH_EXIST, LENGTH_BODY).toInt()
                if (update != null) {
                    val newData = update(oldData)
                    val newDataByteArray = newData.toByteArray()
                    if (newDataByteArray.size != LENGTH_BODY) {
                        error("new data size is not equal %s when update".format(LENGTH_BODY))
                    }
                    this.byteArrayWrapper.write(byteOffset + LENGTH_EXIST, newDataByteArray)
                    newData
                } else {
                    oldData
                }
            } else {//not exist
                val existByteArray = ByteArray(1) { 1.toByte() }
                this.byteArrayWrapper.write(byteOffset, existByteArray)
                var newData = create()
                if (update != null) {
                    newData = update(newData)
                }
                val newDataByteArray = newData.toByteArray()
                if (newDataByteArray.size != LENGTH_BODY) {
                    error("new data size is not equal %s when create".format(LENGTH_BODY))
                }
                this.byteArrayWrapper.write(byteOffset + LENGTH_EXIST, newDataByteArray)
                newData
            }
        }
    }

    /**
     * get
     * @param index
     * @return ByteArray
     */
    operator fun get(index: Long): ByteArray {
        val realIndex = (index - this.indexOffset)
        val byteOffset = (realIndex * LENGTH).toInt()
        return this.byteArrayWrapper.read(byteOffset + LENGTH_EXIST, LENGTH_BODY)
    }
}