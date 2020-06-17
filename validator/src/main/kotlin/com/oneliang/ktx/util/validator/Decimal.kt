package com.oneliang.ktx.util.validator

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Decimal(val min: Double = 0.0, val max: Double = 0.0, val nullable: Boolean = true)