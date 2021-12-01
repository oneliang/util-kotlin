package com.oneliang.ktx.util.common

object Base64UrlCodec {

    fun encode(data: ByteArray): String {
        val base64Text: String = Base64Codec.encode(data)
        var byteArray: ByteArray = base64Text.toByteArray(Charsets.US_ASCII)

        //base64url encoding doesn't use padding chars:
        byteArray = removePadding(byteArray)

        //replace URL-unfriendly Base64 chars to url-friendly ones:
        for (i in byteArray.indices) {
            if (byteArray[i] == '+'.toByte()) {
                byteArray[i] = '-'.toByte()
            } else if (byteArray[i] == '/'.toByte()) {
                byteArray[i] = '_'.toByte()
            }
        }
        return String(byteArray, Charsets.US_ASCII)
    }

    private fun removePadding(bytes: ByteArray): ByteArray {
        var result = bytes
        var paddingCount = 0
        for (i in bytes.size - 1 downTo 1) {
            if (bytes[i] == '='.toByte()) {
                paddingCount++
            } else {
                break
            }
        }
        if (paddingCount > 0) {
            result = ByteArray(bytes.size - paddingCount)
            System.arraycopy(bytes, 0, result, 0, bytes.size - paddingCount)
        }
        return result
    }

    fun decode(encoded: String): ByteArray {
        var chars = encoded.toCharArray() //always ASCII - one char == 1 byte

        //Base64 requires padding to be in place before decoding, so add it if necessary:
        chars = ensurePadding(chars)

        //Replace url-friendly chars back to normal Base64 chars:
        for (i in chars.indices) {
            if (chars[i] == '-') {
                chars[i] = '+'
            } else if (chars[i] == '_') {
                chars[i] = '/'
            }
        }
        val base64Text = String(chars)
        return Base64Codec.decode(base64Text)
    }

    private fun ensurePadding(chars: CharArray): CharArray {
        var result = chars //assume argument in case no padding is necessary
        var paddingCount = 0

        //fix for https://github.com/jwtk/jjwt/issues/31
        val remainder = chars.size % 4
        if (remainder == 2 || remainder == 3) {
            paddingCount = 4 - remainder
        }
        if (paddingCount > 0) {
            result = CharArray(chars.size + paddingCount)
            System.arraycopy(chars, 0, result, 0, chars.size)
            for (i in 0 until paddingCount) {
                result[chars.size + i] = '='
            }
        }
        return result
    }
}