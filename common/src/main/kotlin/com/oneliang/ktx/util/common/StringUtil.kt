package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants

object StringUtil {

    /**
     * fill zero
     * @param length
     * @return String
     */
    fun fillZero(length: Int): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until length) {
            stringBuilder.append(Constants.String.ZERO)
        }
        return stringBuilder.toString()
    }
}