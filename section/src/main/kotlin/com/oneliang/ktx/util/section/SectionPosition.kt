package com.oneliang.ktx.util.section

import com.oneliang.ktx.util.common.toHexString

class SectionPosition constructor(val fromIndex: Int = -1, val toIndex: Int = -1, val byteArray: ByteArray = ByteArray(0)) {

    /**
     * @return the byteArray
     */
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("(from,to")
        stringBuilder.append(",value")
        stringBuilder.append(")(")
        stringBuilder.append("$fromIndex,")
        stringBuilder.append(toIndex)
        stringBuilder.append("," + byteArray.toHexString())
        stringBuilder.append(")")
        return stringBuilder.toString()
    }
}