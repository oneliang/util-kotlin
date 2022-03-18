package com.oneliang.ktx.util.jxl

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.ObjectUtil
import com.oneliang.ktx.util.common.nullToBlank
import com.oneliang.ktx.util.common.toMapWithIndex
import com.oneliang.ktx.util.file.createFileIncludeDirectory
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableCell
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import java.io.File
import kotlin.reflect.KClass

inline fun WritableWorkbook.use(block: (WritableWorkbook) -> Unit) {
    try {
        block(this)
    } finally {
        this.write()
        this.close()
    }
}

object JxlUtil {

    private val DEFAULT_JXL_PROCESSOR = DefaultJxlProcessor()

    /**
     * copy excel
     * @param excelFile
     * @param newExcelFile
     * @param jxlProcessor
     * @param instance
     */
    @Throws(Exception::class)
    fun <T : Any> copyExcel(excelFile: String, newExcelFile: String, instance: T, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR) {
        val workbook = Workbook.getWorkbook(File(excelFile))
        val writableWorkbook = Workbook.createWorkbook(File(newExcelFile), workbook)
        writableWorkbook.use {
            val sheets = writableWorkbook.sheets
            for (sheet in sheets) {
                val columns = sheet.columns
                for (i in 0 until columns) {
                    val cells = sheet.getColumn(i)
                    for (cell in cells) {
                        jxlProcessor.copyProcess(cell, instance)
                    }
                }
            }
        }
    }

    /**
     * read simple excel
     * @param <T>
     * @param fullFilename
     * @param kClass
     * @param jxlMappingBean
     * @param dataRowOffset
     * @param jxlProcessor
     * @return ReadResult<T>
    </T></T> */
    @Throws(Exception::class)
    fun <T : Any> readSimpleExcel(fullFilename: String, kClass: KClass<T>, jxlMappingBean: JxlMappingBean, headerRowIndex: Int = -1, dataRowOffset: Int = 0, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR): ReadResult<T> {
        val workbook = Workbook.getWorkbook(File(fullFilename))
        return readSimpleExcel(workbook, kClass, jxlMappingBean, headerRowIndex, dataRowOffset, jxlProcessor)
    }

    /**
     * read simple excel
     * @param <T>
     * @param workbook
     * @param kClass
     * @param jxlMappingBean
     * @param headerRowIndex
     * @param dataRowOffset
     * @param jxlProcessor
     * @return ReadResult<T>
    </T></T> */
    @Throws(Exception::class)
    fun <T : Any> readSimpleExcel(workbook: Workbook, kClass: KClass<T>, jxlMappingBean: JxlMappingBean, headerRowIndex: Int = -1, dataRowOffset: Int = 0, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR): ReadResult<T> {
        return readSimpleExcelForDataRow(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
            createRowInstance(sheet, rowIndex, kClass, existHeader, headerIndexMap, jxlMappingBean, jxlProcessor)
        }
    }

    /**
     * read simple excel
     * @param <T>
     * @param workbook
     * @param headerRowIndex
     * @param dataRowOffset
     * @return ReadResult<Map<String, String>>
    </T></T> */
    @Throws(Exception::class)
    fun readSimpleExcel(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0): ReadResult<Map<String, String>> {
        return readSimpleExcelForDataRow(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
            createRowData(sheet, rowIndex, existHeader, headerIndexMap, { _, cell, _, _ -> cell.contents.nullToBlank() }) { it }
        }
    }

    /**
     * read simple excel
     * @param <T>
     * @param workbook
     * @param headerRowIndex
     * @param dataRowOffset
     * @param valueTransform
     * @return ReadResult<Map<String, V>>
    </T></T> */
    @Throws(Exception::class)
    fun <V : Any> readSimpleExcel(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0, valueTransform: (header: String, cell: Cell, rowIndex: Int, columnIndex: Int) -> V): ReadResult<Map<String, V>> {
        return readSimpleExcelForDataRow(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
            createRowData(sheet, rowIndex, existHeader, headerIndexMap, valueTransform) { it }
        }
    }

    /**
     * read simple excel
     * @param <T>
     * @param workbook
     * @param headerRowIndex
     * @param dataRowOffset
     * @param valueTransform
     * @param rowDataTransform
     * @return ReadResult<T>
    </T></T> */
    @Throws(Exception::class)
    fun <V : Any, T : Any> readSimpleExcel(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0, valueTransform: (header: String, cell: Cell, rowIndex: Int, columnIndex: Int) -> V, rowDataTransform: (dataMap: Map<String, V>) -> T): ReadResult<T> {
        return readSimpleExcelForDataRow(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
            createRowData(sheet, rowIndex, existHeader, headerIndexMap, valueTransform, rowDataTransform)
        }
    }

