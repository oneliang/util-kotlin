package com.oneliang.ktx.util.packet

import com.oneliang.ktx.util.common.toByteArray
import java.io.ByteArrayOutputStream

class TlvPacket constructor(var type: ByteArray = ByteArray(0), var body: ByteArray = ByteArray(0)) : Packet {
    companion object

    @Throws(Exception::class)
    override fun toByteArray(): ByteArray {
        if (this.type.isEmpty() && this.body.isEmpty()) {
            return ByteArray(0)
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.write(this.type)
        val bodyLengthByteArray = body.size.toByteArray()
        byteArrayOutputStream.write(bodyLengthByteArray)
        byteArrayOutputStream.write(body)
        return byteArrayOutputStream.toByteArray()
    }
}