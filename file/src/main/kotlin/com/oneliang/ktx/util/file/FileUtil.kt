package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.MD5String
import com.oneliang.ktx.util.common.StreamUtil
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

object FileUtil {

    private val DEFAULT_FILE_COPY_PROCESSOR = DefaultCopyFileProcessor()

    /**
     * file exists,include directory or file
     * @param path
     * directory or file
     * @return boolean
     */
    fun exists(path: String): Boolean {
        return File(path).exists()
    }

    /**
     * has file in directory
     * @param directory
     * @param fileSuffix
     * @return boolean
     */
    fun hasFile(directory: String, fileSuffix: String = Constants.String.BLANK): Boolean {
        return hasFile(File(directory), fileSuffix)
    }

    /**
     * has file in directory
     * @param directoryFile
     * @param fileSuffix
     * @return boolean
     */
    fun hasFile(directoryFile: File, fileSuffix: String = Constants.String.BLANK): Boolean {
        val queue = ConcurrentLinkedQueue<File>()
        queue.add(directoryFile)
        var result = false
        while (!queue.isEmpty()) {
            val file = queue.poll()
            if (file.isDirectory) {
                val fileArray = file.listFiles()
                if (fileArray != null) {
                    queue.addAll(fileArray)
                }
            } else if (file.isFile) {
                if (file.name.toLowerCase().endsWith(fileSuffix.toLowerCase())) {
                    result = true
                    break
                }
            }
        }
        return result
    }

    /**
     * create directory
     * @param directoryFullFilename
     */
    fun createDirectory(directoryFullFilename: String) {
        if (directoryFullFilename.isBlank()) return
        val directoryFile = File(directoryFullFilename)
        createDirectory(directoryFile)
    }

    /**
     * create directory
     * @param directory
     */
    fun createDirectory(directory: File) {
        if (!directory.exists()) {
            directory.setReadable(true, false)
            directory.setWritable(true, true)
            directory.mkdirs()
        }
    }

    /**
     * create file and parent directory, full filename, single empty file.
     * @param fullFilename
     * @return boolean
     */
    fun createFileIncludeDirectory(fullFilename: String): Boolean {
        if (fullFilename.isBlank()) return false
        val file = File(fullFilename)
        return createFileIncludeDirectory(file)
    }

    /**
     * create file and parent directory, full filename, single empty file.
     * @param file
     * @return boolean
     */
    fun createFileIncludeDirectory(file: File): Boolean {
        createDirectory(file.parent)
        try {
            file.setReadable(true, false)
            file.setWritable(true, true)
            return file.createNewFile()
        } catch (e: Exception) {
            throw FileUtilException(e)
        }
    }

    /**
     * delete all file
     * @param directory
     */
    fun deleteAllFile(directory: String) {
        if (directory.isBlank()) {
            return
        }
        val directoryFile = File(directory)
        deleteAllFile(directoryFile)
    }

    /**
     * delete all file
     * @param directoryFile
     */
    fun deleteAllFile(directoryFile: File) {
        val fileList = mutableListOf<File>()
        if (!directoryFile.exists() || !directoryFile.isDirectory) {
            return
        }
        val queue = ConcurrentLinkedQueue<File>()
        queue.add(directoryFile)
        while (!queue.isEmpty()) {
            val file = queue.poll()
            if (file.isDirectory) {
                val fileArray = file.listFiles()
                if (fileArray != null) {
                    queue.addAll(fileArray)
                }
            }
            fileList.add(file)
        }
        for (i in fileList.indices.reversed()) {
            fileList[i].delete()
        }
    }

    /**
     * copy file
     * @param from
     * @param to
     * @param copyType
     * @param copyFileProcessor
     */
    fun copyFile(from: String, to: String, copyType: CopyType = CopyType.PATH_TO_PATH, copyFileProcessor: CopyFileProcessor = DEFAULT_FILE_COPY_PROCESSOR) {
        when (copyType) {
            CopyType.FILE_TO_PATH -> copyFileToPath(from, to, copyFileProcessor)
            CopyType.FILE_TO_FILE -> copyFileToFile(from, to, copyFileProcessor)
            CopyType.PATH_TO_PATH -> copyPathToPath(from, to, copyFileProcessor)
        }
    }

