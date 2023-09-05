package com.oneliang.ktx.util.json

import com.oneliang.ktx.Constants
import com.oneliang.ktx.exception.MethodInvokeException
import com.oneliang.ktx.util.common.KotlinClassUtil
import com.oneliang.ktx.util.common.ObjectUtil
import com.oneliang.ktx.util.common.joinToString
import java.util.*
import kotlin.reflect.KClass

object JsonUtil {

    val DEFAULT_JSON_PROCESSOR: JsonProcessor = DefaultJsonProcessor()
    val DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR = DefaultJsonKotlinClassProcessor()

    /**
     * Method:basic array to json
     * @param instance
     * @return String
     */
    fun baseArrayToJson(instance: Any): String {
        return when (instance) {
            is BooleanArray -> {
                instance.toJson()
            }
            is ByteArray -> {
                instance.toJson()
            }
            is ShortArray -> {
                instance.toJson()
            }
            is IntArray -> {
                instance.toJson()
            }
            is LongArray -> {
                instance.toJson()
            }
            is FloatArray -> {
                instance.toJson()
            }
            is DoubleArray -> {
                instance.toJson()
            }
            is CharArray -> {
                instance.toJson()
            }
            else -> {
                throw JsonUtilException("unsupport in this method")
            }
        }
    }

    fun jsonArrayToArrayBoolean(jsonArray: JsonArray): Array<Boolean> {
        return Array(jsonArray.length()) { index ->
            jsonArray.optBoolean(index, false)
        }
    }

    fun jsonToArrayBoolean(json: String, supportDuplicateKey: Boolean = false): Array<Boolean> {
        val jsonArray = JsonArray(json, supportDuplicateKey)
        return jsonArrayToArrayBoolean(jsonArray)
    }

    fun jsonArrayToArrayInt(jsonArray: JsonArray): Array<Int> {
        return Array(jsonArray.length()) { index ->
            jsonArray.optInt(index, 0)
        }
    }

    fun jsonToArrayInt(json: String, supportDuplicateKey: Boolean = false): Array<Int> {
        val jsonArray = JsonArray(json, supportDuplicateKey)
        return jsonArrayToArrayInt(jsonArray)
    }

    fun jsonArrayToArrayLong(jsonArray: JsonArray): Array<Long> {
        return Array(jsonArray.length()) { index ->
            jsonArray.optLong(index, 0L)
        }
    }

    fun jsonToArrayLong(json: String, supportDuplicateKey: Boolean = false): Array<Long> {
        val jsonArray = JsonArray(json, supportDuplicateKey)
        return jsonArrayToArrayLong(jsonArray)
    }

    fun jsonArrayToArrayFloat(jsonArray: JsonArray): Array<Float> {
        return Array(jsonArray.length()) { index ->
            jsonArray.optDouble(index, 0.0).toFloat()
        }
    }

    fun jsonToArrayFloat(json: String, supportDuplicateKey: Boolean = false): Array<Float> {
        val jsonArray = JsonArray(json, supportDuplicateKey)
        return jsonArrayToArrayFloat(jsonArray)
    }

    fun jsonArrayToArrayDouble(jsonArray: JsonArray): Array<Double> {
        return Array(jsonArray.length()) { index ->
            jsonArray.optDouble(index, 0.0)
        }
    }

    fun jsonToArrayDouble(json: String, supportDuplicateKey: Boolean = false): Array<Double> {
        val jsonArray = JsonArray(json, supportDuplicateKey)
        return jsonArrayToArrayDouble(jsonArray)
    }

    fun jsonArrayToArrayString(jsonArray: JsonArray): Array<String> {
        return Array(jsonArray.length()) { index ->
            jsonArray.optString(index, Constants.String.BLANK)
        }
    }

    fun jsonToArrayString(json: String, supportDuplicateKey: Boolean = false): Array<String> {
        val jsonArray = JsonArray(json, supportDuplicateKey)
        return jsonArrayToArrayString(jsonArray)
    }

    /**
     * Method: array to json
     * @param <T>
     * @param array
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return String
     */
    fun <T> arrayToJson(array: Array<T>, jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        return array.joinToString(
            prefix = Constants.Symbol.MIDDLE_BRACKET_LEFT,
            postfix = Constants.Symbol.MIDDLE_BRACKET_RIGHT,
            separator = Constants.Symbol.COMMA
        ) {
            if (it != null) {
                jsonProcessor.process<Any>(null, Constants.String.BLANK, it, ignoreFirstLetterCase)
            } else {
                it.toString()
            }
        }
    }

