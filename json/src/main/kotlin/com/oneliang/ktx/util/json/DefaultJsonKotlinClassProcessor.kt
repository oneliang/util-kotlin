package com.oneliang.ktx.util.json

import com.oneliang.ktx.util.common.DefaultKotlinClassProcessor
import com.oneliang.ktx.util.common.KotlinClassUtil
import com.oneliang.ktx.util.common.toArray
import kotlin.reflect.KClass

open class DefaultJsonKotlinClassProcessor : DefaultKotlinClassProcessor() {

    enum class Type {
        OBJECT, ARRAY
    }

    private fun <T : Any, SP> simplyChangeClassProcess(kClass: KClass<T>, values: Array<String>, fieldName: String, specialParameter: SP?): Any? {
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

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any, SP> changeClassProcess(kClass: KClass<T>, values: Array<String>, fieldName: String, specialParameter: SP?): Any? {
        return if (specialParameter != null && specialParameter is Map<*, *> && specialParameter.isNotEmpty()) {
            try {
                val fieldNameKClassMapping = specialParameter as Map<String, Pair<Type, KClass<*>>>
                if (fieldNameKClassMapping.containsKey(fieldName)) {
                    val (type, fieldNameKClass) = fieldNameKClassMapping[fieldName]!!
                    when (type) {
                        Type.OBJECT -> values[0].jsonToObject(fieldNameKClass, this, specialParameter)
                        Type.ARRAY -> values[0].jsonToObjectList(fieldNameKClass, this, specialParameter)
                    }
                } else {
                    this.simplyChangeClassProcess(kClass, values, fieldName, specialParameter)
                }
            } catch (e: Throwable) {
                this.simplyChangeClassProcess(kClass, values, fieldName, specialParameter)
            }
        } else {
            this.simplyChangeClassProcess(kClass, values, fieldName, specialParameter)
        }
    }
}