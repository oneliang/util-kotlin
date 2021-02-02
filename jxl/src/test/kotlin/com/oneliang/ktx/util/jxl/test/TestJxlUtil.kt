package com.oneliang.ktx.util.jxl.test

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.toArray
import com.oneliang.ktx.util.jxl.JxlUtil
import com.oneliang.ktx.util.jxl.readSimpleExcel
import com.oneliang.ktx.util.jxl.writeOrUpdateSimpleExcelForArray
import com.oneliang.ktx.util.jxl.writeSimpleExcel
import jxl.Workbook
import java.io.File

fun main() {
    val fullFilename = "/D:/Dandelion/java/githubWorkspace/util-kotlin/jxl/src/test/kotlin/test.xls"
    val writeFullFilename = "/D:/Dandelion/java/githubWorkspace/util-kotlin/jxl/src/test/kotlin/test_write.xls"
    val readResult = File(fullFilename).readSimpleExcel(headerRowIndex = 0, dataRowOffset = 1)
    val headers = arrayOf("列1", "列2", "列3", "列4", "列5", "列6", "列7")
    val mapping = mapOf(
        "列1" to 0,
        "列2" to 1,
        "列3" to 2,
        "列4" to 3,
        "列5" to 4,
        "列6" to 5,
        "列7" to 6
    )
    val newList = mutableListOf<Array<String>>()
    readResult.dataList.forEachIndexed { row, data ->
        newList += data.toArray(mapping, Constants.String.BLANK)
        data.forEach { (key, value) ->
            println("$row:$key:$value")
        }
    }
//    File(writeFullFilename).writeSimpleExcel(headerArray = headers, iterable = newList)
    File(writeFullFilename).writeOrUpdateSimpleExcelForArray(
        arrayOf(
            JxlUtil.WriteOptionForArray(
                sheetName = "a",
                sheetIndex = 0,
                headerArray = headers,
                iterable = newList
            ),
            JxlUtil.WriteOptionForArray(
                sheetName = "b",
                sheetIndex = 1,
                headerArray = headers,
                iterable = newList
            )
        )
    )
}