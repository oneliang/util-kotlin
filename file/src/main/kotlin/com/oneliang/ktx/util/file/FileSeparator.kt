package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.readContentIgnoreLine
import com.oneliang.ktx.util.file.create
import java.io.File
import java.io.FileOutputStream

object FileSeparator {

    fun separateTextContent(file: File, maxLineCountPerFile: Int = 100000) {
        val filename = file.name
        val directory = file.parentFile.absolutePath
        var lineCount = 0
        var newFileOutputStream: FileOutputStream? = null
        file.inputStream().readContentIgnoreLine {
            //first create new file
            if (lineCount % maxLineCountPerFile == 0) {
                //close old file
                newFileOutputStream?.flush()
                newFileOutputStream?.close()
                val times = lineCount / maxLineCountPerFile
                //new file
                val outputFile = File(directory, filename + Constants.Symbol.UNDERLINE + (times) * maxLineCountPerFile + Constants.Symbol.UNDERLINE + (times + 1) * maxLineCountPerFile + Constants.Symbol.DOT + "log")
                outputFile.create()
                newFileOutputStream = outputFile.outputStream()
                newFileOutputStream?.write((it + Constants.String.CRLF_STRING).toByteArray())
            }
            newFileOutputStream?.write((it + Constants.String.CRLF_STRING).toByteArray())
            lineCount++
            true
        }
        newFileOutputStream?.flush()
        newFileOutputStream?.close()
    }

    fun separateTextContent(fullFilename: String, maxLineCountPerFile: Int = 100000) {
        separateTextContent(File(fullFilename), maxLineCountPerFile)
    }
}