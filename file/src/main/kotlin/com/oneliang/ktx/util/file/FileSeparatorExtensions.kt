package com.oneliang.ktx.util.file

import java.io.File

fun File.separateTextContent(maxLineCountPerFile: Int = 100000) = FileSeparator.separateTextContent(this, maxLineCountPerFile)