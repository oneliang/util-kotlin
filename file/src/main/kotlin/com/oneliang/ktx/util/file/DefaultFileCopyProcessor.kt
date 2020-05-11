package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.file.FileUtil.FileCopyProcessor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

open class DefaultFileCopyProcessor : FileCopyProcessor {
    /**
     * copyFileToFileProcess
     * @param from,maybe directory
     * @param to,maybe directory
     * @param isFile,maybe directory or file
     * @return boolean,if true keep going copy,only active in directory so far
     */
    override fun copyFileToFileProcess(from: String, to: String, isFile: Boolean): Boolean {
        try {
            if (isFile) {
                val fromFile = File(from).absolutePath
                var toFile = File(to).absolutePath
                if (fromFile == toFile) {
                    toFile += "_copy"
                }
                FileUtil.createFile(toFile)
                val inputStream = FileInputStream(fromFile)
                val outputStream = FileOutputStream(toFile)
                try {
                    val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
                    var length = inputStream.read(buffer, 0, buffer.size)
                    while (length != -1) {
                        outputStream.write(buffer, 0, length)
                        outputStream.flush()
                        length = inputStream.read(buffer, 0, buffer.size)
                    }
                } finally {
                    inputStream.close()
                    outputStream.close()
                }
            } else {
                FileUtil.createDirectory(to)
            }
        } catch (e: Exception) {
            throw FileCopyException(e)
        }
        return true
    }
}