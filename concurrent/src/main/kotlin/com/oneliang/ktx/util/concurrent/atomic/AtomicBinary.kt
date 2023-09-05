package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.annotation.ThreadSafe
import com.oneliang.ktx.annotation.ThreadUnsafe
import com.oneliang.ktx.pojo.ByteArrayWrapper
import com.oneliang.ktx.pojo.LongWrapper

abstract class AtomicBinary<DATA : Any>(
    private val maxSize: Int,
    private val dataLength: Int,
    private val indexOffset: Long = 0L,
    private val byteArrayToData: (byteArray: ByteArray) -> DATA,
    private val dataToByteArray: (data: DATA) -> ByteArray
) : Iterable<DATA> {

    companion object {
        private const val LENGTH_EXIST = 1//one byte, maybe one bit is better than one byte for memory
    }

    private val binaryDataLength = LENGTH_EXIST + this.dataLength

    init {
        if (maxSize * this.binaryDataLength <= 0) {
            error("max size maybe too large or equal 0, now max size is %s, %s * (%s + %s) must be less than %s".format(maxSize, maxSize, LENGTH_EXIST, this.dataLength, Int.MAX_VALUE))
        }
    }

    private val byteArrayWrapper = ByteArrayWrapper(maxSize * this.binaryDataLength)

    /**
     * operate
     * @param index
     * @param create
     * @param update
     * @return DATA
     */
    @ThreadSafe
    fun operate(index: LongWrapper, create: () -> DATA, update: ((DATA) -> DATA)? = null): DATA {
        val realIndex = (index.value - this.indexOffset)
        val byteOffset = (realIndex * this.binaryDataLength).toInt()
        return synchronized(index) {
            val existByte = this.byteArrayWrapper.read(byteOffset, LENGTH_EXIST)[0]
            if (existByte > 0) {//exist
                val oldData = this.byteArrayToData(this.byteArrayWrapper.read(byteOffset + LENGTH_EXIST, this.dataLength))
                if (update != null) {
                    val newData = update(oldData)
                    val newDataByteArray = this.dataToByteArray(newData)
                    if (newDataByteArray.size != this.dataLength) {
                        error("new data size is not equal %s when update".format(this.dataLength))
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
                val newDataByteArray = this.dataToByteArray(newData)
                if (newDataByteArray.size != this.dataLength) {
                    error("new data size is not equal %s when create".format(this.dataLength))
                }
                this.byteArrayWrapper.write(byteOffset + LENGTH_EXIST, newDataByteArray)
                newData
            }
        }
    }

    /**
     * get
     * @param index
     * @return DATA
     */
    @ThreadUnsafe
    operator fun get(index: Long): DATA {
        val realIndex = (index - this.indexOffset)
        val byteOffset = (realIndex * this.binaryDataLength).toInt()
        return this.byteArrayToData(this.byteArrayWrapper.read(byteOffset + LENGTH_EXIST, this.dataLength))
    }

    /**
     * iterator
     * @return Iterator<DATA>
     */
    @ThreadUnsafe
    override fun iterator(): Iterator<DATA> {
        return AtomicBinaryIterator(
            this.maxSize,
            this.indexOffset,
            this::get
        )
    }

    private class AtomicBinaryIterator<DATA : Any>(
        private val maxSize: Int,
        private val indexOffset: Long = 0L,
        private val getData: (index: Long) -> DATA
    ) : Iterator<DATA> {

        private var currentIndex = 0

        override fun next(): DATA {
            val index = currentIndex + this.indexOffset
            val data = this.getData(index)
            this.currentIndex++
            return data
        }

        override fun hasNext(): Boolean {
            return this.currentIndex <= this.maxSize - 1
        }

    }
}