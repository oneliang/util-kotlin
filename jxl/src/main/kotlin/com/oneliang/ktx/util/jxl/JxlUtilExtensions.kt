package com.oneliang.ktx.util.jxl

import jxl.Workbook
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun File.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0): List<Map<String, String>> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset)
}

fun InputStream.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0): List<Map<String, String>> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset)
}

fun <T> File.writeSimpleExcel(headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (value: T) -> String = { it.toString() }) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcel(writableWorkbook, headers, iterable, transform)
}

fun <T> OutputStream.writeSimpleExcel(headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (value: T) -> String = { it.toString() }) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcel(writableWorkbook, headers, iterable, transform)
}