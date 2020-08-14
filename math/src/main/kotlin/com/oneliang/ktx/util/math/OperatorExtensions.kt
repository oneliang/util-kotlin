package com.oneliang.ktx.util.math

fun <K : Any, V : Any, R : Any> Map<K, V>.operate(operateString: String, operateKeyTransform: (key: String) -> K, operateValueTransform: (value: V) -> R) = Operator.operate(this, operateString, operateKeyTransform, operateValueTransform)

fun <V : Any> Map<String, V>.operate(operateString: String) = this.operate(operateString, { key: String -> key }) { it }
