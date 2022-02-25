package com.oneliang.ktx.util.file

import com.oneliang.ktx.Constants
import java.io.File
import java.io.InputStream

fun File.zip(outputZipFullFilename: String, fileSuffix: String = Constants.String.BLANK, zipProcessor: ((zipEntryName: String, inputStream: InputStream) -> InputStream)? = null) = ZipUtil.zip(this, outputZipFullFilename, fileSuffix, zipProcessor)

fun File.zip(outputZipFile: File, fileSuffix: String = Constants.String.BLANK, zipProcessor: ((zipEntryName: String, inputStream: InputStream) -> InputStream)? = null) = ZipUtil.zip(this, outputZipFile, fileSuffix, zipProcessor)

fun File.unZip(outputDirectory: String, zipEntryNameList: List<String> = emptyList()) = ZipUtil.unzip(this, outputDirectory, zipEntryNameList)

fun File.unZip(outputDirectoryFile: File, zipEntryNameList: List<String> = emptyList()) = ZipUtil.unzip(this, outputDirectoryFile, zipEntryNameList)