    /**
     * Method: iterable to json
     * @param <T>
     * @param iterable
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return String
    </T> */
    fun <T> iterableToJson(iterable: Iterable<T>, jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        return iterable.joinToString(
            separator = Constants.Symbol.COMMA,
            prefix = Constants.Symbol.MIDDLE_BRACKET_LEFT,
            postfix = Constants.Symbol.MIDDLE_BRACKET_RIGHT
        ) {
            jsonProcessor.process<Any>(null, Constants.String.BLANK, it, ignoreFirstLetterCase)
        }
    }

    /**
     * Method: map to json
     * @param map
     * @param extendValueMap can append new key and value, can replace value with same key, key is json's properties, value is value
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json string
     */
    fun <K, V> mapToJson(map: Map<K, V>, extendValueMap: Map<String, Any> = emptyMap(), jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        val keyMap = mutableMapOf<Any?, Any?>()
        map.forEach { (key, value) ->
            keyMap += key to value
        }
        extendValueMap.forEach { (key, _) ->
            keyMap += key to key
        }
        return keyMap.joinToString(
            separator = Constants.Symbol.COMMA,
            prefix = Constants.Symbol.BIG_BRACKET_LEFT,
            postfix = Constants.Symbol.BIG_BRACKET_RIGHT
        ) { key, _ ->
            var extendValue = extendValueMap[key.toString()]
            extendValue = if (extendValue == null) {
                val instance = map[key]
                jsonProcessor.process<Any>(null, key.toString(), instance, ignoreFirstLetterCase)
            } else {
                jsonProcessor.process(extendValue.javaClass.kotlin, key.toString(), extendValue, ignoreFirstLetterCase)
            }
            Constants.Symbol.DOUBLE_QUOTE + key + Constants.Symbol.DOUBLE_QUOTE + Constants.Symbol.COLON + extendValue
        }
    }

