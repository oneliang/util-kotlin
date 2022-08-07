package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.InputStream

fun InputStream.readContentIgnoreLine(encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK) = StreamUtil.readInputStreamContentIgnoreLine(this, encoding, append)

fun InputStream.readContentEachLine(encoding: String = Constants.Encoding.UTF8, readLineProcessor: (line: String) -> Boolean) = StreamUtil.readInputStreamContentEachLine(this, encoding, readLineProcessor)

fun InputStream.readLines(encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK) = StreamUtil.readInputStreamLines(this, encoding)