package com.oneliang.ktx.exception

/**
 * initialize
 */
class InitializeException : RuntimeException {

    /**
     * @param message
     */
    constructor(message: String) : super(message)

    /**
     * @param cause
     */
    constructor(cause: Throwable) : super(cause)

    /**
     * @param message
     * @param cause
     */
    constructor(message: String, cause: Throwable) : super(message, cause)
}
