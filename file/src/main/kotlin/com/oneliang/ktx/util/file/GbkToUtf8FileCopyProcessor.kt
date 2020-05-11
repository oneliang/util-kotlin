package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.file.FileUtil.FileCopyProcessor
import java.io.*

class GbkToUtf8FileCopyProcessor : FileCopyProcessor {
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
                FileUtil.createFile(to)
                val inputStream = FileInputStream(from)
                val outputStream = FileOutputStream(to)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream, Constants.Encoding.GBK))
                val bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream, Constants.Encoding.UTF8))
                var string: String? = bufferedReader.readLine()
                while (string != null) {
                    bufferedWriter.write(string + Constants.String.CRLF_STRING)
                    bufferedWriter.flush()
                    string = bufferedReader.readLine()
                }
                bufferedReader.close()
                bufferedWriter.flush()
                bufferedWriter.close()
            } else {
                FileUtil.createDirectory(to)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}