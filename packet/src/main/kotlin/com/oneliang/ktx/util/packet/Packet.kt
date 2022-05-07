package com.oneliang.ktx.util.packet

interface Packet {

    @Throws(Exception::class)
    fun toByteArray(): ByteArray
}