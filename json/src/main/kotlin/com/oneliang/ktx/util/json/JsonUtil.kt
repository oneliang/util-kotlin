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
     * Method:simple array to json
     * @param <T>
     * @param array
     * @param jsonProcessor
     * @return String
    </T> */
    fun <T : Any> simpleArrayToJson(array: Array<T>, jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR): String {
        return array.toJson(jsonProcessor)
    }

    /**
     * Method:object array to json
     * @param <T>
     * @param array
     * @param jsonProcessor
     * @return String
    </T> */
    fun <T : Any> objectArrayToJson(array: Array<T>, jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR) = simpleArrayToJson(array, jsonProcessor)

    /**
     * Method:object array to json
     * @param <T>
     * @param array
     * @param fields
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return String
    </T> */
    @Deprecated("Deprecated")
    private fun <T : Any> objectArrayToJson(array: Array<T>, fields: Array<String> = emptyArray(), jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean): String {
        val string = StringBuilder()
        string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
        val length = array.size
        for (i in 0 until length) {
            val instance = array[i]
            string.append(objectToJson(instance, fields, jsonProcessor, ignoreFirstLetterCase))
            if (i < length - 1) {
                string.append(Constants.Symbol.COMMA)
            }
        }
        string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
        return string.toString()
    }

    /**
     * Method:object array to json array,key means json's properties,value means
     * object field
     * @param <T>
     * @param array
     * @param fieldMap
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return String
    </T> */
    @Deprecated("Deprecated")
    private fun <T : Any> objectArrayToJson(array: Array<T>, fieldMap: Map<String, String> = emptyMap(), jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String? {
        val result: String
        val string = StringBuilder()
        string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
        val length = array.size
        for (i in 0 until length) {
            val instance = array[i]
            string.append(objectToJson(instance, fieldMap, jsonProcessor, ignoreFirstLetterCase))
            if (i < length - 1) {
                string.append(Constants.Symbol.COMMA)
            }
        }
        string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
        result = string.toString()
        return result
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
        val string = StringBuilder()
        string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT)
        val iterator = iterable.iterator()
        while (iterator.hasNext()) {
            val instance = iterator.next()
            string.append(jsonProcessor.process<Any>(null, Constants.String.BLANK, instance, ignoreFirstLetterCase))
            if (iterator.hasNext()) {
                string.append(Constants.Symbol.COMMA)
            }
        }
        string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT)
        return string.toString()
    }

    fun <K, V> mapToJson(map: Map<K, V>, jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        val string = StringBuilder()
        string.append(Constants.Symbol.BIG_BRACKET_LEFT)
        string.append(map.joinToString { key, instance ->
            val value = jsonProcessor.process<Any>(null, key.toString(), instance, ignoreFirstLetterCase)
            Constants.Symbol.DOUBLE_QUOTE + key.toString() + Constants.Symbol.DOUBLE_QUOTE + Constants.Symbol.COLON + value
        })
        string.append(Constants.Symbol.BIG_BRACKET_RIGHT)
        return string.toString()
    }

    /**
     * Method: object to json string
     * @param instance
     * @param fields
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json string
     */
    fun <T : Any> objectToJson(instance: T, fields: Array<String> = emptyArray(), jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        val objectJson = StringBuilder()
        val kClass = instance.javaClass.kotlin
        val fieldTreeSet: SortedSet<String>
        objectJson.append(Constants.Symbol.BIG_BRACKET_LEFT)
        if (fields.isNotEmpty()) {
            fieldTreeSet = fields.toSortedSet()
        } else {
            fieldTreeSet = TreeSet<String>()
            val methods = instance.javaClass.methods
            for (method in methods) {
                val methodName = method.name
                val fieldName = ObjectUtil.methodNameToFieldName(methodName, ignoreFirstLetterCase)
                if (fieldName.isNotBlank()) {
                    fieldTreeSet += fieldName
                }
            }
        }
        val length = fieldTreeSet.size
        fieldTreeSet.forEachIndexed { index, fieldName ->
            var methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName, ignoreFirstLetterCase)
            methodReturnValue = jsonProcessor.process(kClass, fieldName, methodReturnValue, ignoreFirstLetterCase)
            objectJson.append(Constants.Symbol.DOUBLE_QUOTE + fieldName + Constants.Symbol.DOUBLE_QUOTE + Constants.Symbol.COLON + methodReturnValue.toString())
            if (index < length - 1) {
                objectJson.append(Constants.Symbol.COMMA)
            }
        }
        objectJson.append(Constants.Symbol.BIG_BRACKET_RIGHT)
        return objectJson.toString()
    }

    /**
     * Method:object to json with field map,key means json's properties,value
     * means object field
     * @param <T>
     * @param instance
     * @param fieldMap
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json
    </T> */
    fun <T : Any> objectToJson(instance: T, fieldMap: Map<String, String>, jsonProcessor: JsonProcessor = DEFAULT_JSON_PROCESSOR, ignoreFirstLetterCase: Boolean = false): String {
        val objectJson = StringBuilder()
        val kClass = instance.javaClass.kotlin
        val iterator = fieldMap.entries.iterator()
        objectJson.append(Constants.Symbol.BIG_BRACKET_LEFT)
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val key = entry.key
            val fieldName = entry.value
            var methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName, ignoreFirstLetterCase)
            methodReturnValue = jsonProcessor.process(kClass, fieldName, methodReturnValue, ignoreFirstLetterCase)
            objectJson.append(key + Constants.Symbol.COLON + methodReturnValue.toString())
            if (iterator.hasNext()) {
                objectJson.append(Constants.Symbol.COMMA)
            }
        }
        objectJson.append(Constants.Symbol.BIG_BRACKET_RIGHT)
        return objectJson.toString()
    }

    /**
     * jsonObject to object
     * @param jsonObject
     * @param kClass
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNameArray
     * @return T
     */
    fun <T : Any> jsonObjectToObject(jsonObject: JsonObject, kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, ignoreFirstLetterCase: Boolean = false, ignoreFieldNameArray: Array<String> = emptyArray()): T {
        val instance: T
        val methods = kClass.java.methods
        try {
            instance = kClass.java.newInstance()
        } catch (e: Throwable) {
            throw JsonException(e)
        }
        val ignoreFieldNameSet = ignoreFieldNameArray.toHashSet()
        for (method in methods) {
            val methodName = method.name
            val fieldName = if (methodName.startsWith(Constants.Method.PREFIX_SET)) {
                ObjectUtil.methodNameToFieldName(Constants.Method.PREFIX_SET, methodName, ignoreFirstLetterCase)
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
                                    KotlinClassUtil.changeType(objectClass, arrayOf(jsonObject.get(fieldName).toString()), fieldName, classProcessor)
                                } else {
                                    KotlinClassUtil.changeType(objectClass, emptyArray(), fieldName, classProcessor)
                                }
                            } else if (KotlinClassUtil.isBaseArray(objectClass) || KotlinClassUtil.isSimpleArray(objectClass)) {
                                if (!jsonObject.isNull(fieldName)) {
                                    value = jsonArrayToArray(jsonObject.getJsonArray(fieldName), objectClass, fieldName, classProcessor)
                                }
                            } else {
                                if (!jsonObject.isNull(fieldName)) {
                                    value = KotlinClassUtil.changeType(objectClass, arrayOf(jsonObject.get(fieldName).toString()), fieldName, classProcessor)
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
     * @param kClass
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNameArray
     * @return List<T>
    </T></T> */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> jsonArrayToList(jsonArray: JsonArray, kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, ignoreFirstLetterCase: Boolean = false, ignoreFieldNameArray: Array<String> = emptyArray()): List<T> {
        val length = jsonArray.length()
        val list = mutableListOf<T>()
        for (i in 0 until length) {
            val jsonArrayItem = jsonArray.get(i)
            if (jsonArrayItem is JsonObject) {
                list.add(jsonObjectToObject(jsonArrayItem, kClass, classProcessor, ignoreFirstLetterCase, ignoreFieldNameArray))
            } else if (jsonArrayItem is JsonArray) {
                val subArrayLength = jsonArrayItem.length()
                val stringArray = Array(subArrayLength) { Constants.String.BLANK }
                for (j in 0 until subArrayLength) {
                    val subJsonArrayItem = jsonArrayItem.get(j)
                    stringArray[j] = subJsonArrayItem.toString()
                }
                list.add(classProcessor.changeClassProcess(kClass, stringArray, Constants.String.BLANK) as T)
            }
        }
        return list
    }

    /**
     * json to object
     * @param json
     * @param kClass
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNameArray
     * @return T
     */
    fun <T : Any> jsonToObject(json: String, kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, ignoreFirstLetterCase: Boolean = false, ignoreFieldNameArray: Array<String> = emptyArray()): T {
        val jsonObject = json.jsonToJsonObject()
        return jsonObjectToObject(jsonObject, kClass, classProcessor, ignoreFirstLetterCase, ignoreFieldNameArray)
    }

    /**
     * json to object list
     * @param json
     * @param kClass
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @param ignoreFieldNameArray
     * @return List<T>
    </T> */
    fun <T : Any> jsonToObjectList(json: String, kClass: KClass<T>, classProcessor: KotlinClassUtil.KotlinClassProcessor = DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR, ignoreFirstLetterCase: Boolean = false, ignoreFieldNameArray: Array<String> = emptyArray()): List<T> {
        val jsonArray = json.jsonToJsonArray()
        return jsonArrayToList(jsonArray, kClass, classProcessor, ignoreFirstLetterCase, ignoreFieldNameArray)
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