    /**
     * read simple excel for data row
     * @param <T>
     * @param workbook
     * @param headerRowIndex
     * @param dataRowOffset
     * @param readDataRow
     * @return ReadResult<T>
    </T></T> */
    fun <T : Any> readSimpleExcelForDataRow(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0, readDataRow: (sheet: Sheet, rowIndex: Int, existHeader: Boolean, headerIndexMap: Map<String, Int>) -> T): ReadResult<T> {
        val list = mutableListOf<T>()
        val sheets = workbook.sheets
        val sheet = (if (sheets.isNotEmpty()) sheets[0] else null) ?: return ReadResult(dataList = list)
        val rows = sheet.rows
        if (rows < headerRowIndex + 1) {
            return ReadResult(dataList = list)
        }

        //find header maybe has no header
        var headerIndexMap = emptyMap<String, Int>()
        if (headerRowIndex >= 0) {
            val headerCellArray = sheet.getRow(headerRowIndex) ?: emptyArray()
            headerIndexMap = headerCellArray.toMapWithIndex { index, cell ->
                cell.contents.nullToBlank() to index
            }
        }
        val existHeader = headerIndexMap.isNotEmpty()
        for (i in dataRowOffset until rows) {
            if (i == headerRowIndex) {//header index
                continue
            }
            val instance = readDataRow(sheet, i, existHeader, headerIndexMap)
            list += instance
        }
        return ReadResult(headerIndexMap, list)
    }

    /**
     * create row data
     */
    private fun <V : Any, T : Any> createRowData(sheet: Sheet, rowIndex: Int, existHeader: Boolean, headerIndexMap: Map<String, Int>, valueTransform: (header: String, cell: Cell, rowIndex: Int, columnIndex: Int) -> V, rowDataTransform: (dataMap: Map<String, V>) -> T): T {
        val map = mutableMapOf<String, V>()
        if (existHeader) {
            headerIndexMap.forEach { (header, columnIndex) ->
                val cell = sheet.getCell(columnIndex, rowIndex)
                map[header] = valueTransform(header, cell, rowIndex, columnIndex)
            }
        } else {
            val columns = sheet.columns
            for (columnIndex in 0 until columns) {
                val cell = sheet.getCell(columnIndex, rowIndex)
                val header = columnIndex.toString()
                map[header] = valueTransform(header, cell, rowIndex, columnIndex)
            }
        }
        return rowDataTransform(map)
    }

    /**
     * create row instance
     */
    private fun <T : Any> createRowInstance(sheet: Sheet, rowIndex: Int, kClass: KClass<T>, existHeader: Boolean, headerIndexMap: Map<String, Int>, jxlMappingBean: JxlMappingBean, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR): T {
        val instance = kClass.java.newInstance()
        val methods = kClass.java.methods
        for (method in methods) {
            val methodName = method.name
            var fieldName = Constants.String.BLANK
            if (methodName.startsWith(Constants.Method.PREFIX_SET)) {
                fieldName = ObjectUtil.methodNameToFieldName(Constants.Method.PREFIX_SET, methodName)
            }
            if (fieldName.isNotBlank()) {
                val columnIndex = if (existHeader) {
                    val header = jxlMappingBean.getHeader(fieldName)
                    headerIndexMap[header] ?: -1
                } else {
                    jxlMappingBean.getIndex(fieldName)
                }
                if (columnIndex >= 0) {
                    val cell = sheet.getCell(columnIndex, rowIndex)
                    val classes = method.parameterTypes
                    if (classes.size == 1) {
                        val value = jxlProcessor.readProcess(classes[0].kotlin, cell)
                        method.invoke(instance, value)
                    }
                }
            }
        }
        return instance
    }

