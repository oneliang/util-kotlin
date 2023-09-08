package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.file.FileUtil.CopyFileProcessor
import java.io.*

class GbkToUtf8CopyFileProcessor : CopyFileProcessor {
    /**
     * copyFileToFileProcess
     * @param from,maybe directory
     * @param to,maybe directory
     * @return boolean,if true keep going copy,only active in directory so far
     */
    override fun copyFileToFileProcess(fromFile: File, toFile: File): Boolean {
        try {
            if (fromFile.isFile) {
                FileUtil.createFileIncludeDirectory(toFile)
                val inputStream = FileInputStream(fromFile)
                val outputStream = FileOutputStream(toFile)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream, Constants.Encoding.GBK))
                val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream, Constants.Encoding.UTF8))
                var string: String? = bufferedReader.readLine()
                while (string != null) {
                    bufferedWriter.write(string + Constants.String.NEW_LINE)
                    bufferedWriter.flush()
                    string = bufferedReader.readLine()
                }
                bufferedReader.close()
                bufferedWriter.flush()
                bufferedWriter.close()
            } else {
                FileUtil.createDirectory(toFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}