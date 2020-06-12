package com.oneliang.ktx.util.jxl

import jxl.Sheet
import jxl.Workbook
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun <T : Any> File.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0, transform: (dataMap: Map<String, String>) -> T): List<T> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset, transform)
}

fun <T : Any> InputStream.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0, transform: (dataMap: Map<String, String>) -> T): List<T> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset, transform)
}

fun File.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0): List<Map<String, String>> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset)
}

fun InputStream.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0): List<Map<String, String>> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset)
}

fun <T : Any> File.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0, readDataRow: (sheet: Sheet, rowIndex: Int, existHeader: Boolean, headerIndexMap: Map<String, Int>) -> T): List<T> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset, readDataRow)
}

fun <T : Any> InputStream.readSimpleExcel(headerRowIndex: Int = -1, dataRowOffset: Int = 0, writeDataRows: (sheet: Sheet, rowIndex: Int, existHeader: Boolean, headerIndexMap: Map<String, Int>) -> T): List<T> {
    val workbook = Workbook.getWorkbook(this)
    return JxlUtil.readSimpleExcel(workbook, headerRowIndex, dataRowOffset, writeDataRows)
}


fun <T> File.writeSimpleExcel(headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (value: T) -> String = { it.toString() }) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, headerArray = headerArray, iterable = iterable, transform = transform)
}

fun <T> OutputStream.writeSimpleExcel(headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (value: T) -> String = { it.toString() }) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, headerArray = headerArray, iterable = iterable, transform = transform)
}

fun <T> File.writeOrModifySimpleExcel(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (value: T) -> String = { it.toString() }) {
    val writableWorkbook = JxlUtil.getOrCreateWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable, transform)
}