package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.annotation.ThreadSafe
import com.oneliang.ktx.annotation.ThreadUnsafe
import com.oneliang.ktx.pojo.ByteArrayWrapper
import com.oneliang.ktx.pojo.LongWrapper
import java.util.concurrent.locks.ReentrantLock

abstract class AtomicBinary<DATA : Any>(
    private val initializeSize: Int,
    private val expandSize: Int = 10000,
    private val dataLength: Int,
    private val byteArrayToData: (byteArray: ByteArray) -> DATA,
    private val dataToByteArray: (data: DATA) -> ByteArray
) : Iterable<DATA> {

    companion object {
        private const val LENGTH_EXIST = 1//one byte, maybe one bit is better than one byte for memory
    }

    private val binaryDataLength = LENGTH_EXIST + this.dataLength

    init {
        if (this.initializeSize * this.binaryDataLength <= 0) {
            error(
                "initialize size maybe too large or equal 0, now max size is %s, %s * (%s + %s) must be less than %s".format(
                    this.initializeSize,
                    this.initializeSize,
                    LENGTH_EXIST,
                    this.dataLength,
                    Int.MAX_VALUE
                )
            )
        }
        if (this.expandSize * this.binaryDataLength <= 0) {
            error(
                "expand size maybe too large or equal 0, now max size is %s, %s * (%s + %s) must be less than %s".format(
                    this.expandSize,
                    this.expandSize,
                    LENGTH_EXIST,
                    this.dataLength,
                    Int.MAX_VALUE
                )
            )
        }
    }

    private val byteArrayWrapperList = mutableListOf<ByteArrayWrapper>()

    init {
        this.byteArrayWrapperList += ByteArrayWrapper(this.initializeSize * this.binaryDataLength)
    }

    private val autoExpandLock = ReentrantLock()

    /**
     * get suitable real index and byte array wrapper
     * @param index
     * @return Pair<Int, ByteArrayWrapper>
     */
    private fun getSuitableRealIndexAndByteArrayWrapper(index: Long): Pair<Int, ByteArrayWrapper> {
        return if (0 <= index && index < this.initializeSize.toLong()) {
            index.toInt() to this.byteArrayWrapperList[0]
        } else {
            val expandIndexInList = (index - this.initializeSize.toLong()) / this.expandSize.toLong() + 1
            val realIndex = ((index - this.initializeSize.toLong()) % this.expandSize.toLong()).toInt()
            if (expandIndexInList < this.byteArrayWrapperList.size) {
                realIndex to this.byteArrayWrapperList[expandIndexInList.toInt()]
            } else {//auto expand
                try {
                    this.autoExpandLock.lock()
                    //double check, because size will be changed in previous execute
                    if (expandIndexInList < this.byteArrayWrapperList.size) {
                        realIndex to this.byteArrayWrapperList[expandIndexInList.toInt()]
                    } else {
                        val needToIncreaseSize = expandIndexInList - (this.byteArrayWrapperList.size - 1)
                        (0 until needToIncreaseSize).forEach { _ ->
                            this.byteArrayWrapperList += ByteArrayWrapper(this.expandSize * this.binaryDataLength)
                        }
                        realIndex to this.byteArrayWrapperList[expandIndexInList.toInt()]
                    }
                } finally {
                    this.autoExpandLock.unlock()
                }
            }
        }
    }

    /**
     * operate
     * @param index
     * @param create
     * @param update
     * @return DATA
     */
    @ThreadSafe
    fun operate(index: LongWrapper, create: () -> DATA, update: ((DATA) -> DATA)? = null): DATA {
        val (realIndex, byteArrayWrapper) = getSuitableRealIndexAndByteArrayWrapper(index.value)
        val byteOffset = (realIndex * this.binaryDataLength)
        return synchronized(index) {
            val existByte = byteArrayWrapper.read(byteOffset, LENGTH_EXIST)[0]
            if (existByte > 0) {//exist
                val oldData = this.byteArrayToData(byteArrayWrapper.read(byteOffset + LENGTH_EXIST, this.dataLength))
                if (update != null) {
                    val newData = update(oldData)
                    val newDataByteArray = this.dataToByteArray(newData)
                    if (newDataByteArray.size != this.dataLength) {
                        error("new data size is not equal %s when update".format(this.dataLength))
                    }
                    byteArrayWrapper.write(byteOffset + LENGTH_EXIST, newDataByteArray)
                    newData
                } else {
                    oldData
                }
            } else {//not exist
                val existByteArray = ByteArray(1) { 1.toByte() }
                byteArrayWrapper.write(byteOffset, existByteArray)
                var newData = create()
                if (update != null) {
                    newData = update(newData)
                }
                val newDataByteArray = this.dataToByteArray(newData)
                if (newDataByteArray.size != this.dataLength) {
                    error("new data size is not equal %s when create".format(this.dataLength))
                }
                byteArrayWrapper.write(byteOffset + LENGTH_EXIST, newDataByteArray)
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
//        val realIndex = (index - this.indexOffset)
//        val byteOffset = (realIndex * this.binaryDataLength).toInt()
        val (realIndex, byteArrayWrapper) = getSuitableRealIndexAndByteArrayWrapper(index)
        val byteOffset = (realIndex * this.binaryDataLength)
        return this.byteArrayToData(byteArrayWrapper.read(byteOffset + LENGTH_EXIST, this.dataLength))
    }

    /**
     * iterator
     * @return Iterator<DATA>
     */
    @ThreadUnsafe
    override fun iterator(): Iterator<DATA> {
        val totalSize = this.initializeSize + this.expandSize * (this.byteArrayWrapperList.size - 1)
        return AtomicBinaryIterator(
            totalSize,
            this::get
        )
    }

    private class AtomicBinaryIterator<DATA : Any>(
        private val totalSize: Int,
        private val getData: (index: Long) -> DATA
    ) : Iterator<DATA> {

        private var currentIndex = 0L

        override fun next(): DATA {
            val data = this.getData(this.currentIndex)
            this.currentIndex++
            return data
        }

        override fun hasNext(): Boolean {
            return this.currentIndex <= this.totalSize - 1
        }

    }
}