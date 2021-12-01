package com.oneliang.ktx.util.common

import javax.xml.bind.DatatypeConverter

object Base64Codec {

    fun encode(data: ByteArray): String {
        return DatatypeConverter.printBase64Binary(data)
    }

    fun decode(encoded: String): ByteArray {
        return DatatypeConverter.parseBase64Binary(encoded)
    }
}