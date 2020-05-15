package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import java.io.*
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.collections.ArrayList
import kotlin.collections.set

object FileUtil {

    private val DEFAULT_FILE_COPY_PROCESSOR = DefaultFileCopyProcessor()

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
     *
     * @param directory
     * @param fileSuffix
     * @return boolean
     */
    fun hasFile(directory: String, fileSuffix: String = Constants.String.BLANK): Boolean {
        val directoryFile = File(directory)
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
     * create file,full filename,signle empty file.
     * @param fullFilename
     * @return boolean
     */
    fun createFile(fullFilename: String): Boolean {
        if (fullFilename.isBlank()) return false
        val file = File(fullFilename)
        return createFile(file)
    }

    /**
     * create file,full filename,signle empty file.
     * @param file
     * @return boolean
     */
    fun createFile(file: File): Boolean {
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
     * @param fileCopyType
     * @param fileCopyProcessor
     */
    fun copyFile(from: String, to: String, fileCopyType: FileCopyType = FileCopyType.PATH_TO_PATH, fileCopyProcessor: FileCopyProcessor = DEFAULT_FILE_COPY_PROCESSOR) {
        when (fileCopyType) {
            FileCopyType.FILE_TO_PATH -> copyFileToPath(from, to, fileCopyProcessor)
            FileCopyType.FILE_TO_FILE -> copyFileToFile(from, to, fileCopyProcessor)
            FileCopyType.PATH_TO_PATH -> copyPathToPath(from, to, fileCopyProcessor)
        }
    }

    /**
     * copy path to path,copy process include directory copy
     * @param fromPath
     * @param toPath
     * @param fileCopyProcessor
     */
    fun copyPathToPath(fromPath: String, toPath: String, fileCopyProcessor: FileCopyProcessor) {
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
            val file = queue.poll()
            val fromFilePath = file.absolutePath
            val toFilePath = toDirectoryPath + fromFilePath.substring(fromDirectoryPath.length)
            if (file.isDirectory) {
                val result = fileCopyProcessor.copyFileToFileProcess(fromFilePath, toFilePath, false)
                if (result) {
                    val fileArray = file.listFiles()
                    if (fileArray != null) {
                        queue.addAll(fileArray)
                    }
                }
            } else if (file.isFile) {
                fileCopyProcessor.copyFileToFileProcess(fromFilePath, toFilePath, true)
            }
        }
    }

    /**
     * @param fromFile
     * @param toPath
     * @param fileCopyProcessor
     */
    private fun copyFileToPath(fromFile: String, toPath: String, fileCopyProcessor: FileCopyProcessor) {
        val from = File(fromFile)
        val to = File(toPath)
        if (from.exists() && from.isFile) {
            createDirectory(toPath)
            val tempFromFile = from.absolutePath
            val tempToFile = to.absolutePath + File.separator + from.getName()
            copyFileToFile(tempFromFile, tempToFile, fileCopyProcessor)
        }
    }

    /**
     * unzip
     * @param zipFullFilename
     * @param outputDirectory
     * @param zipEntryNameList,if
     * it is null or empty,will unzip all
     * @return List<String>
    </String> */
    fun unzip(zipFullFilename: String, outputDirectory: String, zipEntryNameList: List<String>): List<String> {
        createDirectory(outputDirectory)
        val storeFileList = mutableListOf<String>()
        var zipFile: ZipFile? = null
        try {
            zipFile = ZipFile(zipFullFilename)
            val outputDirectoryAbsolutePath = File(outputDirectory).absolutePath
            val enumeration = zipFile.entries()
            while (enumeration.hasMoreElements()) {
                val zipEntry = enumeration.nextElement()
                val zipEntryName = zipEntry.name
                var contains = false
                if (zipEntryNameList.isEmpty()) {
                    contains = true
                } else {
                    if (zipEntryNameList.contains(zipEntryName)) {
                        contains = true
                    }
                }
                if (contains) {
                    val inputStream = zipFile.getInputStream(zipEntry)
                    val outputFullFilename = outputDirectoryAbsolutePath + Constants.Symbol.SLASH_LEFT + zipEntryName
                    if (zipEntry.isDirectory) {
                        createDirectory(outputFullFilename)
                    } else {
                        createFile(outputFullFilename)
                        val outputStream = FileOutputStream(outputFullFilename)
                        try {
                            val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
                            var length = inputStream.read(buffer, 0, buffer.size)
                            while (length != -1) {
                                outputStream.write(buffer, 0, length)
                                outputStream.flush()
                                length = inputStream.read(buffer, 0, buffer.size)
                            }
                        } finally {
                            inputStream?.close()
                            outputStream.close()
                        }
                        storeFileList.add(outputFullFilename)
                    }
                }
            }
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            try {
                zipFile?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return storeFileList
    }

    /**
     * zip
     *
     * @param outputZipFullFilename
     * @param directory
     * @param fileSuffix
     * @param zipProcessor
     */
    fun zip(outputZipFullFilename: String, directory: String, fileSuffix: String, zipProcessor: ZipProcessor) {
        val matchOption = MatchOption()
        matchOption.fileSuffix = fileSuffix
        val fileList = findMatchFile(directory, matchOption)
        if (fileList.isNotEmpty()) {
            val zipEntryPathList = ArrayList<ZipEntryPath>()
            val outputFullFilenameLength = File(directory).absolutePath.length + 1
            for (file in fileList) {
                var zipEntryName = file.substring(outputFullFilenameLength, file.length)
                zipEntryName = zipEntryName.replace(Constants.Symbol.SLASH_RIGHT, Constants.Symbol.SLASH_LEFT)
                zipEntryPathList.add(ZipEntryPath(file, ZipEntry(zipEntryName), true))
            }
            zip(outputZipFullFilename, Constants.String.BLANK, zipEntryPathList, zipProcessor)
        }
    }

    /**
     * zip
     *
     * @param outputZipFullFilename
     * @param inputZipFullFilename,can
     * blank,the entry will not from the input file
     * @param zipEntryPathList
     * @param zipProcessor
     */
    fun zip(outputZipFullFilename: String, inputZipFullFilename: String = Constants.String.BLANK, zipEntryPathList: List<ZipEntryPath>, zipProcessor: ZipProcessor? = null) {
        var zipOutputStream: ZipOutputStream? = null
        var zipFile: ZipFile? = null
        val zipEntryPathMap = mutableMapOf<String, ZipEntryPath>()
        val needToAddEntryNameList = mutableListOf<String>()
        if (zipEntryPathList.isNotEmpty()) {
            for (zipEntryPath in zipEntryPathList) {
                zipEntryPathMap[zipEntryPath.zipEntry.name] = zipEntryPath
                needToAddEntryNameList.add(zipEntryPath.zipEntry.name)
            }
        }
        try {
            createFile(outputZipFullFilename)
            zipOutputStream = ZipOutputStream(FileOutputStream(outputZipFullFilename))
            if (inputZipFullFilename.isBlank()) {
                zipFile = ZipFile(inputZipFullFilename)
                val enumeration = zipFile.entries()
                while (enumeration.hasMoreElements()) {
                    var zipEntry = enumeration.nextElement()
                    val zipEntryName = zipEntry.name
                    var inputStream: InputStream? = null
                    if (zipEntryPathMap.containsKey(zipEntryName)) {
                        val zipEntryPath = zipEntryPathMap[zipEntryName]!!
                        needToAddEntryNameList.remove(zipEntryName)
                        if (zipEntryPath.replace) {
                            zipEntry = zipEntryPath.zipEntry
                            inputStream = FileInputStream(zipEntryPath.fullFilename)
                        } else {
                            //input stream is null, no need to replace
                        }
                    } else {
                        //input stream is null, no need to replace
                    }
                    val newInputStream: InputStream
                    if (inputStream == null) {
                        inputStream = zipFile.getInputStream(zipEntry)
                        if (zipProcessor != null) {
                            newInputStream = zipProcessor.zipEntryProcess(zipEntryName, inputStream)
                            if (newInputStream !== inputStream) {
                                inputStream.close()
                            }
                        } else {
                            newInputStream = inputStream
                        }
                    } else {
                        newInputStream = inputStream
                    }
                    val newZipEntry = ZipEntry(zipEntryName)
                    addZipEntry(zipOutputStream, newZipEntry, newInputStream)
                }
            }
            for (zipEntryName in needToAddEntryNameList) {
                val zipEntryPath = zipEntryPathMap[zipEntryName]!!
                val zipEntry = zipEntryPath.zipEntry
                val inputStream = FileInputStream(zipEntryPath.fullFilename)
                val newInputStream: InputStream
                if (zipProcessor != null) {
                    newInputStream = zipProcessor.zipEntryProcess(zipEntry.name, inputStream)
                    if (newInputStream !== inputStream) {
                        inputStream.close()
                    }
                } else {
                    newInputStream = inputStream
                }
                addZipEntry(zipOutputStream, zipEntry, newInputStream)
            }
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.finish()
                    zipOutputStream.flush()
                    zipOutputStream.close()
                }
                if (zipFile != null) {
                    zipFile.close()
                }
            } catch (e: Exception) {
                throw FileUtilException(e)
            }

        }
    }

    /**
     * merge zip file
     *
     * @param zipOutputFullFilename
     * @param zipFullFilenameList
     */
    fun mergeZip(zipOutputFullFilename: String, zipFullFilenameList: List<String>?) {
        createFile(zipOutputFullFilename)
        var zipOutputStream: ZipOutputStream? = null
        try {
            zipOutputStream = ZipOutputStream(FileOutputStream(zipOutputFullFilename))
            if (zipFullFilenameList != null) {
                for (zipFullFilename in zipFullFilenameList) {
                    if (exists(zipFullFilename)) {
                        val zipFile = ZipFile(zipFullFilename)
                        val enumeration = zipFile.entries()
                        while (enumeration.hasMoreElements()) {
                            val zipEntry = enumeration.nextElement()
                            val inputStream = zipFile.getInputStream(zipEntry)
                            val newZipEntry = ZipEntry(zipEntry.name)
                            addZipEntry(zipOutputStream, newZipEntry, inputStream)
                        }
                        zipFile.close()
                    }
                }
            }
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.close()
                }
            } catch (e: Exception) {
                throw FileUtilException(e)
            }

        }
    }

    /**
     * add zip entry
     *
     * @param zipOutputStream
     * @param zipEntry
     * @param inputStream
     * @throws Exception
     */
    fun addZipEntry(zipOutputStream: ZipOutputStream, zipEntry: ZipEntry, inputStream: InputStream) {
        try {
            zipOutputStream.putNextEntry(zipEntry)
            val buffer = ByteArray(Constants.Capacity.BYTES_PER_KB)
            var length = inputStream.read(buffer, 0, buffer.size)
            while (length != -1) {
                zipOutputStream.write(buffer, 0, length)
                zipOutputStream.flush()
                length = inputStream.read(buffer, 0, buffer.size)
            }
        } catch (e: ZipException) {
            // do nothing
        } finally {
            inputStream.close()
            zipOutputStream.closeEntry()
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
            copyStream(inputStream, byteArrayOutputStream)
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
     *
     * @param outputFullFilename
     * @param byteArray
     */
    fun writeFile(outputFullFilename: String, byteArray: ByteArray, append: Boolean = false) {
        createFile(outputFullFilename)
        val inputStream = ByteArrayInputStream(byteArray)
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(outputFullFilename, append)
            copyStream(inputStream, outputStream)
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
     *
     * @param fullFilename
     * @param encoding
     * @param append
     * @return String
     */
    fun readFileContentIgnoreLine(fullFilename: String, encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK): String {
        val stringBuilder = StringBuilder()
        readFileContentIgnoreLine(fullFilename, encoding, object : ReadFileContentProcessor {
            override fun afterReadLine(line: String): Boolean {
                stringBuilder.append(line)
                stringBuilder.append(append)
                return true
            }
        })
        return stringBuilder.toString()
    }

    /**
     * read file content ignore line
     *
     * @param fullFilename
     * @param encoding
     * @param readFileContentProcessor
     */
    fun readFileContentIgnoreLine(fullFilename: String, encoding: String = Constants.Encoding.UTF8, readFileContentProcessor: ReadFileContentProcessor) {
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(fullFilename)
            readInputStreamContentIgnoreLine(inputStream, encoding, readFileContentProcessor)
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
     * read input stream content ignore line
     *
     * @param inputStream
     * @param encoding
     * @param append
     * @return String
     */
    fun readInputStreamContentIgnoreLine(inputStream: InputStream, encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK): String {
        val stringBuilder = StringBuilder()
        readInputStreamContentIgnoreLine(inputStream, encoding, object : ReadFileContentProcessor {
            override fun afterReadLine(line: String): Boolean {
                stringBuilder.append(line)
                stringBuilder.append(append)
                return true
            }
        })
        return stringBuilder.toString()
    }

    /**
     * read input stream content ignore line
     *
     * @param inputStream
     * @param encoding
     * @param readFileContentProcessor
     */
    fun readInputStreamContentIgnoreLine(inputStream: InputStream, encoding: String = Constants.Encoding.UTF8, readFileContentProcessor: ReadFileContentProcessor) {
        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(InputStreamReader(inputStream, encoding))
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                val continueRead = readFileContentProcessor.afterReadLine(line)
                if (!continueRead) {
                    break
                }
                line = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: Exception) {
                    throw FileUtilException(e)
                }
            }
        }
    }

    /**
     * write file content,best for string content
     *
     * @param fullFilename
     * @param writeFileContentProcessor
     */
    fun writeFileContent(fullFilename: String, charsetName: String = Constants.Encoding.UTF8, append: Boolean = false, writeFileContentProcessor: WriteFileContentProcessor? = null) {
        createFile(fullFilename)
        var bufferedWriter: BufferedWriter? = null
        try {
            bufferedWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(fullFilename, append), charsetName))
            writeFileContentProcessor?.writeContent(bufferedWriter)
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
     * copy stream , from input to output,it don't close
     *
     * @param inputStream
     * @param outputStream
     */
    fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        try {
            val buffer = ByteArray(Constants.Capacity.BYTES_PER_MB)
            var length = inputStream.read(buffer, 0, buffer.size)
            while (length != -1) {
                outputStream.write(buffer, 0, length)
                outputStream.flush()
                length = inputStream.read(buffer, 0, buffer.size)
            }
        } catch (e: Exception) {
            throw FileUtilException(e)
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
                    copyStream(inputStream, outputStream)
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
    fun findMatchFileDirectory(directoryFile: File, matchOption: MatchOption, onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
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
    fun findMatchFile(directoryFile: File, matchOption: MatchOption, onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
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
    fun findMatchFile(directory: String, matchOption: MatchOption, onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        return findMatchFile(File(directory), matchOption, onMatch)
    }

    /**
     * find match directory
     * @param directoryFile
     * @param matchOption
     * @return List<String>
     */
    fun findMatchDirectory(directoryFile: File, matchOption: MatchOption): List<String> {
        matchOption.findType = MatchOption.FindType.DIRECTORY
        return findMatchFileOrMatchFileDirectory(directoryFile, matchOption)
    }

    /**
     * find match directory
     * @param directory
     * @param matchOption
     * @return List<String>
     */
    fun findMatchDirectory(directory: String, matchOption: MatchOption): List<String> {
        return findMatchDirectory(File(directory), matchOption)
    }

    /**
     * find match file or match file directory
     * @param directory
     * @param matchOption
     * @param onMatch
     * @return List<String>
     */
    private fun findMatchFileOrMatchFileDirectory(directory: String, matchOption: MatchOption, onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
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
    private fun findMatchFileOrMatchFileDirectory(directoryFile: File, matchOption: MatchOption, onMatch: (file: File) -> String = { it.absolutePath }): List<String> {
        val list = ArrayList<String>()
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
     * get zip entry map
     * @param zipFullFilename
     * @return Map<String></String>, String>
     */
    private fun getZipEntryMap(zipFullFilename: String): Map<String, String> {
        var zipFile: ZipFile? = null
        val map = mutableMapOf<String, String>()
        try {
            zipFile = ZipFile(zipFullFilename)
            val entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                val zipEntry = entries.nextElement() as ZipEntry
                if (!zipEntry.isDirectory) {
                    val key = zipEntry.name
                    val value = zipEntry.crc.toString() + Constants.Symbol.DOT + zipEntry.size
                    map[key] = value
                }
            }
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close()
                } catch (e: IOException) {
                    throw FileUtilException(e)
                }
            }
        }
        return map
    }

    class ZipEntryPath(val fullFilename: String, val zipEntry: ZipEntry, val replace: Boolean = false)

    /**
     * @param fromFile
     * @param toFile
     * @param fileCopyProcessor
     */
    private fun copyFileToFile(fromFile: String, toFile: String, fileCopyProcessor: FileCopyProcessor) {
        fileCopyProcessor.copyFileToFileProcess(fromFile, toFile, true)
    }

    /**
     * get properties,if is not exist will auto create
     *
     * @param propertiesFullFilename
     * @return Properties
     */
    fun getPropertiesAutoCreate(propertiesFullFilename: String): Properties {
        if (!exists(propertiesFullFilename)) {
            createFile(propertiesFullFilename)
        }
        return getProperties(propertiesFullFilename)
    }

    /**
     * get properties
     *
     * @param propertiesFullFilename
     * @return Properties
     */
    fun getProperties(propertiesFullFilename: String): Properties {
        val properties = Properties()
        if (propertiesFullFilename.isNotBlank()) {
            var inputStream: InputStream? = null
            try {
                inputStream = FileInputStream(propertiesFullFilename)
                properties.load(inputStream)
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
     *
     * @param properties
     * @param outputFullFilename
     */
    fun saveProperties(properties: Properties, outputFullFilename: String) {
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(outputFullFilename)
            properties.store(outputStream, null)
        } catch (e: Exception) {
            throw FileUtilException(e)
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush()
                    outputStream.close()
                } catch (e: Exception) {
                    throw FileUtilException(e)
                }
            }
        }
    }

    class FileUtilException(cause: Throwable) : RuntimeException(cause)

    enum class FileCopyType {
        PATH_TO_PATH, FILE_TO_PATH, FILE_TO_FILE
    }

    interface FileCopyProcessor {

        /**
         * copyFileToFileProcess
         *
         * @param from,maybe
         *            directory
         * @param to,maybe
         *            directory
         * @param isFile,maybe
         *            directory or file
         * @return boolean,if true keep going copy,only active in directory so
         *         far
         */
        fun copyFileToFileProcess(from: String, to: String, isFile: Boolean): Boolean

    }

    interface ZipProcessor {

        /**
         * zip entry process
         *
         * @param zipEntryName
         * @param inputStream
         * @return InputStream
         */
        fun zipEntryProcess(zipEntryName: String, inputStream: InputStream): InputStream
    }

    interface CacheProcessor {
        /**
         * key process,can change key to save cache
         *
         * @param key
         * @return String
         */
        fun keyProcess(key: String): String
    }

    interface NoCacheFileProcessor {
        /**
         * process
         *
         * @param uncachedFileList
         * @return boolean,true is save cache else false
         */
        fun process(uncachedFileList: List<String>): Boolean
    }

    interface NoCacheFileFinder {

        /**
         * find no cache file list
         *
         * @param cacheFileMapping
         * @return List<String>
         */
        fun findNoCacheFileList(cacheFileMapping: Properties): List<String>
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

    interface DifferZipProcessor {
        /**
         * found added zip entry process
         *
         * @param zipEntryName
         * @return boolean true is need to save in different.zip
         */
        fun foundAddedZipEntryProcess(zipEntryName: String): ZipEntryInformation

        /**
         * found modified zip entry process
         *
         * @param zipEntryName
         * @return boolean true is need to save in different.zip
         */
        fun foundModifiedZipEntryProcess(zipEntryName: String): ZipEntryInformation

        /**
         * found deleted zip entry process
         *
         * @param zipEntryName
         */
        fun foundDeletedZipEntryProcess(zipEntryName: String)

        class ZipEntryInformation(val needToSave: Boolean, val zipEntry: ZipEntry, val inputStream: InputStream)
    }

    interface ReadFileContentProcessor {
        /**
         * after read line
         *
         * @param line
         * @return boolean, if true continue read, false break read
         */
        fun afterReadLine(line: String): Boolean
    }

    interface WriteFileContentProcessor {
        /**
         * write content
         *
         * @param bufferedWriter
         * @throws Exception
         */
        fun writeContent(bufferedWriter: BufferedWriter)
    }
}