    /**
     * write simple excel for multi sheet
     */
    @Throws(Exception::class)
    fun writeSimpleExcel(fullFilename: String, writeOptions: Array<WriteOption>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcel(it, writeOptions)
        }
    }

    /**
     * write simple excel for multi sheet
     */
    @Throws(Exception::class)
    fun writeSimpleExcel(writableWorkbook: WritableWorkbook, writeOptions: Array<WriteOption>) {
        writeOptions.forEach {
            val writableSheet = getOrCreateSheet(writableWorkbook, it.sheetName, it.sheetIndex)
            var row = it.startRow
            if (it.headers.isNotEmpty()) {
                for ((column, header) in it.headers.withIndex()) {
                    val cell = Label(column, row, header)
                    writableSheet.addCell(cell)
                }
                row++
            }
            it.writeDataRows(writableSheet, row)
        }
    }


    /**
     * write simple excel for default sheet
     */
    @Throws(Exception::class)
    fun writeSimpleExcel(fullFilename: String, startRow: Int = 0, headers: Array<String> = emptyArray(), writeDataRows: (writableSheet: WritableSheet, currentRow: Int) -> Unit) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcel(writableWorkbook, "sheet", 0, startRow, headers, writeDataRows)
        }
    }

    /**
     * write simple excel for default sheet
     */
    @Throws(Exception::class)
    fun writeSimpleExcel(writableWorkbook: WritableWorkbook, startRow: Int = 0, headers: Array<String> = emptyArray(), writeDataRows: (writableSheet: WritableSheet, currentRow: Int) -> Unit) {
        writeSimpleExcel(writableWorkbook, "sheet", 0, startRow, headers, writeDataRows)
    }

    /**
     * write simple excel for one sheet
     */
    @Throws(Exception::class)
    fun writeSimpleExcel(fullFilename: String, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), writeDataRows: (writableSheet: WritableSheet, currentRow: Int) -> Unit) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcel(it, sheetName, sheetIndex, startRow, headers, writeDataRows)
        }
    }

    /**
     * write simple excel for one sheet
     */
    @Throws(Exception::class)
    fun writeSimpleExcel(writableWorkbook: WritableWorkbook, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), writeDataRows: (writableSheet: WritableSheet, currentRow: Int) -> Unit) {
        val writableSheet = getOrCreateSheet(writableWorkbook, sheetName, sheetIndex)
        var row = startRow
        if (headers.isNotEmpty()) {
            for ((column, header) in headers.withIndex()) {
                val cell = Label(column, row, header)
                writableSheet.addCell(cell)
            }
            row++
        }
        writeDataRows(writableSheet, row)
    }

    /**
     * write simple excel for multi sheet
     */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(fullFilename: String, writeOptionForArrays: Array<WriteOptionForArray<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForArray(it, writeOptionForArrays)
        }
    }

    /**
     * write simple excel for multi sheet
     */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(writableWorkbook: WritableWorkbook, writeOptionForArrays: Array<WriteOptionForArray<T>>) {
        writeOptionForArrays.forEach {
            writeSimpleExcel(writableWorkbook, it.sheetName, it.sheetIndex, it.startRow, it.headers) { writableSheet, currentRow ->
                var row = currentRow
                for (array in it.iterable) {
                    array.forEachIndexed { index, value ->
                        val transformValue = it.transform(index, row, value)
                        val writableCell = DEFAULT_JXL_PROCESSOR.writeProcess(index, row, Constants.String.BLANK, transformValue)
                        writableSheet.addCell(writableCell)
                    }
                    row++
                }
            }
        }
    }

    /**
     * write simple excel for array, for default sheet
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(fullFilename: String, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForArray(writableWorkbook, arrayOf(WriteOptionForArray("sheet", 0, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
        }
    }

    /**
     * write simple excel for array, for default sheet
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(writableWorkbook: WritableWorkbook, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
        writeSimpleExcelForArray(writableWorkbook, arrayOf(WriteOptionForArray("sheet", 0, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
    }

    /**
     * write simple excel for array, for default sheet
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForArray(fullFilename: String, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForArray(writableWorkbook, arrayOf(WriteOptionForArray("sheet", 0, startRow, headers, iterable, transform)))
        }
    }

    /**
     * write simple excel for array, for default sheet
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForArray(writableWorkbook: WritableWorkbook, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        writeSimpleExcelForArray(writableWorkbook, arrayOf(WriteOptionForArray("sheet", 0, startRow, headers, iterable, transform)))
    }

    /**
     * write simple excel for array, for one sheet
     * @param <T>
     * @param fullFilename
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(fullFilename: String, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForArray(it, arrayOf(WriteOptionForArray(sheetName, sheetIndex, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
        }
    }

    /**
     * write simple excel for array, for one sheet
     * @param <T>
     * @param writableWorkbook
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(writableWorkbook: WritableWorkbook, sheetName: String, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
        writeSimpleExcelForArray(writableWorkbook, arrayOf(WriteOptionForArray(sheetName, sheetIndex, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
    }

    /**
     * write simple excel for array, for one sheet
     * @param <T>
     * @param fullFilename
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForArray(fullFilename: String, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForArray(it, arrayOf(WriteOptionForArray(sheetName, sheetIndex, startRow, headers, iterable, transform)))
        }
    }

    /**
     * write simple excel for array, for one sheet
     * @param <T>
     * @param writableWorkbook
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForArray(writableWorkbook: WritableWorkbook, sheetName: String, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        writeSimpleExcelForArray(writableWorkbook, arrayOf(WriteOptionForArray(sheetName, sheetIndex, startRow, headers, iterable, transform)))
    }

    /**
     * write simple excel for multi sheet
     * @param fullFilename
     * @param writeOptionForIterables
     */
    fun <T> writeSimpleExcelForIterable(fullFilename: String, writeOptionForIterables: Array<WriteOptionForIterable<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForIterable(it, writeOptionForIterables)
        }
    }

    /**
     * write simple excel for multi sheet
     * @param writableWorkbook
     * @param writeOptionForIterables
     */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForIterable(writableWorkbook: WritableWorkbook, writeOptionForIterables: Array<WriteOptionForIterable<T>>) {
        writeOptionForIterables.forEach {
            writeSimpleExcel(writableWorkbook, it.sheetName, it.sheetIndex, it.startRow, it.headers) { writableSheet, currentRow ->
                var row = currentRow
                for (innerIterable in it.iterable) {
                    innerIterable.forEachIndexed { index, value ->
                        val transformValue = it.transform(index, row, value)
                        val writableCell = DEFAULT_JXL_PROCESSOR.writeProcess(index, row, Constants.String.BLANK, transformValue)
                        writableSheet.addCell(writableCell)
                    }
                    row++
                }
            }
        }
    }

    /**
     * write simple excel for iterable, default sheet
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForIterable(fullFilename: String, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable("sheet", 0, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
        }
    }

    /**
     * write simple excel for iterable, default sheet
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    fun <T> writeSimpleExcelForIterable(writableWorkbook: WritableWorkbook, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>) {
        writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable("sheet", 0, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
    }

    /**
     * write simple excel for iterable, default sheet
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForIterable(fullFilename: String, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable("sheet", 0, startRow, headers, iterable, transform)))
        }
    }

    /**
     * write simple excel for iterable, default sheet
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    fun <T, R> writeSimpleExcelForIterable(writableWorkbook: WritableWorkbook, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable("sheet", 0, startRow, headers, iterable, transform)))
    }

    /**
     * write simple excel for iterable, for one sheet
     * @param <T>
     * @param fullFilename
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForIterable(fullFilename: String, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable(sheetName, sheetIndex, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
        }
    }

    /**
     * write simple excel for iterable, for one sheet
     * @param <T>
     * @param writableWorkbook
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
    </T> */
    fun <T> writeSimpleExcelForIterable(writableWorkbook: WritableWorkbook, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>) {
        writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable(sheetName, sheetIndex, startRow, headers, iterable) { _: Int, _: Int, value: T -> value }))
    }

    /**
     * write simple excel for iterable, for one sheet
     * @param <T>
     * @param fullFilename
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForIterable(fullFilename: String, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable(sheetName, sheetIndex, startRow, headers, iterable, transform)))
        }
    }

    /**
     * write simple excel for iterable, for one sheet
     * @param <T>
     * @param writableWorkbook
     * @param sheetName
     * @param sheetIndex
     * @param startRow
     * @param headers
     * @param iterable
     * @param transform
    </T> */
    fun <T, R> writeSimpleExcelForIterable(writableWorkbook: WritableWorkbook, sheetName: String = Constants.String.BLANK, sheetIndex: Int, startRow: Int = 0, headers: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        writeSimpleExcelForIterable(writableWorkbook, arrayOf(WriteOptionForIterable(sheetName, sheetIndex, startRow, headers, iterable, transform)))
    }

    /**
     * write simple excel
     * @param <T>
     * @param headers
     * @param iterable
     * @param jxlMappingBean
     * @param fullFilename
    </T> */
    @Throws(Exception::class)
    fun <T : Any> writeSimpleExcel(headers: Array<String> = emptyArray(), iterable: Iterable<T>, jxlMappingBean: JxlMappingBean?, fullFilename: String, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR) {
        val newHeaders = if (headers.isEmpty()) {
            val jxlMappingColumnBeanList = jxlMappingBean!!.jxlMappingColumnBeanList
            val headerList = arrayListOf<String>()
            for (jxlMappingColumnBean in jxlMappingColumnBeanList) {
                headerList[jxlMappingColumnBean.index] = jxlMappingColumnBean.header
            }
            headerList
        } else {
            headers.toList()
        }
        if (jxlMappingBean == null) {
            return
        }
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename).apply { this.createFileIncludeDirectory() })
        writableWorkbook.use {
            writeSimpleExcel(it, headers = newHeaders.toTypedArray()) { sheet, currentRow ->
                val jxlMappingColumnBeanList = jxlMappingBean.jxlMappingColumnBeanList
                var row = currentRow
                for (instance in iterable) {
                    for (jxlMappingColumnBean in jxlMappingColumnBeanList) {
                        val fieldName = jxlMappingColumnBean.field
                        val columnIndex = jxlMappingColumnBean.index
                        val methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName)
                        val writableCell = jxlProcessor.writeProcess(columnIndex, row, fieldName, methodReturnValue)
                        sheet.addCell(writableCell)
                    }
                    row++
                }
            }
        }
    }

    /**
     * get or create workbook
     */
    fun getOrCreateWorkbook(file: File): WritableWorkbook {
        return if (!file.exists()) {
            file.createFileIncludeDirectory()
            Workbook.createWorkbook(file)
        } else {
            val workbook = Workbook.getWorkbook(file)
            val writableWorkbook = Workbook.createWorkbook(file, workbook)
            workbook.close()
            writableWorkbook
        }
    }

    /**
     * get or create sheet, only use index to get
     */
    private fun getOrCreateSheet(writableWorkbook: WritableWorkbook, sheetName: String, sheetIndex: Int): WritableSheet {
        val sheet: WritableSheet? = if (sheetIndex < writableWorkbook.numberOfSheets) {
            writableWorkbook.getSheet(sheetIndex)//will throw exception
        } else {
            null
        }
        if (sheet != null) {
            return sheet
        }
        var fixSheetName = sheetName
        if (fixSheetName.isBlank()) {
            fixSheetName = "sheet$sheetIndex"
        }
        return writableWorkbook.createSheet(fixSheetName, sheetIndex)
    }

    /**
     * class read result
     */
    class ReadResult<T>(val headerIndexMap: Map<String, Int> = emptyMap(), val dataList: List<T> = emptyList())

    /**
     * class write option
     */
    abstract class AbstractWriteOption(
        val sheetName: String = Constants.String.BLANK,
        val sheetIndex: Int,
        val startRow: Int = 0,
        val headers: Array<String> = emptyArray()
    )

    class WriteOption(
        sheetName: String = Constants.String.BLANK,
        sheetIndex: Int,
        startRow: Int = 0,
        headers: Array<String> = emptyArray(),
        val writeDataRows: (writableSheet: WritableSheet, currentRow: Int) -> Unit
    ) : AbstractWriteOption(sheetName, sheetIndex, startRow, headers)

    class WriteOptionForArray<T>(
        sheetName: String = Constants.String.BLANK,
        sheetIndex: Int,
        startRow: Int = 0,
        headers: Array<String> = emptyArray(),
        val iterable: Iterable<Array<T>>,
        val transform: (column: Int, row: Int, value: T) -> Any? = { _: Int, _: Int, value: T -> value }
    ) : AbstractWriteOption(sheetName, sheetIndex, startRow, headers)

    class WriteOptionForIterable<T>(
        sheetName: String = Constants.String.BLANK,
        sheetIndex: Int,
        startRow: Int = 0,
        headers: Array<String> = emptyArray(),
        val iterable: Iterable<Iterable<T>>,
        val transform: (column: Int, row: Int, value: T) -> Any? = { _: Int, _: Int, value: T -> value }
    ) : AbstractWriteOption(sheetName, sheetIndex, startRow, headers)

    interface JxlProcessor {

        /**
         * copy process
         * @param <T>
         * @param cell
         * @param instance
        </T> */
        fun <T : Any> copyProcess(cell: Cell, instance: T)

        /**
         * read process
         * @param <T>
         * @param parameterClass
         * @param cell
         * @return Object
        </T> */
        fun <T : Any> readProcess(parameterClass: KClass<T>, cell: Cell): Any

        /**
         * write process
         * @param column
         * @param row
         * @param fieldName
         * @param value
         * @return String
        </T> */
        fun writeProcess(column: Int, row: Int, fieldName: String, value: Any?): WritableCell
    }
}
