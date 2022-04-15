package com.oneliang.ktx.util.json

import com.oneliang.ktx.util.common.KotlinClassUtil
import kotlin.reflect.KClass

fun <T : Any> T.toJson(fields: Array<String> = emptyArray(), extendValueMap: Map<String, Any> = emptyMap(), jsonProcessor: JsonUtil.JsonProcessor = JsonUtil.DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
    return JsonUtil.objectToJson(this, fields, extendValueMap, jsonProcessor, ignoreFirstLetterCase)
}

fun Array<*>.toJson(jsonProcessor: JsonUtil.JsonProcessor = JsonUtil.DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false) = JsonUtil.arrayToJson(this, jsonProcessor, ignoreFirstLetterCase)

fun <T> Iterable<T>.toJson(jsonProcessor: JsonUtil.JsonProcessor = JsonUtil.DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false) = JsonUtil.iterableToJson(this, jsonProcessor, ignoreFirstLetterCase)

fun <K, V> Map<K, V>.toJson(extendValueMap: Map<String, Any> = emptyMap(), jsonProcessor: JsonUtil.JsonProcessor = JsonUtil.DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false) = JsonUtil.mapToJson(this, extendValueMap, jsonProcessor, ignoreFirstLetterCase)

fun String.jsonToArrayBoolean(supportDuplicateKey: Boolean = false): Array<Boolean> = JsonUtil.jsonToArrayBoolean(this, supportDuplicateKey)

fun String.jsonToArrayInt(supportDuplicateKey: Boolean = false): Array<Int> = JsonUtil.jsonToArrayInt(this, supportDuplicateKey)

fun String.jsonToArrayLong(supportDuplicateKey: Boolean = false): Array<Long> = JsonUtil.jsonToArrayLong(this, supportDuplicateKey)

fun String.jsonToArrayFloat(supportDuplicateKey: Boolean = false): Array<Float> = JsonUtil.jsonToArrayFloat(this, supportDuplicateKey)

fun String.jsonToArrayDouble(supportDuplicateKey: Boolean = false): Array<Double> = JsonUtil.jsonToArrayDouble(this, supportDuplicateKey)

fun String.jsonToArrayString(supportDuplicateKey: Boolean = false): Array<String> = JsonUtil.jsonToArrayString(this, supportDuplicateKey)

fun <T : Any> String.jsonToObject(kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = JsonUtil.DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, fieldNameKClassMapping: Map<String, Pair<DefaultJsonKotlinClassProcessor.Type, KClass<*>>> = emptyMap(), ignoreFirstLetterCase: Boolean = false, ignoreFieldNames: Array<String> = emptyArray()): T =
    JsonUtil.jsonToObject(this, kClass, classProcessor, fieldNameKClassMapping, ignoreFirstLetterCase, ignoreFieldNames)

fun <T : Any> String.jsonToObjectList(kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = JsonUtil.DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, fieldNameKClassMapping: Map<String, Pair<DefaultJsonKotlinClassProcessor.Type, KClass<*>>> = emptyMap(), ignoreFirstLetterCase: Boolean = false, ignoreFieldNames: Array<String> = emptyArray()): List<T> =
    JsonUtil.jsonToObjectList(this, kClass, classProcessor, fieldNameKClassMapping, ignoreFirstLetterCase, ignoreFieldNames)

fun JsonArray.toArrayBoolean(): Array<Boolean> = JsonUtil.jsonArrayToArrayBoolean(this)

fun JsonArray.toArrayFloat(): Array<Float> = JsonUtil.jsonArrayToArrayFloat(this)

fun JsonArray.toArrayDouble(): Array<Double> = JsonUtil.jsonArrayToArrayDouble(this)

fun JsonArray.toArrayInt(): Array<Int> = JsonUtil.jsonArrayToArrayInt(this)

fun JsonArray.toArrayLong(): Array<Long> = JsonUtil.jsonArrayToArrayLong(this)

fun JsonArray.toArrayString(): Array<String> = JsonUtil.jsonArrayToArrayString(this)