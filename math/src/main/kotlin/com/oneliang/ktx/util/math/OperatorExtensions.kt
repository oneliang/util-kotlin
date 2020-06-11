package com.oneliang.ktx.util.math

fun <K : Any, V : Any> Map<K, V>.operate(operateString: String, operateKeyTransform: (key: String) -> K) = Operator.operate(this, operateString, operateKeyTransform)

fun <V : Any> Map<String, V>.operate(operateString: String) = this.operate(operateString) { key: String -> key }
