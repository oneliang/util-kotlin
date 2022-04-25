package com.oneliang.ktx.util.generate

import com.oneliang.ktx.Constants
import com.oneliang.ktx.pojo.KeyValue
import com.oneliang.ktx.util.common.nullToBlank
import com.oneliang.ktx.util.common.toFile
import com.oneliang.ktx.util.file.readContentIgnoreLine
import java.io.FileNotFoundException

class BeanDescription {
    companion object {
        const val BEGIN = "begin:"
        private const val PACKAGE_NAME = "packageName:"
        private const val IMPORTS = "imports:"
        private const val CLASS_NAME = "className:"
        private const val FIELDS = "fields:"
        internal const val TEMPLATE_FIELD_TYPE_CLASS = "CLASS"
        internal const val FLAG_PACKAGE_NAME = 1 shl 0
        internal const val FLAG_IMPORTS = 1 shl 1
        internal const val FLAG_CLASS_NAME = 1 shl 2
        internal const val FLAG_FIELDS = 1 shl 5
        internal const val FLAG_FIELDS_SUB_CLASS_1 = 1 shl 6
        internal const val FLAG_FIELDS_SUB_CLASS_2 = 1 shl 7
        internal val keywordMap = mapOf(
            PACKAGE_NAME to FLAG_PACKAGE_NAME,
            IMPORTS to FLAG_IMPORTS,
            CLASS_NAME to FLAG_CLASS_NAME,
            FIELDS to FLAG_FIELDS
        )
    }

    var packageName = Constants.String.BLANK
    var imports = emptyArray<String>()
    var className = Constants.String.BLANK
    var fields = emptyArray<FieldDescription>()

    class FieldDescription(
        name: String = Constants.String.BLANK,
        type: String = Constants.String.BLANK,
        var description: String = Constants.String.BLANK
    ) : KeyValue(name, type) {
        var subFields = emptyArray<FieldDescription>()
    }
}

fun BeanDescription.Companion.parseField(line: String): BeanDescription.FieldDescription {
    val field = line.split(Constants.String.SPACE)
    val description = line.substring(field[0].length + Constants.String.SPACE.length + field[1].length).trim()
    return BeanDescription.FieldDescription(field[0], field[1], description)
}

fun BeanDescription.Companion.processSubClass(
    line: String,
    currentFlag: Int,
    flagSubClassKeyMap: MutableMap<Int, String>,
    fieldList: MutableList<BeanDescription.FieldDescription>,
    subClass1: Int,
    subClass2: Int
): Pair<Int, Array<BeanDescription.FieldDescription>?> {
    val spaceIndex = line.indexOf(Constants.String.SPACE)
    val colonIndex = line.lastIndexOf(Constants.Symbol.COLON)
    //space priority higher than colon
    if (spaceIndex < 0 && colonIndex > 0 || colonIndex in 1 until spaceIndex) {//no space but has colon or colon index less than space, has array
        val fieldKey = line.substring(0, colonIndex)
        if (currentFlag and subClass1 == subClass1) {//sub class flag has open, reset sub class flag
            if (fieldKey == flagSubClassKeyMap[subClass1].nullToBlank()) {//the same key, so finished sub class 1
                return currentFlag and subClass1.inv() to null//remove sub class 1 flag
            } else {//2
                if (fieldKey == flagSubClassKeyMap[subClass2].nullToBlank()) {
                    return currentFlag and subClass2.inv() to null//remove sub class 2 flag
                } else {
                    flagSubClassKeyMap[subClass2] = fieldKey
                    val description = line.substring(colonIndex + 1)
                    val lastField = fieldList.last()
                    val subFieldList = lastField.subFields.toMutableList()
                    subFieldList += BeanDescription.FieldDescription(fieldKey, TEMPLATE_FIELD_TYPE_CLASS, description)
                    lastField.subFields = subFieldList.toTypedArray()
                    return currentFlag or subClass2 to null
                }
            }
        } else {//first in sub class
            flagSubClassKeyMap[subClass1] = fieldKey
            val description = line.substring(colonIndex + 1)
            fieldList += BeanDescription.FieldDescription(fieldKey, TEMPLATE_FIELD_TYPE_CLASS, description)
            return currentFlag or subClass1 to fieldList.toTypedArray()
        }
    } else {
        if (currentFlag and subClass2 == subClass2) {//use sub class 2
            if (fieldList.isNotEmpty()) {
                val lastField = fieldList.last()
                val subFieldList = lastField.subFields.toMutableList()
                val lastSubField = subFieldList.last()
                val subSubFieldList = lastSubField.subFields.toMutableList()
                subSubFieldList += parseField(line)
                lastSubField.subFields = subSubFieldList.toTypedArray()
                return 0 to null
            } else {
                error("some invoke sequence error, please check it")
            }
        } else if (currentFlag and subClass1 == subClass1) {//use sub class 1
            if (fieldList.isNotEmpty()) {
                val lastField = fieldList.last()
                val subFieldList = lastField.subFields.toMutableList()
                subFieldList += parseField(line)
                lastField.subFields = subFieldList.toTypedArray()
                return 0 to null
            } else {
                error("some invoke sequence error, please check it")
            }
        } else {//has space, space priority higher than colon
            fieldList += parseField(line)
            return 0 to fieldList.toTypedArray()
        }
    }
}

