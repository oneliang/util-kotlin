package com.oneliang.ktx.util.math

fun <K : Any, V : Any> Map<K, V>.stat(functionString: String, statKeyTransform: (key: String) -> K) = Stater.stat(this, functionString, statKeyTransform)

fun <V : Any> Map<String, V>.stat(functionString: String) = this.stat(functionString) { key: String -> key }

fun <K : Any, V : Any> Map<K, V>.stat(statKeyArray: Array<Stater.StatKey>, statKeyTransform: (key: String) -> K) = Stater.stat(this, statKeyArray, statKeyTransform)

fun <V : Any> Map<String, V>.stat(statKeyArray: Array<Stater.StatKey>) = this.stat(statKeyArray) { key: String -> key }

fun <K : Any, V : Any> Iterable<Map<K, V>>.stat(statKeyArray: Array<Stater.StatKey>, statKeyTransform: (key: String) -> K) = Stater.stat(this, statKeyArray, statKeyTransform)

fun <V : Any> Iterable<Map<String, V>>.stat(statKeyArray: Array<Stater.StatKey>) = this.stat(statKeyArray) { key: String -> key }
