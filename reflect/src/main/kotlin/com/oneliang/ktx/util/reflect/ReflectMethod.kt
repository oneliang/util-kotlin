package com.oneliang.ktx.util.reflect

import java.lang.reflect.Method

class ReflectMethod<C, ReturnType>(clazz: Class<*>, methodName: String, methodParameterType: Array<Class<*>>, modifier: Int) : ThrowableChain() {

    private var method: Method? = null

    init {
        try {
            val method = clazz.getDeclaredMethod(methodName, *methodParameterType)
            this.method = method
            if (modifier > 0 && method.modifiers and modifier != modifier) {
                addThrowable(ReflectException("$method does not match modifiers: $modifier"))
            }
            method.isAccessible = true
        } catch (e: Exception) {
            addThrowable(ReflectException(e))
        }
    }

    /**
     * invoke
     * @param instance
     * @param parameters
     * @return ReturnType
     * @throws ReflectException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(ReflectException::class)
    operator fun invoke(instance: C, parameters: Array<Any?>): ReturnType? {
        return try {
            method?.invoke(instance, *parameters) as ReturnType?
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }
}