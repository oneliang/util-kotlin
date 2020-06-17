package com.oneliang.ktx.util.validator

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Regex(vararg val value: String, val nullable: Boolean = true) {
    companion object {
        const val POSITIVE_INTEGER = "^[1-9]\\d*$"
        const val NEGATIVE_INTEGER = "^-[1-9]\\d*$"
        const val INTEGER_NOT_INCLUDE_ZERO = "^-?[1-9]\\d*$"
        const val POSITIVE_INTEGER_INCLUDE_ZERO = "^([1-9]\\d*|0)$"
        const val NEGATIVE_INTEGER_INCLUDE_ZERO = "^(-[1-9]\\d*|0)$"
        const val POSITIVE_DECIMAL = "^([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$"
        const val NEGATIVE_DECIMAL = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$"
        const val DECIMAL_NOT_INCLUDE_ZERO = "^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$"
        const val POSITIVE_DECIMAL_INCLUDE_ZERO = "^([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$"
        const val NEGATIVE_DECIMAL_INCLUDE_ZERO = "^((-([1-9]\\d*.\\d*)|(0.\\d*[1-9]\\d*))|0?.0+|0)$"
    }
}