package com.oneliang.ktx.util.logging

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.getDayZeroTimePrevious
import com.oneliang.ktx.util.common.toFormatString
import com.oneliang.ktx.util.common.toUtilDate
import com.oneliang.ktx.util.file.FileUtil
import com.oneliang.ktx.util.file.deleteAll
import com.oneliang.ktx.util.logging.Logger.Level
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class FileLogger(level: Level,
                 private val directory: File,
                 private val filename: String,
                 private val rule: Rule = Rule.DAY,
                 private val retainDays: Int = 7) : BaseLogger(level) {
    private var currentFileOutputStream: FileOutputStream? = null
    private var currentBeginTime: Long = 0L
    private val logLock = ReentrantLock()

    enum class Rule(val interval: Long, internal val directoryNameFormat: String, val filenameFormat: String) {
        DAY(Constants.Time.MILLISECONDS_OF_DAY,
                Constants.Time.YEAR_MONTH_DAY,
                Constants.Time.YEAR + Constants.Symbol.UNDERLINE + Constants.Time.MONTH + Constants.Symbol.UNDERLINE + Constants.Time.DAY),
        HOUR(Constants.Time.MILLISECONDS_OF_HOUR,
                Constants.Time.YEAR_MONTH_DAY,
                DAY.filenameFormat + Constants.Symbol.UNDERLINE + Constants.Time.HOUR),
        MINUTE(Constants.Time.MILLISECONDS_OF_MINUTE,
                Constants.Time.YEAR_MONTH_DAY,
                HOUR.filenameFormat + Constants.Symbol.UNDERLINE + Constants.Time.MINUTE)
    }

    init {
        val beginDate = Date()
        this.currentBeginTime = beginDate.toFormatString(rule.filenameFormat).toUtilDate(rule.filenameFormat).time
        this.currentFileOutputStream = newFileOutputStream(directory, currentBeginTime, filename, rule)
    }

    override fun log(level: Level, message: String, throwable: Throwable?, extraInfo: ExtraInfo) {
        val logContent = this.generateLogContent(level, message, throwable, extraInfo) + Constants.String.CRLF_STRING
        try {
            val currentTime = System.currentTimeMillis()
            var timeInterval = currentTime - currentBeginTime
            //next time internal
            if (timeInterval >= rule.interval) {
                this.logLock.lock()
                timeInterval = currentTime - currentBeginTime
                //double check, current day may be change, day internal is the same when first in, but second time is not the same
                if (timeInterval >= rule.interval) {
                    currentBeginTime += rule.interval
                    //close current file output stream
                    destroy()
                    //set to new file output stream
                    deleteExpireFile(directory, currentBeginTime, rule)
                    val fileOutputStream = newFileOutputStream(directory, currentBeginTime, filename, rule)
                    destroyCurrentFileOutputStream()//destroy
                    //reset
                    this.currentFileOutputStream = fileOutputStream
                }
                this.logLock.unlock()
            }
            writeLogContent(this.currentFileOutputStream, logContent, throwable)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * delete expire file
     */
    private fun deleteExpireFile(directory: File, currentBeginTime: Long, rule: Rule) {
        val beginDate = Date(currentBeginTime)
        for (i in 30 downTo this.retainDays) {
            val subDirectoryName = beginDate.getDayZeroTimePrevious(i).toUtilDate().toFormatString(rule.directoryNameFormat)
            val subDirectoryFile = File(directory, subDirectoryName)
            subDirectoryFile.deleteAll()
        }
    }

    private fun newFileOutputStream(directory: File, currentBeginTime: Long, filename: String, rule: Rule): FileOutputStream {
        val beginDate = Date(currentBeginTime)
        val subDirectoryName = beginDate.toFormatString(rule.directoryNameFormat)
        val subDirectoryFile = File(directory, subDirectoryName)
        val filenamePrefix = beginDate.toFormatString(rule.filenameFormat)
        val outputFile = File(subDirectoryFile, filenamePrefix + Constants.Symbol.UNDERLINE + filename)
        FileUtil.createFile(outputFile.absolutePath)
        return FileOutputStream(outputFile, true)
    }

    private fun writeLogContent(fileOutputStream: FileOutputStream?, logContent: String, throwable: Throwable?) {
        fileOutputStream ?: return
        fileOutputStream.write(logContent.toByteArray())
        throwable?.printStackTrace(PrintStream(fileOutputStream))
        fileOutputStream.flush()
    }

    private fun destroyCurrentFileOutputStream() {
        try {
            this.currentFileOutputStream?.flush()
            this.currentFileOutputStream?.close()
            this.currentFileOutputStream = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun destroy() {
        this.destroyCurrentFileOutputStream()
    }
}