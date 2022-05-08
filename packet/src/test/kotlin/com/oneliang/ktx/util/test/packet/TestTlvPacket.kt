package com.oneliang.ktx.util.test.packet

import com.oneliang.ktx.util.common.toByteArray
import com.oneliang.ktx.util.common.toHexString
import com.oneliang.ktx.util.packet.TlvPacket

fun main() {
    val tlvPacket = TlvPacket(arrayOf(
        TlvPacket(1.toByteArray(), "1".toByteArray()),
        TlvPacket(1.toByteArray(), "2".toByteArray()),
        TlvPacket(1.toByteArray(), "3".toByteArray())
    ))
    println(tlvPacket.toByteArray().toHexString())
}