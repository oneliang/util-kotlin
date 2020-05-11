package com.oneliang.ktx.util.upload

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.*

/**
 * for any file upload
 * @author Dandelion
 */
class FileUpload {

    companion object {
        private val logger = LoggerManager.getLogger(FileUpload::class)

        //the line from 0 to end,just like line index
        private const val HEADER_SEPARATE = ": "
        private const val FILENAME_START_SIGN = "filename=\""
        private const val FILENAME_END_SIGN = "\""
    }
    /**
     * @return the saveFilePath
     */
    /**
     * @param saveFilePath the saveFilePath to set
     */
    var saveFilePath: String? = null//save file path

    /**
     * upload the single file,just for InputStream,client upload
     * @param inputStream
     * @param filename
     * @return FileUploadSign
     * @throws IOException
     */
    fun upload(inputStream: InputStream, filename: String): FileUploadResult {
        val fileUploadResult = FileUploadResult()
        fileUploadResult.filename = filename
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(saveFilePath + Constants.Symbol.SLASH_LEFT + filename)
            val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
            var length = inputStream.read(buffer, 0, buffer.size)
            while (length != -1) {
                outputStream.write(buffer, 0, length)
                outputStream.flush()
                length = inputStream.read(buffer, 0, buffer.size)
            }
            fileUploadResult.isSuccess = true
            fileUploadResult.filePath = saveFilePath
            logger.debug("Upload end,original save file is:$filename")
        } catch (e: Exception) {
            logger.error("upload error", e)
        } finally {
            try {
                outputStream?.flush()
                outputStream?.close()
            } catch (e: Exception) {
                logger.error("outputStream close error", e)
            }
        }
        return fileUploadResult
    }

    /**
     * upload the file,form upload
     * @param inputStream
     * @param totalLength
     * @return List<FileUploadSign>
     * @throws IOException
    </FileUploadSign> */
    fun upload(inputStream: InputStream, totalLength: Int, saveFilenames: Array<String> = emptyArray()): List<FileUploadResult> {
        val fileUploadResultList = mutableListOf<FileUploadResult>()
        var outputStream: OutputStream? = null
        try {
            var firstLine = true
            var formField = true
            var headByteArray: ByteArray? = null
            val lineByteArray = ByteArrayOutputStream()
            var contentDisposition: String? = null
            var originalFilename: String? = null
            var fileCount = 0
            var count = 0
            var data: Int
            do {
                data = inputStream.read()
                if (data == -1) {
                    break
                }
                if (data.toByte() == Constants.String.CR) {
                    var temp = inputStream.read()
                    if (temp.toByte() == Constants.String.LF) {
                        //end one line..
                        if (firstLine) {
                            firstLine = false
                            headByteArray = lineByteArray.toByteArray()
                            lineByteArray.reset()
                        } else {
                            val line = String(lineByteArray.toByteArray(), Charsets.UTF_8)
                            val header = line.split(HEADER_SEPARATE)
                            if (header.size > 1) {
                                if (header[0] == Constants.Http.HeaderKey.CONTENT_DISPOSITION) {
                                    formField = true
                                    contentDisposition = header[1]
                                } else if (header[0] == Constants.Http.HeaderKey.CONTENT_TYPE) {
                                    formField = false
                                    if (contentDisposition != null && contentDisposition.indexOf(FILENAME_START_SIGN) > 0) {
                                        val startIndex = contentDisposition.indexOf(FILENAME_START_SIGN) + FILENAME_START_SIGN.length
                                        val endIndex = contentDisposition.lastIndexOf(FILENAME_END_SIGN)
                                        originalFilename = contentDisposition.substring(startIndex, endIndex)
                                    }
                                }
                            }
                            lineByteArray.reset()
                        }
                        val nextOne = inputStream.read()
                        val nextTwo = inputStream.read()
                        if (nextOne.toByte() == Constants.String.CR && nextTwo.toByte() == Constants.String.LF) {
                            //end one form field
                            if (!formField) {
                                var saveFilename = originalFilename
                                if (saveFilenames.size > fileCount) {
                                    val tempFilename = saveFilenames[fileCount]
                                    saveFilename = tempFilename + originalFilename!!.substring(originalFilename.lastIndexOf(Constants.Symbol.DOT), originalFilename.length)
                                }
                                outputStream = FileOutputStream(saveFilePath + Constants.Symbol.SLASH_LEFT + saveFilename)
                                var i = 0
                                var mayBeEndSign = false
                                val byteArray = ByteArray(headByteArray!!.size)
                                val tempArray = ByteArray(headByteArray.size)
                                var byteArrayInputStream: ByteArrayInputStream? = null
                                while (count < totalLength) {
                                    if (mayBeEndSign) {
                                        data = byteArrayInputStream!!.read()
                                        if (byteArrayInputStream.available() == 0) {
                                            mayBeEndSign = false
                                        }
                                    } else {
                                        data = inputStream.read()
                                    }
                                    if (data.toByte() == Constants.String.CR) {//may be end
                                        if (mayBeEndSign) {
                                            temp = byteArrayInputStream!!.read()
                                            if (byteArrayInputStream.available() == 0) {
                                                mayBeEndSign = false
                                            }
                                        } else {
                                            temp = inputStream.read()
                                        }
                                        if (temp.toByte() == Constants.String.LF) {
                                            var length: Int
                                            if (mayBeEndSign) {
                                                val available = byteArrayInputStream!!.available()
                                                System.arraycopy(tempArray, tempArray.size - available, byteArray, 0, available)
                                                length = available
                                                length += inputStream.read(byteArray, available, tempArray.size - available)
                                            } else {
                                                length = inputStream.read(byteArray, 0, byteArray.size)
                                            }
                                            if (compare(headByteArray, byteArray)) {//upload file end
                                                outputStream.flush()
                                                outputStream.close()
                                                logger.debug("Upload end,original file is:$originalFilename,save file is:$saveFilename")
                                                //initial all variable
                                                val fileUploadResult = FileUploadResult()
                                                fileUploadResult.isSuccess = true
                                                fileUploadResult.filePath = saveFilePath
                                                fileUploadResult.filename = saveFilename
                                                fileUploadResult.originalFilename = originalFilename
                                                fileUploadResultList.add(fileUploadResult)
                                                originalFilename = null
                                                fileCount++
                                                break
                                            } else {
                                                outputStream.write(Constants.String.CRLF)
                                                count += 2
                                                i += 2
                                                System.arraycopy(byteArray, 0, tempArray, 0, tempArray.size)
                                                byteArrayInputStream = ByteArrayInputStream(tempArray, 0, length)
                                                if (byteArrayInputStream.available() > 0) {
                                                    mayBeEndSign = true
                                                }
                                                continue
                                            }
                                        } else {
                                            outputStream.write(data)
                                            outputStream.write(temp)
                                            count += 2
                                            i += 2
                                        }
                                    } else {
                                        outputStream.write(data)
                                        count++
                                        i++
                                    }
                                    if (i % Constants.Capacity.BYTES_PER_KB == 0) {
                                        outputStream.flush()
                                    }
                                }
                            } else {
                                //form field process.skip yet.
                            }
                        } else {
                            lineByteArray.write(nextOne)
                            lineByteArray.write(nextTwo)
                            count += 2
                            continue
                        }
                    } else {
                        lineByteArray.write(data)
                        lineByteArray.write(temp)
                        count += 2
                    }
                } else {
                    lineByteArray.write(data)
                    count++
                }
            } while (data != -1)
        } catch (e: Exception) {
            logger.error("upload error", e)
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: Exception) {
                    logger.error("outputStream close error", e)
                }
            }
        }
        return fileUploadResultList
    }

    private fun compare(a: ByteArray, b: ByteArray): Boolean {
        var result = true
        for (i in a.indices) {
            if (a[i] != b[i]) {
                result = false
                break
            }
        }
        return result
    }
}
