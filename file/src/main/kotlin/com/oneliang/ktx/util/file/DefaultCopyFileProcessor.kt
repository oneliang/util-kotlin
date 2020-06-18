package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.file.FileUtil.CopyFileProcessor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

open class DefaultCopyFileProcessor : CopyFileProcessor {
    /**
     * copyFileToFileProcess
     * @param fromFile,maybe directory
     * @param toFile,maybe directory
     * @return boolean,if true keep going copy,only active in directory so far
     */
    override fun copyFileToFileProcess(fromFile: File, toFile: File): Boolean {
        try {
            if (fromFile.isFile) {
                val fromFullFilename = fromFile.absolutePath
                var toFullFilename = toFile.absolutePath
                if (fromFullFilename == toFullFilename) {
                    toFullFilename += "_copy"
                }
                FileUtil.createFile(toFullFilename)
                val inputStream = FileInputStream(fromFullFilename)
                val outputStream = FileOutputStream(toFullFilename)
                try {
                    inputStream.copyTo(outputStream)
                } finally {
                    inputStream.close()
                    outputStream.close()
                }
            } else {
                FileUtil.createDirectory(toFile)
            }
        } catch (e: Exception) {
            throw FileCopyException(e)
        }
        return true
    }
}