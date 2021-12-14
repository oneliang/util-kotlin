package com.oneliang.ktx.util.common

import javax.xml.bind.DatatypeConverter

/**
 * use Base64Codec to encode
 */
fun ByteArray.encodeToBase64(): String = Base64Codec.encode(this)

/**
 * use Base64Codec to decode
 */
fun String.decodeFromBase64(): ByteArray = Base64Codec.decode(this)

object Base64Codec {

    fun encode(data: ByteArray): String {
        return DatatypeConverter.printBase64Binary(data)
    }

    fun decode(encoded: String): ByteArray {
        return DatatypeConverter.parseBase64Binary(encoded)
    }
}