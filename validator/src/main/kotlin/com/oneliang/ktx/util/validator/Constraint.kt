package com.oneliang.ktx.util.validator

import kotlin.reflect.KClass

interface ConstraintValidator<FIELD_TYPE : Any> {
    fun validate(fieldName: String, fieldValue: FIELD_TYPE): Validator.ViolateConstraint?
}

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Constraint<FIELD_TYPE : Any, T : ConstraintValidator<FIELD_TYPE>>(val validatedBy: KClass<T>, val nullable: Boolean = true)

