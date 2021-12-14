package com.oneliang.ktx.util.jwt

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.HmacSHA256
import com.oneliang.ktx.util.common.decodeFromBase64Url
import com.oneliang.ktx.util.common.encodeToBase64Url

fun createJwt(headerByteArray: ByteArray, bodyByteArray: ByteArray, signatureBlock: (signatureString: String) -> ByteArray) = JsonWebToken.create(headerByteArray, bodyByteArray, signatureBlock)

fun parseJwt(jwtData: String, signatureBlock: (signatureString: String) -> ByteArray) = JsonWebToken.parse(jwtData, signatureBlock)

/**
 * json web token
 */
object JsonWebToken {

    const val JWT = "JWT"

    fun create(headerByteArray: ByteArray, bodyByteArray: ByteArray, signatureBlock: (signatureString: String) -> ByteArray): String {
        val headerBase64UrlEncode = headerByteArray.encodeToBase64Url()
        val bodyBase64UrlEncode = bodyByteArray.encodeToBase64Url()
        val needToSignatureString = headerBase64UrlEncode + Constants.Symbol.DOT_CHAR + bodyBase64UrlEncode
        val signatureByte = signatureBlock(needToSignatureString)
        return needToSignatureString + Constants.Symbol.DOT_CHAR + if (signatureByte.isEmpty()) {
            Constants.String.BLANK
        } else {
            signatureByte.encodeToBase64Url()
        }
    }

    fun parse(data: String, signatureBlock: (signatureString: String) -> ByteArray): Pair<ByteArray, ByteArray> {
        val stringList = data.split(Constants.Symbol.DOT_CHAR)
        return if (stringList.size == 3) {
            val headerBase64UrlEncode = stringList[0]
            val bodyBase64UrlEncode = stringList[1]
            val signatureString = stringList[2]
            val needToSignatureString = headerBase64UrlEncode + Constants.Symbol.DOT_CHAR + bodyBase64UrlEncode
            val signatureByte = signatureBlock(needToSignatureString)
            if (signatureString == signatureByte.encodeToBase64Url()) {
                headerBase64UrlEncode.decodeFromBase64Url() to bodyBase64UrlEncode.decodeFromBase64Url()
            } else {
                error("Json web token, decode data error, data:%s".format(data))
            }
        } else {
            error("Json web token, decode data error, data:%s".format(data))
        }
    }
}

fun main() {
    val header = "{\"companyId\":\"0001\"}"
    val body = "{\"userId\":\"0000\",\"username\":\"oneliang\"}"
    val secret = "123456"
    val jwtData = createJwt(header.toByteArray(), body.toByteArray()) {
        it.HmacSHA256(secret.toByteArray())
    }
    println(jwtData)
    val (headerByteArray, bodyByteArray) = parseJwt(jwtData) {
        it.HmacSHA256(secret.toByteArray())
    }
    println("header:$header")
    println("body:$body")
    println("parse header:" + String(headerByteArray))
    println("parse body:" + String(bodyByteArray))
}