    /**
     * copy path to path,copy process include directory copy
     * @param fromPath
     * @param toPath
     * @param copyFileProcessor
     */
    fun copyPathToPath(fromPath: String, toPath: String, copyFileProcessor: CopyFileProcessor) {
        val fromDirectoryFile = File(fromPath)
        val toDirectoryFile = File(toPath)
        val fromDirectoryPath = fromDirectoryFile.absolutePath
        var toDirectoryPath = toDirectoryFile.absolutePath
        if (fromDirectoryPath == toDirectoryPath) {
            toDirectoryPath += "_copy"
        }
        val queue = ConcurrentLinkedQueue<File>()
        queue.add(fromDirectoryFile)
        while (!queue.isEmpty()) {
            val fromFile = queue.poll()
            val fromFilePath = fromFile.absolutePath
            val toFile = File(toDirectoryPath + fromFilePath.substring(fromDirectoryPath.length))
            if (fromFile.isDirectory) {
                val result = copyFileProcessor.copyFileToFileProcess(fromFile, toFile)
                if (result) {
                    val fileArray = fromFile.listFiles()
                    if (fileArray != null) {
                        queue.addAll(fileArray)
                    }
                }
            } else if (fromFile.isFile) {
                copyFileProcessor.copyFileToFileProcess(fromFile, toFile)
            }
        }
    }

    /**
     * @param fromFile
     * @param toPath
     * @param copyFileProcessor
     */
    private fun copyFileToPath(fromFile: String, toPath: String, copyFileProcessor: CopyFileProcessor) {
        val from = File(fromFile)
        val to = File(toPath)
        if (from.exists() && from.isFile) {
            createDirectory(toPath)
            val tempFromFile = from.absolutePath
            val tempToFile = to.absolutePath + File.separator + from.getName()
            copyFileToFile(tempFromFile, tempToFile, copyFileProcessor)
        }
    }

