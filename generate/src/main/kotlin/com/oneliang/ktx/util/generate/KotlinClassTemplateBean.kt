package com.oneliang.ktx.util.generate

import com.oneliang.ktx.Constants

class KotlinClassTemplateBean {

    companion object {
        const val TAG_KOTLIN_CLASS = "kotlin-class"
        const val ATTRIBUTE_KOTLIN_CLASS_PACKAGE_NAME = "packageName"
        const val ATTRIBUTE_KOTLIN_CLASS_CLASS_NAME = "className"
        const val ATTRIBUTE_KOTLIN_CLASS_SUPER_CLASS_NAMES = "superClassNames"

        //import
        const val TAG_KOTLIN_CLASS_IMPORT = "import"
        const val ATTRIBUTE_KOTLIN_CLASS_IMPORT_VALUE = "value"

        //field
        const val TAG_KOTLIN_CLASS_FIELD = "field"
        const val ATTRIBUTE_KOTLIN_CLASS_FIELD_OVERRIDE = "override"
        const val ATTRIBUTE_KOTLIN_CLASS_FIELD_LATEINIT = "lateinit"
        const val ATTRIBUTE_KOTLIN_CLASS_FIELD_NAME = "name"
        const val ATTRIBUTE_KOTLIN_CLASS_FIELD_TYPE = "type"
        const val ATTRIBUTE_KOTLIN_CLASS_FIELD_NULLABLE = "nullable"
        const val ATTRIBUTE_KOTLIN_CLASS_FIELD_DEFAULT_VALUE = "defaultValue"

        //codeInClass
        const val TAG_KOTLIN_CLASS_CODE_IN_CLASS = "codeInClass"
    }

    enum class ClassType(val value: String) {
        INTERFACE("INTERFACE"), OPEN_CLASS("OPEN_CLASS"), CLASS("CLASS")
    }

    var packageName = Constants.String.BLANK
    var importArray = emptyArray<String>()
    var className = Constants.String.BLANK
    var superClassNames = Constants.String.BLANK
    var classType = ClassType.CLASS.value

    var fieldArray = emptyArray<Field>()

    var codeInClassArray = emptyArray<String>()

    class Field {
        var override: Boolean = false
        var lateinit: Boolean = false
        var name: String = Constants.String.BLANK
        var type: String = Constants.String.BLANK
        var nullable: Boolean = false//in super class
        var defaultValue: String = Constants.String.BLANK
    }
}