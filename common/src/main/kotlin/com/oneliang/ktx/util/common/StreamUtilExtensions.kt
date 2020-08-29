package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.io.InputStream

fun InputStream.readContentIgnoreLine(encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK) = StreamUtil.readInputStreamContentIgnoreLine(this, encoding, append)

fun InputStream.readContentIgnoreLine(encoding: String = Constants.Encoding.UTF8, readContentProcessor: (line: String) -> Boolean) = StreamUtil.readInputStreamContentIgnoreLine(this, encoding, readContentProcessor)

fun InputStream.readLines(encoding: String = Constants.Encoding.UTF8, append: String = Constants.String.BLANK) = StreamUtil.readInputStreamLines(this, encoding)