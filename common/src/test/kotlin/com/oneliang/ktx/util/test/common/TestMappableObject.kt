package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.Mappable
import com.oneliang.ktx.util.common.pushToMappableObject
import com.oneliang.ktx.util.common.pullFromMappableObject

@Mappable
class TestMappableObject {

    @Mappable.Key("number_field")
    var numberField: Int = 0

    @Mappable.Key("string_field")
    var stringField: String = Constants.String.BLANK
}

fun main() {
    val testMappableObject = TestMappableObject()
    testMappableObject.pushToMappableObject { mappableKey, fieldType ->
        when (mappableKey) {
            "number_field" -> {
                return@pushToMappableObject 12
            }

            "string_field" -> {
                return@pushToMappableObject "string_field"
            }

            else -> {
                return@pushToMappableObject null
            }
        }
    }
    testMappableObject.pullFromMappableObject { mappableKey, fieldValue ->
        println("mapping key:%s, field value:%s".format(mappableKey, fieldValue))
    }
}