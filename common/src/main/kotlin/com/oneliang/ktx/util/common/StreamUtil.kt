package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object StreamUtil {
    /**
     * read input stream content ignore line
     *
     * @param inputStream
     * @param encoding
     * @param readContentProcessor
     */
    fun readInputStreamContentIgnoreLine(inputStream: InputStream, encoding: String = Constants.Encoding.UTF8, readContentProcessor: (line: String) -> Boolean) {
        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(InputStreamReader(inputStream, encoding))
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                val continueRead = readContentProcessor(line)
                if (!continueRead) {
                    break
                }
                line = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            throw StreamUtilException(e)
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: Exception) {
                    throw StreamUtilException(e)
                }
            }
        }
    }

    /**
     * read input stream content ignore line
     *
     * @param inputStream
     * @param encoding
     * @param append
     * @return String
     */
    fun readInputStreamContentIgnoreLine(inputStream: InputStream, encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK): String {
        val stringBuilder = StringBuilder()
        readInputStreamContentIgnoreLine(inputStream, encoding) { line ->
            stringBuilder.append(line)
            stringBuilder.append(append)
            true
        }
        return stringBuilder.toString()
    }

    class StreamUtilException(cause: Throwable) : RuntimeException(cause)
}