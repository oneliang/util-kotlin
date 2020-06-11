package com.oneliang.ktx.util.math

fun <K : Any, V : Any> Map<K, V>.stat(functionString: String, statKeyTransform: (key: String) -> K) = Stater.stat(this, functionString, statKeyTransform)

fun <V : Any> Map<String, V>.stat(functionString: String) = this.stat(functionString) { key: String -> key }
