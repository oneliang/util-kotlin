package com.oneliang.ktx.util.jxl

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.ObjectUtil
import com.oneliang.ktx.util.common.nullToBlank
import com.oneliang.ktx.util.common.toMapWithIndex
import jxl.Cell
import jxl.Workbook
import jxl.write.Label
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
    fun <T : Any> copyExcel(excelFile: String, newExcelFile: String, instance: T, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR) {
        try {
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
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * import simple excel
     * @param <T>
     * @param fullFilename
     * @param kClass
     * @param jxlMappingBean
     * @param dataRowOffset
     * @param jxlProcessor
     * @return List<T>
    </T></T> */
    fun <T : Any> importSimpleExcel(fullFilename: String, kClass: KClass<T>, jxlMappingBean: JxlMappingBean, headerRowIndex: Int = -1, dataRowOffset: Int = 0, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR): List<T> {
        val list = mutableListOf<T>()
        try {
            val workbook = Workbook.getWorkbook(File(fullFilename))
            val sheets = workbook.sheets
            val sheet = (if (sheets.isNotEmpty()) sheets[0] else null) ?: return list
            val rows = sheet.rows
            if (rows < headerRowIndex + 1) {
                return list
            }

            //find header maybe has no header
            var headerIndexMap = emptyMap<String, Int>()
            if (headerRowIndex < 0) {
                val headerCellArray = sheet.getRow(headerRowIndex) ?: emptyArray()
                headerIndexMap = headerCellArray.toMapWithIndex { index, t ->
                    t.contents.nullToBlank() to index
                }
            }
            val existHeader = headerIndexMap.isNotEmpty()
            for (i in dataRowOffset until rows) {
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
                            val cell = sheet.getCell(columnIndex, i)
                            val classes = method.parameterTypes
                            if (classes.size == 1) {
                                val value = jxlProcessor.importProcess<Any>(classes[0].kotlin, cell)
                                method.invoke(instance, value)
                            }
                        }
                    }
                }
                list.add(instance)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        return list
    }

    /**
     * export simple excel
     * @param <T>
     * @param headers
     * @param iterable
     * @param jxlMappingBean
     * @param file
    </T> */
    fun <T : Any> exportSimpleExcel(headers: Array<String> = emptyArray(), iterable: Iterable<T>, jxlMappingBean: JxlMappingBean?, file: String, jxlProcessor: JxlProcessor = DEFAULT_JXL_PROCESSOR) {
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
        try {
            val writableWorkbook = Workbook.createWorkbook(File(file))
            val sheet = writableWorkbook.createSheet("sheet", 0)
            var row = 0
            if (newHeaders.isNotEmpty()) {
                for ((column, header) in newHeaders.withIndex()) {
                    val cell = Label(column, row, header)
                    sheet.addCell(cell)
                }
                row++
            }
            if (jxlMappingBean == null) {
                return
            }
            val jxlMappingColumnBeanList = jxlMappingBean.jxlMappingColumnBeanList
            var currentRow = row
            for (instance in iterable) {
                for (jxlMappingColumnBean in jxlMappingColumnBeanList) {
                    val fieldName = jxlMappingColumnBean.field
                    val columnIndex = jxlMappingColumnBean.index
                    var methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName)
                    methodReturnValue = jxlProcessor.exportProcess<Any>(fieldName, methodReturnValue)
                    val cell = Label(columnIndex, currentRow, methodReturnValue.toString())
                    sheet.addCell(cell)
                }
                currentRow++
            }
            writableWorkbook.write()
            writableWorkbook.close()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
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
         * import process
         * @param <T>
         * @param parameterClass
         * @param cell
         * @return Object
        </T> */
        fun <T : Any> importProcess(parameterClass: KClass<*>, cell: Cell): Any

        /**
         * export process
         * @param <T>
         * @param fieldName
         * @param value
         * @return String
        </T> */
        fun <T : Any> exportProcess(fieldName: String, value: Any?): String
    }
}
