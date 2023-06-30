package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.MappingObject
import com.oneliang.ktx.util.common.initializeMappingObject
import com.oneliang.ktx.util.common.operateMappingObject

@MappingObject
class TestMappingObject {

    @MappingObject.Key("number_field")
    var numberField: Int = 0

    @MappingObject.Key("string_field")
    var stringField: String = Constants.String.BLANK
}

fun main() {
    val testMappingObject = TestMappingObject()
    testMappingObject.initializeMappingObject { mappingKey, fieldType ->
        when (mappingKey) {
            "number_field" -> {
                return@initializeMappingObject 12
            }

            "string_field" -> {
                return@initializeMappingObject "string_field"
            }

            else -> {
                return@initializeMappingObject null
            }
        }
    }
    testMappingObject.operateMappingObject { mappingKey, fieldValue ->
        println("mapping key:%s, field value:%s".format(mappingKey, fieldValue))
    }
}