    /**
     * Method: object to json
     * @param instance
     * @param fields
     * @param extendValueMap can append new key and value, can replace value with same key, key is json's properties, value is value
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json string
     */
    fun <T : Any> objectToJson(instance: T, fields: Array<String> = emptyArray(), extendValueMap: Map<String, Any> = emptyMap(), jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        when (instance) {
            is Array<*> -> {
                return arrayToJson(instance, jsonProcessor, ignoreFirstLetterCase)
            }
            is Iterable<*> -> {
                return iterableToJson(instance, jsonProcessor, ignoreFirstLetterCase)
            }
            is Map<*, *> -> {
                return mapToJson(instance, extendValueMap, jsonProcessor, ignoreFirstLetterCase)
            }
        }
        val kClass = instance.javaClass.kotlin
        val fieldTreeSet: SortedSet<String>
        if (fields.isNotEmpty()) {
            fieldTreeSet = fields.toSortedSet()
        } else {
            fieldTreeSet = TreeSet()
            val methods = instance.javaClass.methods
            for (method in methods) {
                val methodName = method.name
                val fieldName = ObjectUtil.methodNameToFieldName(methodName, ignoreFirstLetterCase)
                if (fieldName.isNotBlank()) {
                    fieldTreeSet += fieldName
                }
            }
        }
        fieldTreeSet += extendValueMap.keys
        return fieldTreeSet.joinToString(
            separator = Constants.Symbol.COMMA,
            prefix = Constants.Symbol.BIG_BRACKET_LEFT,
            postfix = Constants.Symbol.BIG_BRACKET_RIGHT
        ) { fieldName ->
            var extendValue = extendValueMap[fieldName]
            extendValue = if (extendValue == null) {
                val methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName, ignoreFirstLetterCase)
                jsonProcessor.process(kClass, fieldName, methodReturnValue, ignoreFirstLetterCase)
            } else {
                jsonProcessor.process(extendValue.javaClass.kotlin, fieldName, extendValue, ignoreFirstLetterCase)
            }
            Constants.Symbol.DOUBLE_QUOTE + fieldName + Constants.Symbol.DOUBLE_QUOTE + Constants.Symbol.COLON + extendValue.toString()
        }
    }

    /**
     * Method:object to json with field map,key means json's properties,value
     * means object field
     * @param <T>
     * @param instance
     * @param keyFieldMap
     * @param extendValueMap can append new key and value, can replace value with same key, key is json's properties, value is value
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json
    </T> */
    fun <T : Any> objectToJson(instance: T, keyFieldMap: Map<String, String>, extendValueMap: Map<String, Any> = emptyMap(), jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        when (instance) {
            is Array<*> -> {
                return arrayToJson(instance, jsonProcessor, ignoreFirstLetterCase)
            }
            is Iterable<*> -> {
                return iterableToJson(instance, jsonProcessor, ignoreFirstLetterCase)
            }
            is Map<*, *> -> {
                return mapToJson(instance, extendValueMap, jsonProcessor, ignoreFirstLetterCase)
            }
        }
        val kClass = instance.javaClass.kotlin
        val keyMap = mutableMapOf<String, String>()
        keyFieldMap.forEach { (key, field) ->
            keyMap += key to field
        }
        extendValueMap.forEach { (key, _) ->
            keyMap += key to key
        }
        return keyMap.joinToString(
            separator = Constants.Symbol.COMMA,
            prefix = Constants.Symbol.BIG_BRACKET_LEFT,
            postfix = Constants.Symbol.BIG_BRACKET_RIGHT
        ) { key, fieldName ->
            var extendValue = extendValueMap[key]
            extendValue = if (extendValue == null) {
                val methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName, ignoreFirstLetterCase)
                jsonProcessor.process(kClass, fieldName, methodReturnValue, ignoreFirstLetterCase)
            } else {
                jsonProcessor.process(extendValue.javaClass.kotlin, fieldName, extendValue, ignoreFirstLetterCase)
            }
            Constants.Symbol.DOUBLE_QUOTE + key + Constants.Symbol.DOUBLE_QUOTE + Constants.Symbol.COLON + extendValue.toString()
        }
    }

    /**
     * jsonObject to object
     * @param jsonObject
     * @param kClass
     * @param classProcessor
     * @param fieldNameKClassMapping
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNames
     * @return T
     */
    fun <T : Any> jsonObjectToObject(
        jsonObject: JsonObject,
        kClass: KClass<T>,
        classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR,
        fieldNameKClassMapping: Map<String, Pair<DefaultJsonKotlinClassProcessor.Type, KClass<*>>> = emptyMap(),
        ignoreFirstLetterCase: Boolean = false,
        ignoreFieldNames: Array<String> = emptyArray()): T {
        val instance: T
        val methods = kClass.java.methods
        try {
            instance = kClass.java.newInstance()
        } catch (e: Throwable) {
            throw JsonException(e)
        }
        val ignoreFieldNameSet = ignoreFieldNames.toHashSet()
        for (method in methods) {
            val methodName = method.name
            val fieldName = if (methodName.startsWith(Constants.Object.Method.PREFIX_SET)) {
                ObjectUtil.methodNameToFieldName(Constants.Object.Method.PREFIX_SET, methodName, ignoreFirstLetterCase)
            } else {
                Constants.String.BLANK
            }
            if (fieldName.isNotBlank() && !ignoreFieldNameSet.contains(fieldName)) {
                val classes = method.parameterTypes
                var value: Any? = null
                if (classes.size == 1) {
                    val objectClass = classes[0].kotlin
                    if (jsonObject.has(fieldName)) {
                        try {
                            if (KotlinClassUtil.isSimpleClass(objectClass)) {
                                value = if (!jsonObject.isNull(fieldName)) {
                                    KotlinClassUtil.changeType(objectClass, arrayOf(jsonObject.get(fieldName).toString()), fieldName, classProcessor, fieldNameKClassMapping)
                                } else {
                                    KotlinClassUtil.changeType(objectClass, emptyArray(), fieldName, classProcessor, fieldNameKClassMapping)
                                }
                            } else if (KotlinClassUtil.isBaseArray(objectClass) || KotlinClassUtil.isSimpleArray(objectClass)) {
                                if (!jsonObject.isNull(fieldName)) {
                                    value = jsonArrayToArray(jsonObject.getJsonArray(fieldName), objectClass, fieldName, classProcessor)
                                }
                            } else {
                                if (!jsonObject.isNull(fieldName)) {
                                    value = KotlinClassUtil.changeType(objectClass, arrayOf(jsonObject.get(fieldName).toString()), fieldName, classProcessor, fieldNameKClassMapping)
                                }
                            }
                        } catch (e: Throwable) {
                            throw JsonException(kClass.simpleName + Constants.Symbol.DOT + fieldName + ", value:${jsonObject.get(fieldName)}", e)
                        }
                        try {
                            method.invoke(instance, value)
                        } catch (e: Throwable) {
                            throw MethodInvokeException(kClass.simpleName + Constants.Symbol.DOT + fieldName + ", value:$value", e)
                        }
                    }
                }
            }
        }
        return instance
    }

    /**
     * jsonArray to array,just include base array and simple array
     * @param jsonArray
     * @param kClass
     * @param fieldName
     * @param classProcessor
     * @return Object
     */
    private fun jsonArrayToArray(jsonArray: JsonArray, kClass: KClass<*>, fieldName: String, classProcessor: KotlinClassUtil.KotlinClassProcessor): Any {
        val length = jsonArray.length()
        val values = Array(length) { Constants.String.BLANK }
        for (i in 0 until length) {
            values[i] = jsonArray.get(i).toString()
        }
        return KotlinClassUtil.changeType(kClass, values, fieldName, classProcessor) ?: Constants.String.BLANK
    }

    /**
     * jsonArray to list
     * @param <T>
     * @param jsonArray
     * @param kClass may be array component class
     * @param classProcessor
     * @param fieldNameKClassMapping
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNames
     * @return List<T>
    </T></T> */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> jsonArrayToList(
        jsonArray: JsonArray,
        kClass: KClass<T>,
        classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR,
        fieldNameKClassMapping: Map<String, Pair<DefaultJsonKotlinClassProcessor.Type, KClass<*>>> = emptyMap(),
        ignoreFirstLetterCase: Boolean = false,
        ignoreFieldNames: Array<String> = emptyArray()): List<T> {
        val length = jsonArray.length()
        val list = mutableListOf<T>()
        for (i in 0 until length) {
            val jsonArrayItem = jsonArray.get(i)
            if (jsonArrayItem is JsonObject) {
                list.add(jsonObjectToObject(jsonArrayItem, kClass, classProcessor, fieldNameKClassMapping, ignoreFirstLetterCase, ignoreFieldNames))
            } else if (jsonArrayItem is JsonArray) {
                //last depth, so spread the data, only base array and simple array use
                if (KotlinClassUtil.isBaseArray(kClass) || KotlinClassUtil.isSimpleArray(kClass)) {
                    list.add(jsonArrayToArray(jsonArrayItem, kClass, Constants.String.BLANK, classProcessor) as T)
                } else {
                    list.add(classProcessor.changeClassProcess(kClass, arrayOf(jsonArrayItem.toString()), Constants.String.BLANK, null) as T)
                }
            }
        }
        return list
    }

    /**
     * json to object
     * @param json
     * @param kClass
     * @param classProcessor
     * @param fieldNameKClassMapping
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNames
     * @return T
     */
    fun <T : Any> jsonToObject(json: String, kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, fieldNameKClassMapping: Map<String, Pair<DefaultJsonKotlinClassProcessor.Type, KClass<*>>> = emptyMap(), ignoreFirstLetterCase: Boolean = false, ignoreFieldNames: Array<String> = emptyArray()): T {
        val jsonObject = json.jsonToJsonObject()
        return jsonObjectToObject(jsonObject, kClass, classProcessor, fieldNameKClassMapping, ignoreFirstLetterCase, ignoreFieldNames)
    }

    /**
     * json to object list
     * @param json
     * @param kClass
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNames
     * @return List<T>
    </T> */
    fun <T : Any> jsonToObjectList(json: String, kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, fieldNameKClassMapping: Map<String, Pair<DefaultJsonKotlinClassProcessor.Type, KClass<*>>> = emptyMap(), ignoreFirstLetterCase: Boolean = false, ignoreFieldNames: Array<String> = emptyArray()): List<T> {
        val jsonArray = json.jsonToJsonArray()
        return jsonArrayToList(jsonArray, kClass, classProcessor, fieldNameKClassMapping, ignoreFirstLetterCase, ignoreFieldNames)
    }

    class JsonUtilException(message: String) : Exception(message)

    interface JsonProcessor {

        /**
         * process
         * @param <T>
         * @param kClass
         * @param fieldName
         * @param value
         * @param ignoreFirstLetterCase
         * @return String
         */
        fun <T : Any> process(kClass: KClass<T>? = null, fieldName: String, value: Any? = null, ignoreFirstLetterCase: Boolean = false): String
    }
}
