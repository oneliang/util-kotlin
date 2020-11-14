package com.oneliang.ktx.util.reflect

import java.lang.reflect.Field

class ReflectField<C, FieldType>(clazz: Class<*>, fieldName: String, modified: Int) : ThrowableChain() {
    /**
     * @return the field
     */
    private var field: Field? = null

    init {
        try {
            val field = clazz.getDeclaredField(fieldName)
            this.field = field
            if (modified > 0 && field.modifiers and modified != modified) {
                addThrowable(ReflectException("$field does not match modifiers: $modified"))
            }
            field.isAccessible = true
        } catch (e: Exception) {
            addThrowable(e)
        }
    }

    /**
     * get
     * @param instance
     * @return T
     * @throws ReflectException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(ReflectException::class)
    operator fun get(instance: C): FieldType? {
        val value: FieldType? = null
        val field = this.field
        return if (field != null) {
            try {
                field.get(instance) as FieldType?
            } catch (e: Exception) {
                throw ReflectException(e)
            }
        } else value
    }

    /**
     * set
     * @param instance
     * @param value
     * @throws ReflectException
     */
    @Throws(ReflectException::class)
    operator fun set(instance: C, value: FieldType) {
        try {
            val field = this.field
            if (field != null) {
                field[instance] = value
            }
        } catch (e: Exception) {
            throw ReflectException(e)
        }
    }
}