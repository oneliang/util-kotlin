package com.oneliang.ktx.util.resource

class ResourcePoolException : Exception {

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
