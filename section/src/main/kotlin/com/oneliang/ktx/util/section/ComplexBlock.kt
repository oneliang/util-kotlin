package com.oneliang.ktx.util.section

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

abstract class ComplexBlock() : UnitBlock() {
    companion object {
        private val logger = LoggerManager.getLogger(ComplexBlock::class)
    }

    protected var beforeByteArray: ByteArray = ByteArray(0)
    protected var generateBlockList: MutableList<Block> = CopyOnWriteArrayList()

    constructor(beforeByteArray: ByteArray) : this() {
        this.beforeByteArray = beforeByteArray
    }

    override fun parse(inputStream: InputStream) {
        var index = 0
        val byteArrayInputStream = ByteArrayInputStream(this.beforeByteArray)
        val blockWrapperQueue = this.parseBlockWrapperQueue
        while (!blockWrapperQueue.isEmpty()) {
            val blockWrapper = blockWrapperQueue.poll()
            val id = blockWrapper.id
            val block = blockWrapper.block
            beforeRead(index, id, block)
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
            this.totalSize += block.totalSize
            afterRead(index, id, block)
            //				log("index:"+index+",value:"+StringUtil.byteToHexString(buffer));
            index++
        }
    }

    /**
     * to byte array
     * @return byte[]
     */
    override fun toByteArray(): ByteArray {
        return this.generateBlockList.toByteArray()
    }

    /**
     * get parse block queue
     * @return Queue<BlockWrapper>
    </BlockWrapper> */
    protected abstract val parseBlockWrapperQueue: Queue<BlockWrapper>

    /**
     * before read default empty method
     * @param currentIndex
     * @param currentId
     * @param currentBlock
     */
    protected open fun beforeRead(currentIndex: Int, currentId: Int, currentBlock: Block?) {
    }

    /**
     * after read
     * @param currentIndex
     * @param currentId
     * @param currentBlock
     */
    protected open fun afterRead(currentIndex: Int, currentId: Int, currentBlock: Block) {
        this.generateBlockList.add(currentBlock)
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