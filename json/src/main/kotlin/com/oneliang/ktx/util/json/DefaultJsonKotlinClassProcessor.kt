package com.oneliang.ktx.util.json

import com.oneliang.ktx.util.common.DefaultKotlinClassProcessor
import com.oneliang.ktx.util.common.KotlinClassUtil
import com.oneliang.ktx.util.common.toArray
import kotlin.reflect.KClass

open class DefaultJsonKotlinClassProcessor : DefaultKotlinClassProcessor() {
    override fun <T : Any, SP> changeClassProcess(kClass: KClass<T>, values: Array<String>, fieldName: String, specialParameter: SP?): Any? {
        val classType = KotlinClassUtil.getClassType(kClass)
        return if (classType != null) {
            super.changeClassProcess(kClass, values, fieldName, specialParameter)
        } else {
            if (values.isNotEmpty()) {
                if (kClass.java.isArray) {
                    val arrayComponentKClass = kClass.java.componentType.kotlin
                    val objectList = JsonUtil.jsonToObjectList(values[0], arrayComponentKClass, this)
                    objectList.toArray(arrayComponentKClass)
                } else {
                    JsonUtil.jsonToObject(values[0], kClass, this)
                }
            } else {
                null
            }
        }
    }
}