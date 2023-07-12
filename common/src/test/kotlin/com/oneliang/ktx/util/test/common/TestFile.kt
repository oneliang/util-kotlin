package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.*

fun main() {
    val fullFilename = "/Users/oneliang/Java/githubWorkspace/util-kotlin/common/src/test/kotlin/notice.txt"
    val file = fullFilename.toFile()
    println(file.readBytes().toHexString())
    file.read(0, 1) { buffer, length ->
        println(buffer.sliceArray(0 until length).toHexString())
    }
    file.replace(3,4, "你好吗？".toByteArray())
    println(file.readBytes().toHexString())
}