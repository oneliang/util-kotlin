package com.oneliang.ktx.util.section

import java.io.InputStream

interface Block : Section {
    /**
     * set initial size
     * @param initialSize
     */
    var initialSize: Int
    /**
     * @return the value
     */
    /**
     * set value
     * @param value
     */
    var value: ByteArray

    /**
     * @return the totalSize
     */
    val totalSize: Int

    /**
     * parse
     *
     * @param inputStream
     * @throws Exception
     */
    @Throws(Exception::class)
    fun parse(inputStream: InputStream)
}