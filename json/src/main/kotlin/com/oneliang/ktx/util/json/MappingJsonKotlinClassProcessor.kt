package com.oneliang.ktx.util.json

import kotlin.reflect.KClass

open class MappingJsonKotlinClassProcessor : DefaultJsonKotlinClassProcessor() {

    enum class Type {
        OBJECT, ARRAY
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any, SP> changeClassProcess(kClass: KClass<T>, values: Array<String>, fieldName: String, specialParameter: SP?): Any? {
        return if (specialParameter != null && specialParameter is Map<*, *>) {
            try {
                val fieldNameKClassMapping = specialParameter as Map<String, Pair<Type, KClass<*>>>
                if (fieldNameKClassMapping.containsKey(fieldName)) {
                    val (type, fieldNameKClass) = fieldNameKClassMapping[fieldName]!!
                    when (type) {
                        Type.OBJECT -> values[0].jsonToObject(fieldNameKClass)
                        Type.ARRAY -> values[0].jsonToObjectList(fieldNameKClass, this)
                    }
                } else {
                    super.changeClassProcess(kClass, values, fieldName, specialParameter)
                }
            } catch (e: Throwable) {
                super.changeClassProcess(kClass, values, fieldName, specialParameter)
            }
        } else {
            super.changeClassProcess(kClass, values, fieldName, specialParameter)
        }
    }
}