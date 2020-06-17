package com.oneliang.ktx.util.validator

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Length(val min: Int = Int.MIN_VALUE, val max: Int = Int.MAX_VALUE, val nullable: Boolean = true)