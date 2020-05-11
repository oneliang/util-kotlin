package com.oneliang.ktx.util.logging

import com.oneliang.ktx.util.common.matchesPattern
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass

/**
 * @author oneliang
 */
object LoggerManager {
    private val DEFAULT_LOGGER = BaseLogger(Logger.Level.VERBOSE)
    private val loggerMap = ConcurrentHashMap<KClass<*>, Logger>()
    private val patternLoggerMap = ConcurrentHashMap<String, Logger>()
    private val loggerPatternSet = CopyOnWriteArraySet<String>()
    /**
     * get logger
     *
     * @param clazz
     * @return Logger
     */
    fun getLogger(clazz: KClass<*>): Logger {
        var logger = loggerMap[clazz]
        if (logger == null) {
            val className = clazz.java.name
            for (patternKey in loggerPatternSet) {
                if (className.matchesPattern(patternKey)) {
                    logger = patternLoggerMap[patternKey]
                    break
                }
            }
        }
        return logger ?: DEFAULT_LOGGER
    }

    /**
     * register logger
     *
     * @param clazz
     * @param logger
     */
    fun registerLogger(clazz: KClass<*>, logger: Logger) {
        loggerMap[clazz] = logger
    }

    /**
     * register logger
     *
     * @param pattern
     * @param logger
     */
    fun registerLogger(pattern: String, logger: Logger) {
        patternLoggerMap.put(pattern, logger)
        loggerPatternSet.add(pattern)
    }

    /**
     * unregister logger
     *
     * @param clazz
     */
    fun unregisterLogger(clazz: KClass<*>) {
        loggerMap.remove(clazz)
    }

    /**
     * unregister logger
     *
     * @param pattern
     */
    fun unregisterLogger(pattern: String) {
        patternLoggerMap.remove(pattern)
        loggerPatternSet.remove(pattern)
    }

    /**
     * unregister all logger
     */
    fun unregisterAllLogger() {
        loggerMap.forEach { (_, logger) ->
            logger.destroy()
        }
        patternLoggerMap.forEach { (_, logger) ->
            logger.destroy()
        }
        loggerMap.clear()
        patternLoggerMap.clear()
        loggerPatternSet.clear()
    }
}