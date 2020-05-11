package com.oneliang.ktx.util.logging

/**
 * @author oneliang
 */
interface Logger {
    enum class Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL
    }

    /**
     * verbose
     *
     * @param message
     * @param args
     */
    fun verbose(message: String, vararg args: Any?)

    /**
     * debug
     *
     * @param message
     * @param args
     */
    fun debug(message: String, vararg args: Any?)

    /**
     * info
     *
     * @param message
     * @param args
     */
    fun info(message: String, vararg args: Any?)

    /**
     * warning
     *
     * @param message
     * @param args
     */
    fun warning(message: String, vararg args: Any?)

    /**
     * error
     *
     * @param message
     * @param args
     */
    fun error(message: String, vararg args: Any?)

    /**
     * error
     *
     * @param message
     * @param throwable
     * @param args
     */
    fun error(message: String, throwable: Throwable, vararg args: Any?)

    /**
     * fatal
     *
     * @param message
     * @param args
     */
    fun fatal(message: String, vararg args: Any?)

    /**
     * destroy
     */
    fun destroy()
}