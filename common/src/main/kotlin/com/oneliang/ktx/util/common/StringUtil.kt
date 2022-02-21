package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants

fun generateZeroString(length: Int) = StringUtil.generateZeroString(length)

object StringUtil {

    /**
     * generate zero string
     * @param length
     * @return String
     */
    fun generateZeroString(length: Int): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until length) {
            stringBuilder.append(Constants.String.ZERO)
        }
        return stringBuilder.toString()
    }
}