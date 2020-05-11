package com.oneliang.ktx.util.jxl

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.KotlinClassUtil
import com.oneliang.ktx.util.common.ObjectUtil
import com.oneliang.ktx.util.common.parseRegexGroup
import com.oneliang.ktx.util.common.toFormatString
import jxl.Cell
import jxl.CellType
import jxl.write.Label
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
    override fun <T : Any> copyingProcess(cell: Cell, instance: T) {
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
     * importing process
     * @param <T>
     * @param parameterClass
     * @param cell
     * @return Object
    </T> */
    override fun <T : Any> importingProcess(parameterClass: KClass<*>, cell: Cell): Any {
        val cellValue = cell.contents
        return KotlinClassUtil.changeType(parameterClass, arrayOf(cellValue)) ?: Any()
    }

    /**
     * exporting process
     * @param <T>
     * @param clazz
     * @param value
     * @param fieldName
     * @return String
    </T> */
    override fun <T : Any> exportingProcess(fieldName: String, value: Any?): String {
        return if (value == null) {
            Constants.String.BLANK
        } else {
            val clazz = value::class
            if (clazz == Boolean::class || clazz == Short::class
                    || clazz == Int::class || clazz == Long::class
                    || clazz == Float::class || clazz == Double::class
                    || clazz == Byte::class) {
                value.toString()
            } else if (clazz == String::class || clazz == Char::class) {
                value.toString()
            } else if (clazz == Date::class.java) {
                (value as Date).toFormatString()
            } else {
                value.toString()
            }
        }
    }
}
