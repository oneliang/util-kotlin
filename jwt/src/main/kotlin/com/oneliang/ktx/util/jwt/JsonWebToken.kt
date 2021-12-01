package com.oneliang.ktx.util.jwt

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.Base64UrlCodec

object JsonWebToken {

    const val JWT = "JWT"

    fun create(header: String, body: String, block: (signatureString: String) -> String): String {
        val headerBase64UrlEncode = Base64UrlCodec.encode(header.toByteArray(Charsets.US_ASCII))
        val bodyBase64UrlEncode = Base64UrlCodec.encode(body.toByteArray(Charsets.US_ASCII))
        val needToSignatureString = headerBase64UrlEncode + Constants.Symbol.DOT_CHAR + bodyBase64UrlEncode
        val signature = block(needToSignatureString)
        return needToSignatureString + Constants.Symbol.DOT_CHAR + if (signature.isBlank()) {
            Constants.String.BLANK
        } else {
            Base64UrlCodec.encode(signature.toByteArray(Charsets.US_ASCII))
        }
    }
}