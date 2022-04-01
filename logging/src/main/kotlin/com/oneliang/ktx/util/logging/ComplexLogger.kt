package com.oneliang.ktx.util.logging

import com.oneliang.ktx.util.concurrent.ResourceQueueThread

class ComplexLogger(level: Logger.Level, private val loggerList: List<AbstractLogger>, private val async: Boolean = false) : AbstractLogger(level) {

    private var logQueueThread: ResourceQueueThread<LogMessage>? = null

    init {
        if (this.async) {
            this.logQueueThread = ResourceQueueThread(object : ResourceQueueThread.ResourceProcessor<LogMessage> {
                override fun process(resource: LogMessage) {
                    realLog(resource.level, resource.message, resource.throwable, resource.extraInfo)
                }
            })
            this.logQueueThread?.start()
        }
    }

    /**
     * real log
     */
    override fun log(level: Logger.Level, message: String, throwable: Throwable?, extraInfo: ExtraInfo) {
        if (this.async) {
            this.logQueueThread?.addResource(LogMessage(level, message, throwable, extraInfo))
        } else {
            this.realLog(level, message, throwable, extraInfo)
        }
    }

    /**
     * real log
     */
    private fun realLog(level: Logger.Level, message: String, throwable: Throwable?, extraInfo: ExtraInfo) {
        for (logger in this.loggerList) {
            if (level.ordinal >= logger.level.ordinal) {
                logger.log(level, message, throwable, extraInfo)
            }
        }
    }

    override fun destroy() {
        for (logger in this.loggerList) {
            logger.destroy()
        }
        this.logQueueThread?.stop()
    }

    private class LogMessage(val level: Logger.Level, val message: String, val throwable: Throwable?, val extraInfo: ExtraInfo)
}