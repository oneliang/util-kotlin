package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object ZipUtil {
    private val logger = LoggerManager.getLogger(ZipUtil::class)

    /**
     * zip
     * @param directory
     * @param outputZipFullFilename
     * @param fileSuffix
     * @param zipProcessor
     */
    fun zip(directory: String, outputZipFullFilename: String, fileSuffix: String = Constants.String.BLANK, zipProcessor: ((zipEntryName: String, inputStream: InputStream) -> InputStream)? = null) {
        zip(File(directory), outputZipFullFilename, fileSuffix, zipProcessor)
    }

    /**
     * zip
     * @param directoryFile
     * @param outputZipFullFilename
     * @param fileSuffix
     * @param zipProcessor
     */
    fun zip(directoryFile: File, outputZipFullFilename: String, fileSuffix: String = Constants.String.BLANK, zipProcessor: ((zipEntryName: String, inputStream: InputStream) -> InputStream)? = null) {
        if (outputZipFullFilename.isBlank()) {
            error("outputZipFullFilename is blank")
        }
        zip(directoryFile, File(outputZipFullFilename), fileSuffix, zipProcessor)
    }

    /**
     * zip
     * @param directoryFile
     * @param outputZipFile
     * @param fileSuffix
     * @param zipProcessor
     */
    fun zip(directoryFile: File, outputZipFile: File, fileSuffix: String = Constants.String.BLANK, zipProcessor: ((zipEntryName: String, inputStream: InputStream) -> InputStream)? = null) {
        val matchOption = FileUtil.MatchOption()
        matchOption.fileSuffix = fileSuffix
        val fileList = FileUtil.findMatchFile(directoryFile, matchOption)
        if (fileList.isNotEmpty()) {
            val zipEntryPathList = mutableListOf<ZipEntryPath>()
            val outputFullFilenameLength = directoryFile.absolutePath.length + 1
            for (file in fileList) {
                var zipEntryName = file.substring(outputFullFilenameLength, file.length)
                zipEntryName = zipEntryName.replace(Constants.Symbol.SLASH_RIGHT, Constants.Symbol.SLASH_LEFT)
                zipEntryPathList.add(ZipEntryPath(file, ZipEntry(zipEntryName), true))
            }
            zip(outputZipFile, Constants.String.BLANK, zipEntryPathList, zipProcessor)
        }
    }

    /**
     * zip
     * @param outputZipFullFilename
     * @param inputZipFullFilename,can
     * blank,the entry will not from the input file
     * @param zipEntryPathList
     * @param zipProcessor
     */
    fun zip(outputZipFullFilename: String, inputZipFullFilename: String = Constants.String.BLANK, zipEntryPathList: List<ZipEntryPath>, zipProcessor: ((zipEntryName: String, inputStream: InputStream) -> InputStream)? = null) {
        if (outputZipFullFilename.isBlank()) {
            error("outputZipFullFilename is blank")
        }
        zip(File(outputZipFullFilename), inputZipFullFilename, zipEntryPathList, zipProcessor)
    }

    /**
     * zip
     * @param outputZipFile
     * @param inputZipFullFilename,can
     * blank,the entry will not from the input file
     * @param zipEntryPathList
     * @param zipProcessor
     */
    fun zip(outputZipFile: File, inputZipFullFilename: String = Constants.String.BLANK, zipEntryPathList: List<ZipEntryPath>, zipProcessor: ((zipEntryName: String, inputStream: InputStream) -> InputStream)? = null) {
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
            outputZipFile.createFileIncludeDirectory()
            zipOutputStream = ZipOutputStream(FileOutputStream(outputZipFile))
            if (inputZipFullFilename.isNotBlank()) {
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
                            newInputStream = zipProcessor(zipEntryName, inputStream)
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
                if (zipEntryPath.fullFilename.fileExists()) {
                    val inputStream = FileInputStream(zipEntryPath.fullFilename)
                    val newInputStream: InputStream
                    if (zipProcessor != null) {
                        newInputStream = zipProcessor(zipEntry.name, inputStream)
                        if (newInputStream !== inputStream) {
                            inputStream.close()
                        }
                    } else {
                        newInputStream = inputStream
                    }
                    addZipEntry(zipOutputStream, zipEntry, newInputStream)
                } else {
                    logger.error("add zip entry, file is not exists, full filename :%s", zipEntryPath.fullFilename)
                }
            }
        } catch (e: Exception) {
            throw ZipUtilException(e)
        } finally {
            try {
                zipOutputStream?.finish()
                zipOutputStream?.flush()
                zipOutputStream?.close()
                zipFile?.close()
            } catch (e: Exception) {
                throw ZipUtilException(e)
            }
        }
    }

    /**
     * merge zip file
     * @param zipFullFilenameList
     * @param zipOutputFullFilename
     */
    fun mergeZip(zipFullFilenameList: List<String>, zipOutputFullFilename: String) {
        FileUtil.createFileIncludeDirectory(zipOutputFullFilename)
        var zipOutputStream: ZipOutputStream? = null
        try {
            zipOutputStream = ZipOutputStream(FileOutputStream(zipOutputFullFilename))
            for (zipFullFilename in zipFullFilenameList) {
                if (FileUtil.exists(zipFullFilename)) {
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
        } catch (e: Exception) {
            throw ZipUtilException(e)
        } finally {
            try {
                zipOutputStream?.close()
            } catch (e: Exception) {
                throw ZipUtilException(e)
            }
        }
    }

    /**
     * add zip entry
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
     * unzip
     * @param zipFullFilename
     * @param outputDirectory
     * @param zipEntryNameList,if
     * it is null or empty,will unzip all
     * @return List<String>
    </String> */
    fun unzip(zipFullFilename: String, outputDirectory: String, zipEntryNameList: List<String> = emptyList()): List<String> {
        return unzip(File(zipFullFilename), outputDirectory, zipEntryNameList)
    }

    /**
     * unzip
     * @param file
     * @param outputDirectory
     * @param zipEntryNameList,if
     * it is null or empty,will unzip all
     * @return List<String>
    </String> */
    fun unzip(file: File, outputDirectory: String, zipEntryNameList: List<String> = emptyList()): List<String> {
        if (outputDirectory.isBlank()) {
            error("outputDirectory is blank")
        }
        return unzip(file, File(outputDirectory), zipEntryNameList)
    }

    /**
     * unzip
     * @param file
     * @param outputDirectoryFile
     * @param zipEntryNameList,if
     * it is null or empty,will unzip all
     * @return List<String>
    </String> */
    fun unzip(file: File, outputDirectoryFile: File, zipEntryNameList: List<String> = emptyList()): List<String> {
        outputDirectoryFile.createDirectory()
        val storeFileList = mutableListOf<String>()
        var zipFile: ZipFile? = null
        try {
            zipFile = ZipFile(file)
            val outputDirectoryAbsolutePath = outputDirectoryFile.absolutePath
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
                    val outputFile = File(outputDirectoryAbsolutePath, zipEntryName)
                    if (zipEntry.isDirectory) {
                        outputFile.createDirectory()
                    } else {
                        outputFile.createFileIncludeDirectory()
                        val outputStream = FileOutputStream(outputFile)
                        try {
                            inputStream.copyTo(outputStream)
                        } finally {
                            inputStream?.close()
                            outputStream.close()
                        }
                        storeFileList.add(outputFile.absolutePath)
                    }
                }
            }
        } catch (e: Exception) {
            throw ZipUtilException(e)
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
     * get zip entry map
     * @param zipFullFilename
     * @return Map<String></String>, String>
     */
    private fun getZipEntryMap(zipFullFilename: String): MutableMap<String, String> {
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
            throw ZipUtilException(e)
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close()
                } catch (e: IOException) {
                    throw ZipUtilException(e)
                }
            }
        }
        return map
    }

    /**
     * differ zip
     *
     * @param differentOutputFullFilename
     * @param oldZipFullFilename
     * @param newZipFullFilename
     * @param differZipProcessor
     */
    fun differZip(differentOutputFullFilename: String, oldZipFullFilename: String, newZipFullFilename: String, differZipProcessor: DifferZipProcessor? = null) {
        val map = getZipEntryMap(oldZipFullFilename)
        var newZipFile: ZipFile? = null
        var zipOutputStream: ZipOutputStream? = null
        try {
            newZipFile = ZipFile(newZipFullFilename)
            val entries = newZipFile.entries()
            FileUtil.createFileIncludeDirectory(differentOutputFullFilename)
            zipOutputStream = ZipOutputStream(FileOutputStream(differentOutputFullFilename))
            while (entries.hasMoreElements()) {
                val zipEntry = entries.nextElement()
                if (!zipEntry.isDirectory) {
                    val zipEntryName = zipEntry.name
                    val oldZipEntryHash = map[zipEntryName]
                    val newZipEntryHash: String = zipEntry.crc.toString() + Constants.Symbol.DOT + zipEntry.size
                    // old zip entry hash not exist is a new zip entry,if exist
                    // is a modified zip entry
                    if (oldZipEntryHash == null) {
                        var zipEntryInformation: DifferZipProcessor.ZipEntryInformation? = null
                        if (differZipProcessor != null) {
                            zipEntryInformation = differZipProcessor.foundAddedZipEntryProcess(zipEntryName)
                        }
                        if (zipEntryInformation != null && zipEntryInformation.needToSave) { // System.out.println(String.format("found added
                            // entry, key=%s(%s/%s)", new Object[] {
                            // zipEntryName, oldZipEntryHash, newZipEntryHash
                            // }));
                            val newZipEntry = zipEntryInformation.zipEntry ?: ZipEntry(zipEntryName)
                            val newZipEntryInputStream = zipEntryInformation.inputStream ?: newZipFile.getInputStream(zipEntry)
                            addZipEntry(zipOutputStream, newZipEntry, newZipEntryInputStream)
                        }
                    } else if (newZipEntryHash != oldZipEntryHash) {
                        var zipEntryInformation: DifferZipProcessor.ZipEntryInformation? = null
                        if (differZipProcessor != null) {
                            zipEntryInformation = differZipProcessor.foundModifiedZipEntryProcess(zipEntryName)
                        }
                        if (zipEntryInformation != null && zipEntryInformation.needToSave) { // System.out.println(String.format("found modified
// entry, key=%s(%s/%s)", new Object[] {
// zipEntryName, oldZipEntryHash, newZipEntryHash
// }));
                            val newZipEntry = zipEntryInformation.zipEntry ?: ZipEntry(zipEntryName)
                            addZipEntry(zipOutputStream, newZipEntry, zipEntryInformation.inputStream ?: newZipFile.getInputStream(zipEntry))
                        }
                    }
                    map.remove(zipEntryName)
                }
            }
            val deleteKeySet = map.keys
            for (deleteKey in deleteKeySet) {
                differZipProcessor?.foundDeletedZipEntryProcess(deleteKey)
            }
        } catch (e: Exception) {
            throw ZipUtilException(e)
        } finally {
            if (newZipFile != null) {
                try {
                    newZipFile.close()
                } catch (e: IOException) {
                    throw ZipUtilException(e)
                }
            }
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.finish()
                } catch (e: IOException) {
                    throw ZipUtilException(e)
                }
            }
        }
    }

    interface DifferZipProcessor {
        /**
         * found added zip entry process
         *
         * @param zipEntryName
         * @return boolean true is need to save in different.zip
         */
        fun foundAddedZipEntryProcess(zipEntryName: String): ZipEntryInformation?

        /**
         * found modified zip entry process
         *
         * @param zipEntryName
         * @return boolean true is need to save in different.zip
         */
        fun foundModifiedZipEntryProcess(zipEntryName: String): ZipEntryInformation?

        /**
         * found deleted zip entry process
         *
         * @param zipEntryName
         */
        fun foundDeletedZipEntryProcess(zipEntryName: String)

        class ZipEntryInformation(val needToSave: Boolean, val zipEntry: ZipEntry? = null, val inputStream: InputStream? = null)
    }

    class ZipEntryPath(val fullFilename: String, val zipEntry: ZipEntry, val replace: Boolean = false)

    class ZipUtilException(cause: Throwable) : RuntimeException(cause)
}