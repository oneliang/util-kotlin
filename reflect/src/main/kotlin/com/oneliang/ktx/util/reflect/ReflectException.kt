package com.oneliang.ktx.util.reflect

class ReflectException : Exception {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)

    override fun toString(): String {
        return if (cause != null) javaClass.name + ": " + cause else super.toString()
    }
}