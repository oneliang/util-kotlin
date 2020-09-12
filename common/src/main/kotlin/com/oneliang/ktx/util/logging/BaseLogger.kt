package com.oneliang.ktx.util.logging

open class BaseLogger(level: Logger.Level) : AbstractLogger(level) {
    /**
     * log
     *
     * @param level
     * @param message
     * @param throwable
     * @param extraInfo
     */
    override fun log(level: Logger.Level, message: String, throwable: Throwable?, extraInfo: ExtraInfo) {
        println(this.generateLogContent(level, message, throwable, extraInfo))
        throwable?.printStackTrace()
    }

    override fun destroy() {}
}

object EmptyLogger : BaseLogger(Logger.Level.ERROR)