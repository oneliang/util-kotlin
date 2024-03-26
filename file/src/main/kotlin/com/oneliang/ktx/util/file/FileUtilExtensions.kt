package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import java.io.BufferedWriter
import java.io.File
import java.util.*

fun File.readContentIgnoreLine(encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK) = FileUtil.readFileContentIgnoreLine(this, encoding, append)

fun File.readContentEachLine(encoding: String = Constants.Encoding.UTF8, readLineProcessor: (line: String) -> Boolean) = FileUtil.readFileContentEachLine(this, encoding, readLineProcessor)

fun File.deleteAll(fileSuffix: String = Constants.String.BLANK) = FileUtil.deleteAllFile(this, fileSuffix)

fun File.findMatchFile(matchOption: FileUtil.MatchOption, onMatch: (file: File) -> String = { it.absolutePath }) = FileUtil.findMatchFile(this, matchOption, onMatch)

/**
 * create file and parent directory, full filename, single empty file.
 */
fun File.createFileIncludeDirectory() = FileUtil.createFileIncludeDirectory(this)

fun File.createDirectory() = FileUtil.createDirectory(this)

fun String.fileExists(): Boolean = FileUtil.exists(this)

fun File.hasFile(fileSuffix: String = Constants.String.BLANK): Boolean = FileUtil.hasFile(this, fileSuffix)

fun File.write(byteArray: ByteArray, append: Boolean = false) = FileUtil.writeFile(this, byteArray, append)

fun File.writeContent(encoding: String = Constants.Encoding.UTF8, append: Boolean = false, writeFileContentProcessor: ((bufferedWriter: BufferedWriter) -> Unit)? = null) =
    FileUtil.writeFileContent(this, encoding, append, writeFileContentProcessor)

fun String.toProperties(): Properties = FileUtil.getProperties(this)

fun String.toPropertiesAutoCreate(): Properties = FileUtil.getPropertiesAutoCreate(this)

fun File.toProperties(): Properties = FileUtil.getProperties(this)

fun File.toPropertiesAutoCreate(): Properties = FileUtil.getPropertiesAutoCreate(this)

fun File.saveProperties(properties: Properties) = FileUtil.saveProperties(properties, this)

fun Properties.saveTo(file: File) = FileUtil.saveProperties(this, file)

fun Properties.saveTo(fullFilename: String) = FileUtil.saveProperties(this, fullFilename)

fun <K : Any, V> Map<K, V>.saveTo(file: File, gcCount: Int = -1) = FileUtil.saveMap(this, file, gcCount)

fun <K : Any, V> Map<K, V>.saveTo(fullFilename: String, gcCount: Int = -1) = FileUtil.saveMap(this, fullFilename, gcCount)