package com.oneliang.ktx.util.jwt

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.Base64UrlCodec

object JsonWebToken {

    const val JWT = "JWT"

    fun create(headerByteArray: ByteArray, bodyByteArray: ByteArray, block: (signatureString: String) -> ByteArray): String {
        val headerBase64UrlEncode = Base64UrlCodec.encode(headerByteArray)
        val bodyBase64UrlEncode = Base64UrlCodec.encode(bodyByteArray)
        val needToSignatureString = headerBase64UrlEncode + Constants.Symbol.DOT_CHAR + bodyBase64UrlEncode
        val signatureByte = block(needToSignatureString)
        return needToSignatureString + Constants.Symbol.DOT_CHAR + if (signatureByte.isEmpty()) {
            Constants.String.BLANK
        } else {
            Base64UrlCodec.encode(signatureByte)
        }
    }
}