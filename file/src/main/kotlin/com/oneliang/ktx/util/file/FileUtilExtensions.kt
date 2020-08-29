package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import java.io.File
import java.io.InputStream

fun File.readContentIgnoreLine(encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK) = FileUtil.readFileContentIgnoreLine(this.absolutePath, encoding, append)

fun File.readContentIgnoreLine(encoding: String = Constants.Encoding.UTF8, readFileContentProcessor: (line: String) -> Boolean) = FileUtil.readFileContentIgnoreLine(this.absolutePath, encoding, readFileContentProcessor)

fun File.deleteAll() = FileUtil.deleteAllFile(this)

fun File.findMatchFile(matchOption: FileUtil.MatchOption, onMatch: (file: File) -> String = { it.absolutePath }) = FileUtil.findMatchFile(this, matchOption, onMatch)

fun File.create() = FileUtil.createFile(this)

fun File.createDirectory() = FileUtil.createDirectory(this)

fun String.fileExists(): Boolean = FileUtil.exists(this)

fun File.hasFile(fileSuffix: String = Constants.String.BLANK): Boolean = FileUtil.hasFile(this, fileSuffix)