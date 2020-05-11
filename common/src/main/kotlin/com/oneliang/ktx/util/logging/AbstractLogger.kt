package com.oneliang.ktx.util.logging

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.nullToBlank
import com.oneliang.ktx.util.logging.Logger.Level


/**
 * constructor
 *
 * @param level
 */
abstract class AbstractLogger(val level: Level) : Logger {

    companion object {
        private const val STACK_TRACE_BEGIN_INDEX = 4
    }

    var stackTraceMaxSize: Int = 3

    /**
     * verbose
     *
     * @param message
     * @param args
     */
    override fun verbose(message: String, vararg args: Any?) {
        logByLevel(Level.VERBOSE, message, args = *args)
    }

    /**
     * debug
     *
     * @param message
     * @param args
     */
    override fun debug(message: String, vararg args: Any?) {
        logByLevel(Level.DEBUG, message, args = *args)
    }

    /**
     * info
     *
     * @param message
     * @param args
     */
    override fun info(message: String, vararg args: Any?) {
        logByLevel(Level.INFO, message, args = *args)
    }

    /**
     * warning
     *
     * @param message
     * @param args
     */
    override fun warning(message: String, vararg args: Any?) {
        logByLevel(Level.WARNING, message, args = *args)
    }

    /**
     * error
     *
     * @param message
     * @param args
     */
    override fun error(message: String, vararg args: Any?) {
        logByLevel(Level.ERROR, message, args = *args)
    }

    /**
     * error
     *
     * @param message
     * @param throwable
     * @param args
     */
    override fun error(message: String, throwable: Throwable, vararg args: Any?) {
        logByLevel(Level.ERROR, message, throwable, args = *args)
    }

    /**
     * fatal
     *
     * @param message
     * @param args
     */
    override fun fatal(message: String, vararg args: Any?) {
        logByLevel(Level.FATAL, message, args = *args)
    }

    /**
     * log by level
     *
     * @param level
     * @param message
     * @param throwable
     * @param args
     */
    private fun logByLevel(level: Level, message: String, throwable: Throwable? = null, vararg args: Any?) {
        if (level.ordinal >= this.level.ordinal) {
            val extraInfo = ExtraInfo()
            val stackTraceArray = Thread.currentThread().stackTrace
            if (stackTraceArray.size > STACK_TRACE_BEGIN_INDEX) {
                val stackTrace = stackTraceArray[STACK_TRACE_BEGIN_INDEX]
                val currentThread = Thread.currentThread()
                extraInfo.logTime = System.currentTimeMillis()
                extraInfo.threadName = currentThread.name
                extraInfo.threadId = currentThread.id
                extraInfo.className = stackTrace.className
                extraInfo.methodName = stackTrace.methodName
                extraInfo.lineNumber = stackTrace.lineNumber
                extraInfo.filename = stackTrace.fileName.nullToBlank()
                extraInfo.stackTraceInfo = generateStackTraceInfo(stackTraceArray)
            }
            if (args.isEmpty()) {
                log(level, message, throwable, extraInfo)
            } else {
                log(level, message.format(*args), throwable, extraInfo)
            }
        }
    }

    /**
     * log
     * @param level
     * @param message
     * @param throwable
     * @param extraInfo
     */
    abstract fun log(level: Level, message: String, throwable: Throwable?, extraInfo: ExtraInfo)

    private fun generateStackTraceInfo(stackTraceArray: Array<StackTraceElement>): String {
        val stringBuilder = StringBuilder()
        if (stackTraceArray.size > STACK_TRACE_BEGIN_INDEX) {
            val endIndex = if (stackTraceArray.size > (STACK_TRACE_BEGIN_INDEX + stackTraceMaxSize - 1)) {
                STACK_TRACE_BEGIN_INDEX + stackTraceMaxSize - 1//include begin and end
            } else {
                stackTraceArray.size - 1
            }
            for (i in STACK_TRACE_BEGIN_INDEX..endIndex) {
                val stackTrace = stackTraceArray[i]
                stringBuilder.append(Constants.Symbol.BRACKET_LEFT + stackTrace.fileName.nullToBlank() + Constants.Symbol.COLON + stackTrace.lineNumber + Constants.Symbol.BRACKET_RIGHT)
            }
        }
        return stringBuilder.toString()
    }

    class ExtraInfo {
        var logTime = 0L
        var threadName = Constants.String.BLANK
        var threadId: Long = 0
        var className = Constants.String.BLANK
        var methodName = Constants.String.BLANK
        var lineNumber = 0
        var filename = Constants.String.BLANK
        var stackTraceInfo = Constants.String.BLANK
    }
}