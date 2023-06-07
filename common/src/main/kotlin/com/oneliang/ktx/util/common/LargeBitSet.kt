package com.oneliang.ktx.util.common

/**
 * only mask 0 or 1 for every index
 */
class LargeBitSet(private val bitSetCount: Long) {
    companion object {
        private const val MAX_DATA_COUNT = Int.MAX_VALUE.toLong() shl 6
    }

    private var bitSet: LongArray

    init {
        if (this.bitSetCount <= 0 || this.bitSetCount > MAX_DATA_COUNT) {
            throw IllegalArgumentException("dataCount must be 0 .. $MAX_DATA_COUNT; got: $bitSetCount")
        }
        val size = ((this.bitSetCount - 1) shr (6) + 1).toInt()
        this.bitSet = LongArray(size)
    }

    fun set(index: Long) {
        assert(index in 0 until this.bitSetCount) { "index=$index bitSetCount=$bitSetCount" }
        val position = (index shr 6).toInt() // div 64
        val bitmask = 1L shl index.toInt()
        this.bitSet[position] = this.bitSet[position] or bitmask
    }

    fun get(index: Long): Boolean {
        assert(index in 0 until this.bitSetCount) { "index=$index, numBits=$bitSetCount" }
        val position: Int = (index shr 6).toInt() // div 64
        // signed shift will keep a negative index and force an
        // array-index-out-of-bounds-exception, removing the need for an explicit check.
        // signed shift will keep a negative index and force an
        // array-index-out-of-bounds-exception, removing the need for an explicit check.
        val bitmask = 1L shl index.toInt()
        return bitSet[position] and bitmask != 0L
    }
}