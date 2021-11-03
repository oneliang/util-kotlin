package com.oneliang.ktx.util.json

import com.oneliang.ktx.util.common.KotlinClassUtil
import kotlin.reflect.KClass

open class MappingJsonKotlinClassProcessor(private val fieldNameKClassMapping: Map<String, Pair<Type, KClass<*>>>) : DefaultJsonKotlinClassProcessor() {

    enum class Type {
        OBJECT, ARRAY
    }

    companion object {
        fun newInstance(fieldNameKClassMap: Map<String, Pair<Type, KClass<*>>>): KotlinClassUtil.KotlinClassProcessor {
            return MappingJsonKotlinClassProcessor(fieldNameKClassMap)
        }
    }

    override fun <T : Any> changeClassProcess(kClass: KClass<T>, values: Array<String>, fieldName: String): Any? {
        return if (fieldNameKClassMapping.containsKey(fieldName)) {
            val (type, fieldNameKClass) = fieldNameKClassMapping[fieldName]!!
            when (type) {
                Type.OBJECT -> values[0].jsonToObject(fieldNameKClass)
                Type.ARRAY -> values[0].jsonToObjectList(fieldNameKClass, this)
            }
        } else {
            super.changeClassProcess(kClass, values, fieldName)
        }
    }
}