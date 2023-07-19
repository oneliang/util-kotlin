package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.replace
import com.oneliang.ktx.util.concurrent.atomic.OperationLock
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

class FileWrapper(private val fullFilename: String, private val accessMode: AccessMode = AccessMode.RW) {
    companion object {
        private val logger = LoggerManager.getLogger(FileWrapper::class)
    }

    private lateinit var file: RandomAccessFile
    private val readLock = OperationLock()
    private val writeLock = OperationLock()

    @Volatile
    private var replacing = false

    enum class AccessMode(val value: String) {
        R("r"), RW("rw"), RWS("rws"), RWD("rwd")
    }

    init {
        resetFile()
    }

    /**
     * reset file
     */
    private fun resetFile() {
        val file = File(this.fullFilename)
        if (!file.exists()) {
            file.createFileIncludeDirectory()
        }
        this.file = RandomAccessFile(file, this.accessMode.value)
    }

    /**
     * read
     * @param start
     * @param end
     * @return ByteArray
     */
    fun read(start: Long, end: Long): ByteArray {
        val block: () -> ByteArray = {
            val length = (end - start).toInt()
            val byteBuffer = ByteBuffer.allocate(length)
            this.file.channel.read(byteBuffer, start)
            byteBuffer.array()
        }
        if (this.replacing) {//when replacing, lock replace and read
            return this.readLock.operate {
                block()
            }
        }
        return block()
    }

    /**
     * write
     * @param data
     * @param position, specify the start, use in some special business scene
     * @return Pair<Long, Long>
     */
    fun write(data: ByteArray, position: Long = -1): Pair<Long, Long> {
        return this.writeLock.operate {
            val startPosition = if (position > -1) {
                position
            } else {
                this.file.length()
            }
            logger.verbose("write, start:%s, length:%s, data.size:%s, file:%s, hashcode:%s", startPosition, this.file.length(), data.size, this.fullFilename, this.hashCode())
            val byteBuffer = ByteBuffer.wrap(data)
            this.file.channel.write(byteBuffer, startPosition)
            val end = this.file.length()
            startPosition to end
        }
    }

    /**
     * replace
     * @param start
     * @param end
     * @param data
     * @return Pair<Long, Long>
     */
    fun replace(start: Long, end: Long, data: ByteArray): Pair<Long, Long> {
        return this.writeLock.operate {
            try {
                this.replacing = true
                val file = File(this.fullFilename)
                file.replace(start, end, data, this.readLock.lock) {
                    resetFile()
                }
            } finally {//finally finished replacing
                this.replacing = false
            }
            start to start + data.size
        }
    }

    /**
     * length
     * @return Long
     */
    fun length(): Long {
        return this.writeLock.operate {
            this.file.length()
        }
    }

    /**
     * close
     */
    fun close() {
        try {
            this.file.close()
        } catch (e: Throwable) {
            logger.error(Constants.String.EXCEPTION, e)
        }
    }

    /**
     * finalize
     */
    fun finalize() {
        this.close()
    }
}