package com.oneliang.ktx.util.math.stat

fun <K , V > Map<K, V>.stat(functionString: String, statKeyTransform: (key: String) -> K) = Stater.stat(this, functionString, statKeyTransform)

fun <V > Map<String, V>.stat(functionString: String) = this.stat(functionString) { key: String -> key }

fun <K , V > Map<K, V>.stat(statKeyArray: Array<Stater.StatKey>, statKeyTransform: (key: String) -> K) = Stater.stat(this, statKeyArray, statKeyTransform)

fun <V > Map<String, V>.stat(statKeyArray: Array<Stater.StatKey>) = this.stat(statKeyArray) { key: String -> key }

fun <K , V > Iterable<Map<K, V>>.stat(statKeyArray: Array<Stater.StatKey>, statKeyTransform: (key: String) -> K) = Stater.stat(this, statKeyArray, statKeyTransform)

fun <V > Iterable<Map<String, V>>.stat(statKeyArray: Array<Stater.StatKey>) = this.stat(statKeyArray) { key: String -> key }
