package com.oneliang.ktx.util.math.stat

fun <K , V > Map<K, V>.stat(functionString: String, statKeyTransform: (key: String) -> K) = Stater.stat(this, functionString, statKeyTransform)

fun <V > Map<String, V>.stat(functionString: String) = this.stat(functionString) { key: String -> key }

fun <K , V > Map<K, V>.stat(statKeys: Array<Stater.StatKey>, statKeyTransform: (key: String) -> K) = Stater.stat(this, statKeys, statKeyTransform)

fun <V > Map<String, V>.stat(statKeys: Array<Stater.StatKey>) = this.stat(statKeys) { key: String -> key }

fun <K , V > Iterable<Map<K, V>>.stat(statKeys: Array<Stater.StatKey>, statKeyTransform: (key: String) -> K) = Stater.stat(this, statKeys, statKeyTransform)

fun <V > Iterable<Map<String, V>>.stat(statKeys: Array<Stater.StatKey>) = this.stat(statKeys) { key: String -> key }
