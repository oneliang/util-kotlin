package com.oneliang.ktx.util.common

import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass

object MappableUtil {

    /**
     * is mappable object
     * @param instance
     * @return Boolean
     */
    fun <T : Any> isMappableObject(instance: T): Boolean {
        return instance::class.java.isAnnotationPresent(Mappable::class.java)
    }

    /**
     * push to mappable object, push some value to mappable object
     * @param instance
     * @param mappableKeyVisitor
     */
    fun <T : Any> pushToMappableObject(instance: T, mappableKeyVisitor: (mappableKey: String, fieldType: KClass<*>) -> Any?) {
        if (!isMappableObject(instance)) {
            throw MappableUtilException("The instance must be a mappable object")
        }
        val queue = ConcurrentLinkedQueue<Class<*>>()
        queue.add(instance::class.java)
        while (queue.isNotEmpty()) {
            val clazz = queue.poll()
            val declaredFields = clazz.declaredFields
            for (declaredField in declaredFields) {
                if (declaredField.isAnnotationPresent(Mappable.Key::class.java)) {
                    val fieldType = declaredField.type.kotlin
                    val mappableKey = declaredField.getAnnotation(Mappable.Key::class.java).value
                    val mappableValue = mappableKeyVisitor(mappableKey, fieldType) ?: continue//when null keep the instance value, no need to set
                    declaredField.isAccessible = true
                    declaredField.set(instance, mappableValue)
                }
            }
            val superClass = clazz.superclass
            if (superClass != null && superClass != Object::class.java) {
                queue.add(superClass)
            }
        }

    }

    /**
     * pull from mappable object, pull some value from mappable object
     * @param instance
     * @param mappableKeyVisitor
     */
    fun <T : Any> pullFromMappableObject(instance: T, mappableKeyVisitor: (mappableKey: String, fieldValue: Any?) -> Unit) {
        if (!isMappableObject(instance)) {
            throw MappableUtilException("The instance must be a mappable object")
        }
        val queue = ConcurrentLinkedQueue<Class<*>>()
        queue.add(instance::class.java)
        while (queue.isNotEmpty()) {
            val clazz = queue.poll()
            val declaredFields = clazz.declaredFields
            for (declaredField in declaredFields) {
                if (declaredField.isAnnotationPresent(Mappable.Key::class.java)) {
                    val mappableKey = declaredField.getAnnotation(Mappable.Key::class.java).value
                    declaredField.isAccessible = true
                    val fieldValue = declaredField.get(instance)
                    mappableKeyVisitor(mappableKey, fieldValue)
                }
            }
            val superClass = clazz.superclass
            if (superClass != null && superClass != Object::class.java) {
                queue.add(superClass)
            }
        }
    }

    class MappableUtilException : RuntimeException {
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
        constructor(message: String) : super(message)
    }
}