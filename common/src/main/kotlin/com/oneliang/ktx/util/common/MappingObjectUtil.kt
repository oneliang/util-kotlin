package com.oneliang.ktx.util.common

import kotlin.reflect.KClass

object MappingObjectUtil {

    /**
     * is mapping object
     * @param instance
     */
    fun <T : Any> isMappingObject(instance: T): Boolean {
        return instance::class.java.isAnnotationPresent(MappingObject::class.java)
    }

    /**
     * initialize mapping object
     * @param instance
     * @param mappingKeyVisitor
     */
    fun <T : Any> initializeMappingObject(instance: T, mappingKeyVisitor: (mappingKey: String, fieldType: KClass<*>) -> Any?) {
        if (!isMappingObject(instance)) {
            throw MappingObjectUtilException("The instance must be a mapping object")
        }
        val declaredFields = instance::class.java.declaredFields
        for (declaredField in declaredFields) {
            if (declaredField.isAnnotationPresent(MappingObject.Key::class.java)) {
                val fieldType = declaredField.type.kotlin
                val mappingKey = declaredField.getAnnotation(MappingObject.Key::class.java).value
                val mappingValue = mappingKeyVisitor(mappingKey, fieldType) ?: continue//when null keep the instance value, no need to set
                declaredField.isAccessible = true
                declaredField.set(instance, mappingValue)
            }
        }
    }

    /**
     * operate mapping object
     * @param instance
     * @param mappingKeyVisitor
     */
    fun <T : Any> operateMappingObject(instance: T, mappingKeyVisitor: (mappingKey: String, fieldValue: Any?) -> Unit) {
        if (!isMappingObject(instance)) {
            throw MappingObjectUtilException("The instance must be a mapping object")
        }
        val declaredFields = instance::class.java.declaredFields
        for (declaredField in declaredFields) {
            if (declaredField.isAnnotationPresent(MappingObject.Key::class.java)) {
                val mappingKey = declaredField.getAnnotation(MappingObject.Key::class.java).value
                declaredField.isAccessible = true
                val fieldValue = declaredField.get(instance)
                mappingKeyVisitor(mappingKey, fieldValue)
            }
        }
    }

    class MappingObjectUtilException : RuntimeException {
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
        constructor(message: String) : super(message)
    }
}