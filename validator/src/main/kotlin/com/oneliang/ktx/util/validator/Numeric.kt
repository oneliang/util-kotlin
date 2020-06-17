package com.oneliang.ktx.util.validator

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Numeric(val min: Long = Long.MIN_VALUE, val max: Long = Long.MAX_VALUE, val nullable: Boolean = true)