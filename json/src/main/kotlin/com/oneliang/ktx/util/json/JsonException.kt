package com.oneliang.ktx.util.json

class JsonException : RuntimeException {
    /**
     * Constructs a JsonException with an explanatory message.
     * @param message Detail about the reason for the exception.
     */
    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)

    constructor(message: String, cause: Throwable) : super(message, cause)
}