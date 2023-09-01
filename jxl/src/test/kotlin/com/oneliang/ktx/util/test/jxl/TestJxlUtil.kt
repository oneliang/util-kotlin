package com.oneliang.ktx.util.test.jxl

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.toArray
import com.oneliang.ktx.util.common.toMapWithIndex
import com.oneliang.ktx.util.jxl.JxlUtil
import com.oneliang.ktx.util.jxl.readSimpleExcel
import com.oneliang.ktx.util.jxl.writeOrUpdateSimpleExcelForArray
import java.io.File

fun main() {
    val fullFilename = "/D:/Dandelion/java/githubWorkspace/util-kotlin/jxl/src/test/kotlin/test.xls"
    val writeFullFilename = "/D:/Dandelion/java/githubWorkspace/util-kotlin/jxl/src/test/kotlin/test_write.xls"
    val readResult = File(fullFilename).readSimpleExcel(headerRowIndex = 0, dataRowOffset = 1)
    val headers = arrayOf("列1", "列2", "列3", "列4", "列5", "列6", "列7")
    val mapping = headers.toMapWithIndex { index, t -> t to index }
    val newList = mutableListOf<Array<String>>()
    readResult.dataList.forEachIndexed { row, data ->
        newList += data.toArray(mapping, Constants.String.BLANK)
        data.forEach { (key, value) ->
            println("$row:$key:$value")
        }
    }
//    File(writeFullFilename).writeSimpleExcel(headers = headers, iterable = newList)
    File(writeFullFilename).writeOrUpdateSimpleExcelForArray(
        arrayOf(
            JxlUtil.WriteOptionForArray(
                sheetName = "a",
                sheetIndex = 0,
                headers = headers,
                iterable = newList
            ),
            JxlUtil.WriteOptionForArray(
                sheetName = "b",
                sheetIndex = 1,
                headers = headers,
                iterable = newList
            )
        )
    )
}