fun BeanDescription.Companion.buildListFromFile(fullFilename: String): List<BeanDescription> {
    val beanDescriptionList = mutableListOf<BeanDescription>()
    var beanDescription: BeanDescription? = null
    val file = fullFilename.toFile()
    if (file.exists() && file.isFile) {
        var currentFlag = 0
        val flagSubClassKeyMap = mutableMapOf<Int, String>()
        file.readContentIgnoreLine {
            val line = it.trim()
            if (line.isBlank() || line.startsWith(Constants.Symbol.POUND_KEY)) {
                return@readContentIgnoreLine true//continue
            }
            when {
                line.startsWith(BEGIN, true) -> {
                    val newBeanDescription = BeanDescription()
                    beanDescriptionList += newBeanDescription
                    beanDescription = newBeanDescription
                }
                else -> {
                    //keyword process
                    var keywordSign = false
                    for (key in keywordMap.keys) {
                        if (line.startsWith(key, true)) {
                            currentFlag = 0//reset
                            currentFlag = currentFlag or keywordMap[key]!!
                            keywordSign = true
                            break
                        }
                    }
                    if (keywordSign) {
                        return@readContentIgnoreLine true
                    }
                    //data process
                    val currentBeanDescription = beanDescription ?: return@readContentIgnoreLine true
                    when {
                        currentFlag and FLAG_PACKAGE_NAME == FLAG_PACKAGE_NAME -> {
                            currentBeanDescription.packageName = line
                        }
                        currentFlag and FLAG_IMPORTS == FLAG_IMPORTS -> {
                            val importHashSet = currentBeanDescription.imports.toHashSet()
                            importHashSet += line
                            currentBeanDescription.imports = importHashSet.toTypedArray()
                        }
                        currentFlag and FLAG_CLASS_NAME == FLAG_CLASS_NAME -> {
                            currentBeanDescription.className = line
                        }
                        currentFlag and FLAG_FIELDS == FLAG_FIELDS -> {
                            val fieldList = currentBeanDescription.fields.toMutableList()
                            val (modifyCurrentFlag, modifiedFieldArray) = processSubClass(
                                line,
                                currentFlag,
                                flagSubClassKeyMap,
                                fieldList,
                                FLAG_FIELDS_SUB_CLASS_1,
                                FLAG_FIELDS_SUB_CLASS_2
                            )
                            if (modifyCurrentFlag != 0) {
                                currentFlag = modifyCurrentFlag
                            }
                            if (modifiedFieldArray != null) {
                                currentBeanDescription.fields = modifiedFieldArray
                            }
                        }
                    }
                }
            }
            true
        }
    } else {
        throw FileNotFoundException("file does not exists or file is a directory, file:%s".format(fullFilename))
    }
    return beanDescriptionList
}
