package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.MD5String
import com.oneliang.ktx.util.common.differs
import com.oneliang.ktx.util.concurrent.LoopThread
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class FileDetector(
    private val directory: String,
    private val fileSuffix: String
) : LoopThread() {
    companion object {
        private val logger = LoggerManager.getLogger(FileDetector::class)
        private const val THREAD_SLEEP_TIME = 30 * Constants.Time.MILLISECONDS_OF_SECOND
    }

    private val directoryFile = File(this.directory)
    private val latestFileMd5Map = ConcurrentHashMap<String, String>()
    lateinit var detectProcessor: DetectProcessor

    override fun looping() {
        val currentFileMd5Map = mutableMapOf<String, String>()
        this.directoryFile.findMatchFile(FileUtil.MatchOption().also {
            it.fileSuffix = fileSuffix
            it.deepMatch = false
            it.includeHidden = true
        }) {
            val filePath = it.absolutePath
            currentFileMd5Map[filePath] = it.MD5String()
            filePath
        }
        val deleteKeyList = this.latestFileMd5Map.differs(currentFileMd5Map)
        val addKeyList = currentFileMd5Map.differs(this.latestFileMd5Map)
        for (deleteKey in deleteKeyList) {
            this.latestFileMd5Map.remove(deleteKey)
        }
        for (addKey in addKeyList) {
            val fileMd5 = currentFileMd5Map[addKey]!!
            this.latestFileMd5Map[addKey] = fileMd5
            this.detectProcessor.afterUpdateFileProcess(addKey)
        }
        Thread.sleep(THREAD_SLEEP_TIME)
    }

    interface DetectProcessor {
        fun afterUpdateFileProcess(filePath: String)
    }
}

fun main() {
    val fileDetector = FileDetector("D:/", ".jar")
    fileDetector.detectProcessor = object : FileDetector.DetectProcessor {
        override fun afterUpdateFileProcess(filePath: String) {
            println(filePath)
        }
    }
    fileDetector.start()
}