package com.oneliang.ktx.util.section

class UnitSection(private val byteArray: ByteArray) : Section {
    override fun toByteArray(): ByteArray {
        return byteArray
    }
}