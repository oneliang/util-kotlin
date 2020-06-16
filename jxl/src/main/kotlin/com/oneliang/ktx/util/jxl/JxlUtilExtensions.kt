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

fun <T> File.writeSimpleExcel(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable)
}

fun <T, R> File.writeSimpleExcel(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable, transform)
}

fun <T> OutputStream.writeSimpleExcel(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable)
}

fun <T, R> OutputStream.writeSimpleExcel(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
    val writableWorkbook = Workbook.createWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable, transform)
}

fun <T> File.writeOrUpdateSimpleExcelForArray(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
    val writableWorkbook = JxlUtil.getOrCreateWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable)
}

fun <T, R> File.writeOrUpdateSimpleExcelForArray(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
    val writableWorkbook = JxlUtil.getOrCreateWorkbook(this)
    JxlUtil.writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable, transform)
}

fun <T> File.writeOrUpdateSimpleExcelForIterable(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>) {
    val writableWorkbook = JxlUtil.getOrCreateWorkbook(this)
    JxlUtil.writeSimpleExcelForIterable(writableWorkbook, startRow, headerArray, iterable)
}

fun <T, R> File.writeOrUpdateSimpleExcelForIterable(startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>, transform: (column: Int, row: Int, value: T) -> R) {
    val writableWorkbook = JxlUtil.getOrCreateWorkbook(this)
    JxlUtil.writeSimpleExcelForIterable(writableWorkbook, startRow, headerArray, iterable, transform)
}