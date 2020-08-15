package com.oneliang.ktx.util.jxl

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.ObjectUtil
import com.oneliang.ktx.util.common.nullToBlank
import com.oneliang.ktx.util.common.toMapWithIndex
import com.oneliang.ktx.util.file.create
import jxl.Cell
import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableCell
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import java.io.File
import kotlin.reflect.KClass

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
        writableWorkbook.write()
        writableWorkbook.close()
    }

    /**
     * read simple excel
     * @param <T>
     * @param fullFilename
     * @param kClass
     * @param jxlMappingBean
     * @param dataRowOffset
     * @param jxlProcessor
     * @return List<T>
    </T></T> */
    @Throws(Exception::class)
    fun <T : Any> readSimpleExcel(fullFilename: String, kClass: KClass<T>, jxlMappingBean: JxlMappingBean, headerRowIndex: Int = -1, dataRowOffset: Int = 0, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR): List<T> {
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
     * @return List<T>
    </T></T> */
    @Throws(Exception::class)
    fun <T : Any> readSimpleExcel(workbook: Workbook, kClass: KClass<T>, jxlMappingBean: JxlMappingBean, headerRowIndex: Int = -1, dataRowOffset: Int = 0, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR): List<T> {
        return readSimpleExcel(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
            createRowInstance(sheet, rowIndex, kClass, existHeader, headerIndexMap, jxlMappingBean, jxlProcessor)
        }
    }

    /**
     * read simple excel
     * @param <T>
     * @param workbook
     * @param headerRowIndex
     * @param dataRowOffset
     * @return List<T>
    </T></T> */
    @Throws(Exception::class)
    fun readSimpleExcel(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0): List<Map<String, String>> {
        return readSimpleExcel(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
            createRowData(sheet, rowIndex, existHeader, headerIndexMap, { cell, _, _ -> cell.contents.nullToBlank() }) { it }
        }
    }

    /**
     * read simple excel
     * @param <T>
     * @param workbook
     * @param headerRowIndex
     * @param dataRowOffset
     * @param valueTransform
     * @return List<T>
    </T></T> */
    @Throws(Exception::class)
    fun <V : Any> readSimpleExcel(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0, valueTransform: (cell: Cell, rowIndex: Int, columnIndex: Int) -> V): List<Map<String, V>> {
        return readSimpleExcel(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
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
     * @return List<T>
    </T></T> */
    @Throws(Exception::class)
    fun <V : Any, T : Any> readSimpleExcel(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0, valueTransform: (cell: Cell, rowIndex: Int, columnIndex: Int) -> V, rowDataTransform: (dataMap: Map<String, V>) -> T): List<T> {
        return readSimpleExcel(workbook, headerRowIndex, dataRowOffset) { sheet, rowIndex, existHeader, headerIndexMap ->
            createRowData(sheet, rowIndex, existHeader, headerIndexMap, valueTransform, rowDataTransform)
        }
    }

    /**
     * read simple excel
     * @param <T>
     * @param workbook
     * @param headerRowIndex
     * @param dataRowOffset
     * @param readDataRow
     * @return List<T>
    </T></T> */
    fun <T : Any> readSimpleExcel(workbook: Workbook, headerRowIndex: Int = -1, dataRowOffset: Int = 0, readDataRow: (sheet: Sheet, rowIndex: Int, existHeader: Boolean, headerIndexMap: Map<String, Int>) -> T): List<T> {
        val list = mutableListOf<T>()
        val sheets = workbook.sheets
        val sheet = (if (sheets.isNotEmpty()) sheets[0] else null) ?: return list
        val rows = sheet.rows
        if (rows < headerRowIndex + 1) {
            return list
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
            val instance = readDataRow(sheet, i, existHeader, headerIndexMap)
            list += instance
        }
        return list
    }

    /**
     * create row data
     */
    private fun <V : Any, T : Any> createRowData(sheet: Sheet, rowIndex: Int, existHeader: Boolean, headerIndexMap: Map<String, Int>, valueTransform: (cell: Cell, rowIndex: Int, columnIndex: Int) -> V, rowDataTransform: (dataMap: Map<String, V>) -> T): T {
        val map = mutableMapOf<String, V>()
        if (existHeader) {
            headerIndexMap.forEach { (header, columnIndex) ->
                val cell = sheet.getCell(columnIndex, rowIndex)
                map[header] = valueTransform(cell, rowIndex, columnIndex)
            }
        } else {
            val columns = sheet.columns
            for (columnIndex in 0 until columns) {
                val cell = sheet.getCell(columnIndex, rowIndex)
                map[columnIndex.toString()] = valueTransform(cell, rowIndex, columnIndex)
            }
        }
        return rowDataTransform(map)
    }

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

    @Throws(Exception::class)
    fun writeSimpleExcel(writableWorkbook: WritableWorkbook, startRow: Int = 0, headerArray: Array<String> = emptyArray(), writeDataRows: (writableSheet: WritableSheet, currentRow: Int) -> Unit) {
        val writableSheet = getOrCreateSheet(writableWorkbook, "sheet", 0)
        var row = startRow
        if (headerArray.isNotEmpty()) {
            for ((column, header) in headerArray.withIndex()) {
                val cell = Label(column, row, header)
                writableSheet.addCell(cell)
            }
            row++
        }
        writeDataRows(writableSheet, row)
        writableWorkbook.write()
        writableWorkbook.close()
    }

    /**
     * write simple excel for array
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headerArray
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(fullFilename: String, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename))
        writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable) { _: Int, _: Int, value: T -> value }
    }

    /**
     * write simple excel for array
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headerArray
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForArray(fullFilename: String, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename))
        writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable, transform)
    }

    /**
     * write simple excel for array
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headerArray
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForArray(writableWorkbook: WritableWorkbook, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>) {
        writeSimpleExcelForArray(writableWorkbook, startRow, headerArray, iterable) { _: Int, _: Int, value: T -> value }
    }

    /**
     * write simple excel for array
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headerArray
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForArray(writableWorkbook: WritableWorkbook, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Array<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        writeSimpleExcel(writableWorkbook, startRow, headerArray) { writableSheet, currentRow ->
            var row = currentRow
            for (array in iterable) {
                array.forEachIndexed { index, value ->
                    val transformValue = transform(index, row, value)
                    val writableCell = DEFAULT_JXL_PROCESSOR.writeProcess(index, row, Constants.String.BLANK, transformValue)
                    writableSheet.addCell(writableCell)
                }
                row++
            }
        }
    }

    /**
     * write simple excel for iterable
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headerArray
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForIterable(fullFilename: String, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename))
        writeSimpleExcelForIterable(writableWorkbook, startRow, headerArray, iterable) { _: Int, _: Int, value: T -> value }
    }

    /**
     * write simple excel for iterable
     * @param <T>
     * @param fullFilename
     * @param startRow
     * @param headerArray
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForIterable(fullFilename: String, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename))
        writeSimpleExcelForIterable(writableWorkbook, startRow, headerArray, iterable, transform)
    }

    /**
     * write simple excel for iterable
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headerArray
     * @param iterable
    </T> */
    @Throws(Exception::class)
    fun <T> writeSimpleExcelForIterable(writableWorkbook: WritableWorkbook, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>) {
        writeSimpleExcelForIterable(writableWorkbook, startRow, headerArray, iterable) { _: Int, _: Int, value: T -> value }
    }

    /**
     * write simple excel for iterable
     * @param <T>
     * @param writableWorkbook
     * @param startRow
     * @param headerArray
     * @param iterable
     * @param transform
    </T> */
    @Throws(Exception::class)
    fun <T, R> writeSimpleExcelForIterable(writableWorkbook: WritableWorkbook, startRow: Int = 0, headerArray: Array<String> = emptyArray(), iterable: Iterable<Iterable<T>>, transform: (column: Int, row: Int, value: T) -> R) {
        writeSimpleExcel(writableWorkbook, startRow, headerArray) { writableSheet, currentRow ->
            var row = currentRow
            for (innerIterable in iterable) {
                innerIterable.forEachIndexed { index, value ->
                    val transformValue = transform(index, row, value)
                    val writableCell = DEFAULT_JXL_PROCESSOR.writeProcess(index, row, Constants.String.BLANK, transformValue)
                    writableSheet.addCell(writableCell)
                }
                row++
            }
        }
    }

    /**
     * write simple excel
     * @param <T>
     * @param headerArray
     * @param iterable
     * @param jxlMappingBean
     * @param fullFilename
    </T> */
    @Throws(Exception::class)
    fun <T : Any> writeSimpleExcel(headerArray: Array<String> = emptyArray(), iterable: Iterable<T>, jxlMappingBean: JxlMappingBean?, fullFilename: String, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR) {
        val newHeaders = if (headerArray.isEmpty()) {
            val jxlMappingColumnBeanList = jxlMappingBean!!.jxlMappingColumnBeanList
            val headerList = arrayListOf<String>()
            for (jxlMappingColumnBean in jxlMappingColumnBeanList) {
                headerList[jxlMappingColumnBean.index] = jxlMappingColumnBean.header
            }
            headerList
        } else {
            headerArray.toList()
        }
        if (jxlMappingBean == null) {
            return
        }
        val writableWorkbook = Workbook.createWorkbook(File(fullFilename))
        writeSimpleExcel(writableWorkbook, headerArray = newHeaders.toTypedArray()) { sheet, currentRow ->
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

    /**
     * get or create workbook
     */
    fun getOrCreateWorkbook(file: File): WritableWorkbook {
        return if (!file.exists()) {
            file.create()
            Workbook.createWorkbook(file)
        } else {
            val workbook = Workbook.getWorkbook(file)
            val writableWorkbook = Workbook.createWorkbook(file, workbook)
            workbook.close()
            writableWorkbook
        }
    }

    /**
     * get or create sheet
     */
    private fun getOrCreateSheet(writableWorkbook: WritableWorkbook, sheetName: String, index: Int = 0): WritableSheet {
        val sheet = if (sheetName.isNotBlank()) {
            writableWorkbook.getSheet(sheetName)
        } else {
            writableWorkbook.getSheet(index)
        }
        return sheet ?: writableWorkbook.createSheet(sheetName, index)
    }

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
