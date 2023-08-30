package com.oneliang.ktx.pojo

import com.oneliang.ktx.annotation.ThreadUnsafe

class ByteArrayWrapper(private val size: Int) : Iterable<Byte> {

    init {
        if (this.size <= 0) {
            error("size must be greater than 0, now is %s".format(this.size))
        }
    }

    private val byteArray = ByteArray(this.size)

    /**
     * read, thread unsafe
     * @param offset
     * @param size
     * @return ByteArray
     */
    @ThreadUnsafe
    fun read(offset: Int, size: Int): ByteArray {
        val end = offset + size
        if (end > this.size) {
            error("offset+size is large than ByteArray.size")
        }
        val byteArray = ByteArray(size)
        for (i in offset until end) {
            byteArray[i - offset] = this.byteArray[i]
        }
        return byteArray
    }

    /**
     * write, thread unsafe
     * @param offset
     * @param byteArray
     */
    @ThreadUnsafe
    fun write(offset: Int, byteArray: ByteArray) {
        val end = offset + byteArray.size
        for (i in offset until end) {
            this.byteArray[i] = byteArray[i - offset]
        }
    }

    /**
     * size
     * @return Int
     */
    fun size(): Int {
        return this.byteArray.size
    }

    /**
     * iterator
     * @return ByteIterator
     */
    override operator fun iterator(): ByteIterator {
        return this.byteArray.iterator()
    }
}