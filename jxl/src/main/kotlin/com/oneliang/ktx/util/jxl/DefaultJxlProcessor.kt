package com.oneliang.ktx.util.jxl

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.*
import jxl.Cell
import jxl.CellType
import jxl.biff.EmptyCell
import jxl.write.Label
import jxl.write.Number
import jxl.write.WritableCell
import java.util.*
import kotlin.reflect.KClass

class DefaultJxlProcessor : JxlUtil.JxlProcessor {

    companion object {
        private const val REGEX = "\\{([\\w\\.]*)\\}"
    }

    /**
     * copying process
     * @param <T>
     * @param cell
     * @param instance
    </T> */
    override fun <T : Any> copyProcess(cell: Cell, instance: T) {
        val cellType = cell.type
        if (cellType != CellType.LABEL) {
            return
        }
        val label = cell as Label
        var value = label.string
        val list = value.parseRegexGroup(REGEX)
        for (string in list) {
            val pos = string.lastIndexOf(Constants.Symbol.DOT)
            if (pos > 0) {
                val className = string.substring(0, pos)
                val fieldName = string.substring(pos + 1, string.length)
                if (className == instance.javaClass.simpleName || className == instance.javaClass.name) {
                    val fieldValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName)
                    value = value.replaceFirst(REGEX.toRegex(), fieldValue.toString())
                }
            }
        }
        label.string = value
    }

    /**
     * import process
     * @param <T>
     * @param parameterClass
     * @param cell
     * @return Object
    </T> */
    override fun <T : Any> readProcess(parameterClass: KClass<T>, cell: Cell): Any {
        val cellValue = cell.contents
        return KotlinClassUtil.changeType(parameterClass, arrayOf(cellValue)) ?: Any()
    }

    /**
     * export process
     * @param column
     * @param row
     * @param value
     * @param fieldName
     * @return String
    </T> */
    override fun writeProcess(column: Int, row: Int, fieldName: String, value: Any?): WritableCell {
        return if (value == null) {
            EmptyCell(column, row)
        } else {
            val kClass = value::class
            if (kClass == Boolean::class) {
                jxl.write.Boolean(column, row, (value as Boolean))
            } else if (kClass == Short::class
                    || kClass == Int::class || kClass == Long::class
                    || kClass == Float::class || kClass == Double::class
                    || kClass == Byte::class) {
                Number(column, row, value.toString().toDoubleSafely())
            } else if (kClass == String::class || kClass == Char::class) {
                Label(column, row, value.toString())
            } else if (kClass == Date::class) {
                Label(column, row, (value as Date).toFormatString())
            } else {
                Label(column, row, value.toString())
            }
        }
    }
}