    /**
     * read file
     *
     * @param fullFilename
     * @return byte[]
     */
    fun readFile(fullFilename: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(fullFilename)
            inputStream.copyTo(byteArrayOutputStream)
        } catch (e: FileNotFoundException) {
            throw FileUtilException(e)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    throw FileUtilException(e)
                }
            }
            try {
                byteArrayOutputStream.close()
            } catch (e: IOException) {
                throw FileUtilException(e)
            }
        }
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * write file
     * @param outputFullFilename
     * @param byteArray
     */
    fun writeFile(outputFullFilename: String, byteArray: ByteArray, append: Boolean = false) {
        this.writeFile(File(outputFullFilename), byteArray, append)
    }

    /**
     * write file
     * @param outputFile
     * @param byteArray
     */
    fun writeFile(outputFile: File, byteArray: ByteArray, append: Boolean = false) {
        createFileIncludeDirectory(outputFile)
        val inputStream = ByteArrayInputStream(byteArray)
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(outputFile, append)
            inputStream.copyTo(outputStream)
        } catch (e: FileNotFoundException) {
            throw FileUtilException(e)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                throw FileUtilException(e)
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    throw FileUtilException(e)
                }
            }
        }
    }

    /**
     * read file content ignore line
     * @param fullFilename
     * @param encoding
     * @param append
     * @return String
     */
    fun readFileContentIgnoreLine(fullFilename: String, encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK): String {
        return readFileContentIgnoreLine(File(fullFilename), encoding, append)
    }

    /**
     * read file content ignore line
     * @param file
     * @param encoding
     * @param append
     * @return String
     */
    fun readFileContentIgnoreLine(file: File, encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK): String {
        val stringBuilder = StringBuilder()
        readFileContentEachLine(file, encoding) { line ->
            stringBuilder.append(line)
            stringBuilder.append(append)
            true
        }
        return stringBuilder.toString()
    }

    /**
     * read file content each line
     * @param fullFilename
     * @param encoding
     * @param readLineProcessor
     */
    fun readFileContentEachLine(fullFilename: String, encoding: String = Constants.Encoding.UTF8, readLineProcessor: (line: String) -> Boolean) {
        readFileContentEachLine(File(fullFilename), encoding, readLineProcessor)
    }

    /**
     * read file content each line
     * @param file
     * @param encoding
     * @param readLineProcessor
     */
    fun readFileContentEachLine(file: File, encoding: String = Constants.Encoding.UTF8, readLineProcessor: (line: String) -> Boolean) {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(file)
            StreamUtil.readInputStreamContentEachLine(inputStream, encoding, readLineProcessor)
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: Exception) {
                    throw FileUtilException(e)
                }
            }
        }
    }

    /**
     * write file content,best for string content
     * @param outputFullFilename
     * @param writeFileContentProcessor
     */
    fun writeFileContent(outputFullFilename: String, charsetName: String = Constants.Encoding.UTF8, append: Boolean = false, writeFileContentProcessor: ((bufferedWriter: BufferedWriter) -> Unit)? = null) {
        this.writeFileContent(File(outputFullFilename), charsetName, append, writeFileContentProcessor)
    }

    /**
     * write file content,best for string content
     * @param outputFile
     * @param writeFileContentProcessor
     */
    fun writeFileContent(outputFile: File, charsetName: String = Constants.Encoding.UTF8, append: Boolean = false, writeFileContentProcessor: ((bufferedWriter: BufferedWriter) -> Unit)? = null) {
        createFileIncludeDirectory(outputFile)
        var bufferedWriter: BufferedWriter? = null
        try {
            bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(outputFile, append), charsetName))
            writeFileContentProcessor?.invoke(bufferedWriter)
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close()
                } catch (e: Exception) {
                    throw FileUtilException(e)
                }
            }
        }
    }

    /**
     * merge file
     *
     * @param outputFullFilename
     * @param fullFilenameList
     */
    fun mergeFile(outputFullFilename: String, fullFilenameList: List<String>) {
        if (outputFullFilename.isBlank() || fullFilenameList.isEmpty()) {
            return
        }
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(outputFullFilename)
            for (fullFilename in fullFilenameList) {
                var inputStream: InputStream? = null
                try {
                    inputStream = FileInputStream(fullFilename)
                    inputStream.copyTo(outputStream)
                } catch (e: Exception) {
                    throw FileUtilException(e)
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            throw FileUtilException(e)
                        }

                    }
                }
            }
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    throw FileUtilException(e)
                }
            }
        }
    }

    /**
     * find match file directory
     * @param directoryFile
     * @param matchOption
     * @param onMatch
     * @return List<String>
     */
    fun findMatchFileDirectory(directoryFile: File, matchOption: MatchOption = MatchOption(), onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        matchOption.findType = MatchOption.FindType.FILE_DIRECTORY
        return findMatchFileOrMatchFileDirectory(directoryFile, matchOption, onMatch)
    }

    /**
     * find match file directory
     * @param directory
     * @param matchOption
     * @param onMatch
     * @return List<String>
     */
    fun findMatchFileDirectory(directory: String, matchOption: MatchOption, onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        return findMatchFileDirectory(File(directory), matchOption, onMatch)
    }

    /**
     * find match file
     * @param directoryFile
     * @param matchOption
     * @param onMatch
     * @return List<String>
     */
    fun findMatchFile(directoryFile: File, matchOption: MatchOption = MatchOption(), onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        matchOption.findType = MatchOption.FindType.FILE
        return findMatchFileOrMatchFileDirectory(directoryFile, matchOption, onMatch)
    }

    /**
     * find match file
     * @param directory
     * @param matchOption
     * @param onMatch
     * @return List<String>
     */
    fun findMatchFile(directory: String, matchOption: MatchOption = MatchOption(), onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        return findMatchFile(File(directory), matchOption, onMatch)
    }

    /**
     * find match directory
     * @param directoryFile
     * @param matchOption
     * @return List<String>
     */
    fun findMatchDirectory(directoryFile: File, matchOption: MatchOption = MatchOption()): List<String> {
        matchOption.findType = MatchOption.FindType.DIRECTORY
        return findMatchFileOrMatchFileDirectory(directoryFile, matchOption)
    }

    /**
     * find match directory
     * @param directory
     * @param matchOption
     * @return List<String>
     */
    fun findMatchDirectory(directory: String, matchOption: MatchOption = MatchOption()): List<String> {
        return findMatchDirectory(File(directory), matchOption)
    }

    /**
     * find match file or match file directory
     * @param directory
     * @param matchOption
     * @param onMatch
     * @return List<String>
     */
    private fun findMatchFileOrMatchFileDirectory(directory: String, matchOption: MatchOption = MatchOption(), onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        val directoryFile = File(directory)
        return findMatchFileOrMatchFileDirectory(directoryFile, matchOption, onMatch)
    }

    /**
     * find match file or match file directory
     * @param directoryFile
     * @param matchOption
     * @param onMatch
     * @return List<String>
     */
    private fun findMatchFileOrMatchFileDirectory(directoryFile: File, matchOption: MatchOption = MatchOption(), onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        val list = mutableListOf<String>()
        val queue = ConcurrentLinkedQueue<File>()
        queue.add(directoryFile)
        while (!queue.isEmpty()) {
            val file = queue.poll()
            if (!file.exists()) {
                continue
            }
            var result = false
            if (!file.isHidden || matchOption.includeHidden) {
                result = true
            }
            if (!result) {
                continue
            }
            if (file.isDirectory) {
                val fileArray = file.listFiles() ?: continue
                for (singleFile in fileArray) {
                    if (matchOption.findType === MatchOption.FindType.DIRECTORY) {
                        if (singleFile.isDirectory && matchOption.deepMatch) {
                            queue.add(singleFile)
                            if (!singleFile.name.toLowerCase().endsWith(matchOption.fileSuffix.toLowerCase())) {
                                continue
                            }
                            list.add(singleFile.absolutePath)
                        }
                    } else {
                        if (singleFile.isDirectory && matchOption.deepMatch) {
                            queue.add(singleFile)
                        } else if (singleFile.isFile) {
                            queue.add(singleFile)
                        }
                    }
                }
            } else if (file.isFile) {
                if (!file.name.toLowerCase().endsWith(matchOption.fileSuffix.toLowerCase())) {
                    continue
                }
                if (matchOption.findType === MatchOption.FindType.FILE) {
                    val fullFilename = onMatch(file)
                    // ignore when null
                    list.add(fullFilename)
                } else if (matchOption.findType === MatchOption.FindType.FILE_DIRECTORY) {
                    val parentFullFilename = onMatch(file.parentFile)
                    // ignore when null
                    if (!list.contains(parentFullFilename)) {
                        list.add(parentFullFilename)
                    }
                }
            }
        }
        return list
    }

    /**
     * differ directory
     *
     * @param differentOutputDirectory
     * @param oldDirectory
     * @param newDirectory
     */
    fun differDirectory(differentOutputDirectory: String, oldDirectory: String, newDirectory: String) {
        val oldFileList: List<String> = findMatchFile(oldDirectory, MatchOption())
        val oldDirectoryAbsolutePath = File(oldDirectory).absolutePath
        val oldFileMD5Map: MutableMap<String, String> = HashMap()
        val differentOutputDirectoryFullFilename = File(differentOutputDirectory).absolutePath
        for (oldFile in oldFileList) {
            var key = File(oldFile).absolutePath.substring(oldDirectoryAbsolutePath.length + 1)
            key = key.replace(Constants.Symbol.SLASH_RIGHT, Constants.Symbol.SLASH_LEFT)
            val value: String = oldFile.MD5String()
            oldFileMD5Map[key] = value
        }
        val newFileList: List<String> = findMatchFile(newDirectory, MatchOption())
        val newDirectoryAbsolutePath = File(newDirectory).absolutePath
        for (newFile in newFileList) {
            var key = File(newFile).absolutePath.substring(newDirectoryAbsolutePath.length + 1)
            key = key.replace(Constants.Symbol.SLASH_RIGHT, Constants.Symbol.SLASH_LEFT)
            val value: String = newFile.MD5String()
            val oldValue = oldFileMD5Map[key]
            if (oldValue == null || oldValue != value) {
                val toFile = differentOutputDirectoryFullFilename + Constants.Symbol.SLASH_LEFT + key
                // System.out.println("key:"+key+",oldValue:"+oldValue+",value:"+value);
                copyFile(newFile, toFile, CopyType.FILE_TO_FILE)
            }
        }
    }

    /**
     * @param fromFullFilename
     * @param toFullFilename
     * @param copyFileProcessor
     */
    private fun copyFileToFile(fromFullFilename: String, toFullFilename: String, copyFileProcessor: CopyFileProcessor) {
        copyFileProcessor.copyFileToFileProcess(File(fromFullFilename), File(toFullFilename))
    }

    /**
     * get properties,if is not exist will auto create
     * @param propertiesFullFilename
     * @return Properties
     */
    fun getPropertiesAutoCreate(propertiesFullFilename: String): Properties {
        if (!exists(propertiesFullFilename)) {
            createFileIncludeDirectory(propertiesFullFilename)
        }
        return getProperties(propertiesFullFilename)
    }

    /**
     * get properties,if is not exist will auto create
     * @param file
     * @return Properties
     */
    fun getPropertiesAutoCreate(file: File): Properties {
        if (!file.exists()) {
            createFileIncludeDirectory(file)
        }
        return getProperties(file)
    }

    /**
     * get properties
     * @param propertiesFullFilename
     * @return Properties
     */
    fun getProperties(propertiesFullFilename: String): Properties {
        val properties = Properties()
        if (propertiesFullFilename.isNotBlank()) {
            val inputStream = FileInputStream(propertiesFullFilename)
            inputStream.use {
                properties.load(it)
            }
        }
        return properties
    }

    /**
     * get properties from properties file,will auto create
     *
     * @param file
     * @return Properties
     * @throws IOException
     */
    fun getProperties(file: File): Properties {
        return getProperties(file.absolutePath)
    }

    /**
     * save properties
     * @param properties
     * @param outputFullFilename
     */
    fun saveProperties(properties: Properties, outputFullFilename: String) {
        val outputStream = FileOutputStream(outputFullFilename)
        outputStream.use {
            properties.store(it, null)
        }
    }

    /**
     * save properties
     * @param properties
     * @param outputFile
     */
    fun saveProperties(properties: Properties, outputFile: File) {
        val outputStream = FileOutputStream(outputFile)
        outputStream.use {
            properties.store(it, null)
        }
    }

    /**
     * find file list with cache
     * @param directoryList
     * @param cacheMap, it will update the cache map
     * @param fileSuffix
     * @param includeHidden
     * @return List<String>, only return the changed file
     */
    fun findFileListWithCache(directoryList: List<String>, cacheMap: MutableMap<String, String>, fileSuffix: String, includeHidden: Boolean = false, cacheKeyProcessor: ((cacheKey: String) -> String)? = null): List<String> {
        val sourceList = mutableListOf<String>()
        // with cache
        for (directory in directoryList) {
            val matchOption = MatchOption()
            matchOption.fileSuffix = fileSuffix
            matchOption.includeHidden = includeHidden
            findMatchFile(directory, matchOption) {
                val fullFilename = it.absolutePath
                var cacheKey = fullFilename
                if (cacheKeyProcessor != null) {
                    cacheKey = cacheKeyProcessor(cacheKey)
                }
                val sourceFileMd5 = File(fullFilename).MD5String()
                if (cacheMap.containsKey(cacheKey)) {
                    val md5 = cacheMap[cacheKey]
                    if (sourceFileMd5 != md5) {
                        sourceList.add(fullFilename)
                        cacheMap[cacheKey] = sourceFileMd5
                    }
                } else {
                    sourceList.add(fullFilename)
                    cacheMap[cacheKey] = sourceFileMd5
                }
                fullFilename
            }
        }
        return sourceList
    }

    class FileUtilException(cause: Throwable) : RuntimeException(cause)

    enum class CopyType {
        PATH_TO_PATH, FILE_TO_PATH, FILE_TO_FILE
    }

    interface CopyFileProcessor {
        /**
         * copyFileToFileProcess
         * @param fromFile,maybe
         *            directory
         * @param toFile,maybe
         *            directory
         * @return boolean,if true keep going copy,only active in directory so
         *         far
         */
        fun copyFileToFileProcess(fromFile: File, toFile: File): Boolean

    }

    /**
     * match option
     */
    class MatchOption {

        internal enum class FindType {
            FILE, DIRECTORY, FILE_DIRECTORY
        }

        var fileSuffix = Constants.String.BLANK
        internal var findType = FindType.FILE
        var includeHidden = false
        var deepMatch = true
    }
}