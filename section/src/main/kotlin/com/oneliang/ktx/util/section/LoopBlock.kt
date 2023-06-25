package com.oneliang.ktx.util.section

import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

abstract class LoopBlock() : UnitBlock() {
    companion object {
        private val logger = LoggerManager.getLogger(LoopBlock::class)
    }

    protected var beforeByteArray: ByteArray = ByteArray(0)
    protected var generateBlockList: MutableList<Block> = CopyOnWriteArrayList()

    constructor(beforeByteArray: ByteArray) : this() {
        this.beforeByteArray = beforeByteArray
    }

    override fun parse(inputStream: InputStream) {
        val byteArrayInputStream = ByteArrayInputStream(this.beforeByteArray)
        val blockWrapperList = this.parseBlockWrapperList
        var canReadNext = true
        var index = 0
        this.totalSize = 0
        do {
            for ((blockIndex, blockWrapper) in blockWrapperList.withIndex()) {
                val id = blockWrapper.id
                val block = blockWrapper.block
                try {
                    if (byteArrayInputStream.available() > 0) {
                        block.parse(byteArrayInputStream)
                    } else {
                        block.parse(inputStream)
                    }
                } catch (e: Exception) {
                    logger.error(this.toString(), e)
                    throw e
                }
                afterRead(index, id, block)
                if (inputStream.available() <= 0) {
                    canReadNext = false
                    break
                }
            }
            this.totalSize++
            index++
        } while (canReadNext)
    }

    /**
     * to byte array
     * @return byte[]
     */
    override fun toByteArray(): ByteArray {
        return this.generateBlockList.toByteArray()
    }

    /**
     * get parse block list
     * @return Queue<BlockWrapper>
    </BlockWrapper> */
    protected abstract val parseBlockWrapperList: List<BlockWrapper>

    /**
     * after read
     * @param currentIndex
     * @param currentId
     * @param currentBlock
     */
    protected open fun afterRead(currentIndex: Int, currentId: Int, currentBlock: Block) {
        generateBlockList.add(currentBlock)
    }
    /**
     * get value,just implement in UnitBlock,please use method toByteArray();
     * @return the value
     */
    /**
     * set value,just implement in UnitBlock
     * @param value
     */
    override var value: ByteArray
        get() {
            error("Not implement in " + this.javaClass + ",just implement in " + super.hashCode() + ",please use method toByteArray()")
        }
        set(value) {
            error("Not implement in " + this.javaClass + ",just implement in " + super.hashCode())
        }
}