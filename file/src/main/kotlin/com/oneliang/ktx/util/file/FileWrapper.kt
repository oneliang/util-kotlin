package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.replace
import com.oneliang.ktx.util.concurrent.atomic.OperationLock
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.File
import java.io.RandomAccessFile

class FileWrapper(private val fullFilename: String, private val accessMode: AccessMode = AccessMode.RW) {
    companion object {
        private val logger = LoggerManager.getLogger(FileWrapper::class)
    }

    private lateinit var file: RandomAccessFile
    private val readLock = OperationLock()
    private val writeLock = OperationLock()


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
        this.file = RandomAccessFile(this.fullFilename, this.accessMode.value)
    }

    /**
     * read
     * @param start
     * @param end
     * @return ByteArray
     */
    fun read(start: Long, end: Long): ByteArray {
        return this.readLock.operate {
            val length = (end - start).toInt()
            val data = ByteArray(length)
            this.file.seek(start)
            this.file.readFully(data, 0, length)
            data
        }

    }

    /**
     * write
     * @param data
     * @param startPosition, specify the start, use in some special business scene
     * @return Pair<Long, Long>
     */
    fun write(data: ByteArray, startPosition: Long = -1): Pair<Long, Long> {
        return this.writeLock.operate {
            val start = if (startPosition > -1) {
                startPosition
            } else {
                this.file.length()
            }
            this.file.seek(start)
            this.file.write(data)
            val end = this.file.length()
            start to end
        }
    }

    /**
     * replace
     * @param start
     * @param end
     * @param data
     */
    fun replace(start: Long, end: Long, data: ByteArray) {
        this.writeLock.operate {
            val file = File(this.fullFilename)
            file.replace(start, end, data, this.readLock.lock)
            resetFile()
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