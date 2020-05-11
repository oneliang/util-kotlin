package com.oneliang.ktx.util.common

object Encoder {

    /**
     * escape
     * @param string
     * @param excludeCharArray
     * @return String
     */
    fun escape(string: String, excludeCharArray: CharArray = charArrayOf()): String {
        val stringBuilder = StringBuilder()
        stringBuilder.ensureCapacity(string.length * 6)
        for (i in 0 until string.length) {
            val character = string[i]
            var excludeSign = false
            if (excludeCharArray.isNotEmpty()) {
                for (excludeChar in excludeCharArray) {
                    if (character == excludeChar) {
                        stringBuilder.append(character)
                        excludeSign = true
                        break
                    }
                }
            }
            if (!excludeSign) {
                if (character.isDigit() || character.isLowerCaseLetter() || character.isUpperCaseLetter()) {
                    stringBuilder.append(character)
                } else if (character.toInt() < 0x100) {
                    stringBuilder.append("%")
                    if (character.toInt() < 0x10) {
                        stringBuilder.append("0")
                    }
                    stringBuilder.append(character.toInt().toString(radix = 16).toUpperCase())
                } else {
                    stringBuilder.append("%u")
                    stringBuilder.append(character.toInt().toString(radix = 16).toUpperCase())
                }
            }
        }
        return stringBuilder.toString()
    }

    /**
     * unescape string
     * @param string
     * @return String
     */
    fun unescape(string: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.ensureCapacity(string.length)
        var lastPos = 0
        var pos = 0
        while (lastPos < string.length) {
            pos = string.indexOf("%", lastPos)
            if (pos == lastPos) {
                if (string[pos + 1] == 'u') {
                    val character = string.substring(pos + 2, pos + 6).toInt(radix = 16).toChar()
                    stringBuilder.append(character)
                    lastPos = pos + 6
                } else {
                    val character = string.substring(pos + 1, pos + 3).toInt(radix = 16).toChar()
                    stringBuilder.append(character)
                    lastPos = pos + 3
                }
            } else {
                if (pos == -1) {
                    stringBuilder.append(string.substring(lastPos))
                    lastPos = string.length
                } else {
                    stringBuilder.append(string.substring(lastPos, pos))
                    lastPos = pos
                }
            }
        }
        return stringBuilder.toString()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var tmp = "~!@#$%^&*()_+|\\=-,erg./?><;'][{}\""

        println(tmp)
        tmp = escape(tmp, charArrayOf('.'))
        println(tmp)
        println(tmp)
        println(unescape(tmp))
        println(unescape(escape("您好吗？....", charArrayOf('.'))))
    }
}