package com.oneliang.ktx.util.reflect

import java.lang.reflect.Modifier

class ReflectClass<C> : ThrowableChain {
    private lateinit var clazz: Class<C>

    /**
     * constructor
     * @param clazz
     */
    constructor(clazz: Class<C>) {
        this.clazz = clazz
    }

    /**
     * constructor
     * @param className
     */
    @Suppress("UNCHECKED_CAST")
    constructor(className: String) {
        try {
            clazz = Class.forName(className) as Class<C>
        } catch (e: ClassNotFoundException) {
            addThrowable(e)
        }
    }

    /**
     * get static field
     * @param <FieldClass>
     * @param fieldName
     * @return ReflectField<C></C>,FieldClass>
    </FieldClass> */
    fun <FieldClass> getStaticField(fieldName: String): ReflectField<C, FieldClass> {
        return getField(fieldName, Modifier.STATIC)
    }

    /**
     * get declared field
     * @param <FieldClass>
     * @param fieldName
     * @return ReflectField<C></C>,FieldClass>
    </FieldClass> */
    fun <FieldClass> getDeclaredField(fieldName: String): ReflectField<C, FieldClass> {
        return getField(fieldName, 0)
    }

    /**
     * get field
     * @param <FieldClass>
     * @param fieldName
     * @return ReflectField<C></C>,FieldClass>
    </FieldClass> */
    private fun <FieldClass> getField(fieldName: String, modifier: Int): ReflectField<C, FieldClass> {
        return ReflectField(this.clazz, fieldName, modifier)
    }

    /**
     * get static method
     * @param <ReturnType>
     * @param methodName
     * @param methodParameterType
     * @return ReflectMethod<C></C>,ReturnType>
    </ReturnType> */
    fun <ReturnType> getStaticMethod(methodName: String, methodParameterType: Array<Class<*>>): ReflectMethod<C, ReturnType> {
        return getMethod(methodName, methodParameterType, Modifier.STATIC)
    }

    /**
     * get declared method
     * @param <ReturnType>
     * @param methodName
     * @param methodParameterType
     * @return ReflectMethod<C></C>,ReturnType>
    </ReturnType> */
    fun <ReturnType> getDeclaredMethod(methodName: String, methodParameterType: Array<Class<*>>): ReflectMethod<C, ReturnType> {
        return getMethod(methodName, methodParameterType, 0)
    }

    /**
     * get method
     * @param <ReturnType>
     * @param methodName
     * @param methodParameterType
     * @param modifier
     * @return ReflectMethod<C></C>,ReturnType>
    </ReturnType> */
    private fun <ReturnType> getMethod(methodName: String, methodParameterType: Array<Class<*>>, modifier: Int): ReflectMethod<C, ReturnType> {
        return ReflectMethod(clazz, methodName, methodParameterType, modifier)
    }

    fun getClazz(): Class<C> {
        return this.clazz
    }
}