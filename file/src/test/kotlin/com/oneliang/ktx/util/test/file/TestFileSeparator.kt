package com.oneliang.ktx.util.test.file

import com.oneliang.ktx.util.common.toFile
import com.oneliang.ktx.util.file.separateTextContent

fun main() {
    val fullFilename = "/C:/Users/Administrator/Desktop/log/2020_09_29_default.log"
    fullFilename.toFile().separateTextContent(100000)
}