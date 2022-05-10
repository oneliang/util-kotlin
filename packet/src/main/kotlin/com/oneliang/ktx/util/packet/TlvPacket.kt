package com.oneliang.ktx.util.packet

import com.oneliang.ktx.util.common.toByteArray
import java.io.ByteArrayOutputStream

open class TlvPacket constructor(var type: ByteArray = ByteArray(0), var body: ByteArray = ByteArray(0)) : Packet {
    companion object {
        private val TYPE_TLV_PACKAGE: ByteArray by lazy { (-1).toByteArray() }
    }

    constructor(subTlvPackets: Array<TlvPacket>) : this(TYPE_TLV_PACKAGE) {
        subTlvPackets.ifEmpty { error("parameter(subTlvPackets) can not be empty") }
        this.subTlvPackets = subTlvPackets
    }

    private var subTlvPackets = emptyArray<TlvPacket>()

    @Throws(Exception::class)
    override fun toByteArray(): ByteArray {
        if (this.type.isEmpty() && this.body.isEmpty() && this.subTlvPackets.isEmpty()) {
            return ByteArray(0)
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        byteArrayOutputStream.write(this.type)
        val bodyByteArrayOutputStream = ByteArrayOutputStream()
        if (this.body.isNotEmpty() && this.subTlvPackets.isEmpty()) {
            bodyByteArrayOutputStream.write(this.body)
        } else if (this.body.isEmpty() && this.subTlvPackets.isNotEmpty()) {
            this.subTlvPackets.forEach {
                bodyByteArrayOutputStream.write(it.toByteArray())
            }
        } else {
            if (this.type.contentEquals(TYPE_TLV_PACKAGE)) {
                error("parameter(body) and parameter(subTlvPackets) are all empty or not empty, body empty?:%s, subTlvPackets empty?:%s".format(this.body.isEmpty(), this.subTlvPackets.isEmpty()))
            }
        }
        val bodyByteArray = bodyByteArrayOutputStream.toByteArray()
        val bodyLengthByteArray = bodyByteArray.size.toByteArray()
        byteArrayOutputStream.write(bodyLengthByteArray)
        byteArrayOutputStream.write(bodyByteArray)
        return byteArrayOutputStream.toByteArray()
    }
}