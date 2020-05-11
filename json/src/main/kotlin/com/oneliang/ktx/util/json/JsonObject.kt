package com.oneliang.ktx.util.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Modifier
import java.util.*

class JsonObject() {
    /**
     * The map where the JsonObject's properties are kept.
     */
    private val map = mutableMapOf<String, Any>()

    /**
     * JsonObject.NULL is equivalent to the value that JavaScript calls null,
     * whilst Java's null is equivalent to the value that JavaScript calls
     * undefined.
     */
    private class Null {
        /**
         * There is only intended to be a single instance of the NULL object, so
         * the clone method returns itself.
         *
         * @return NULL.
         */
        protected fun clone(): Any {
            return this
        }

        /**
         * Get the "null" string value.
         *
         * @return The string "null".
         */
        override fun toString(): String {
            return "null"
        }
    }

    /**
     * Construct an empty JsonObject.
     */
//    constructor() {
//        this.map = HashMap<String, Any>()
//    }

    /**
     * Construct a JsonObject from a subset of another JsonObject. An array of
     * strings is used to identify the keys that should be copied. Missing keys
     * are ignored.
     *
     * @param jo
     * A JsonObject.
     * @param names
     * An array of strings.
     * @throws JsonException
     * @exception JsonException
     * If a value is a non-finite number or if a name is
     * duplicated.
     */
    constructor(jo: JsonObject, names: Array<String>) : this() {
        var i = 0
        while (i < names.size) {
            try {
                this.putOnce(names[i], jo.opt(names[i]))
            } catch (ignore: Exception) {
            }
            i += 1
        }
    }

    /**
     * Construct a JsonObject from a JsonTokener.
     *
     * @param x
     * A JsonTokener object containing the source string.
     * @throws JsonException
     * If there is a syntax error in the source string or a
     * duplicated key.
     */
    @Throws(JsonException::class)
    constructor(x: JsonTokener) : this() {
        var c: Char
        var key: String
        if (x.nextClean() != '{') {
            throw x.syntaxError("A JsonObject text must begin with '{'")
        }
        while (true) {
            c = x.nextClean()
            when (c) {
                0.toChar() -> throw x.syntaxError("A JsonObject text must end with '}'")
                '}' -> return
                else -> {
                    x.back()
                    key = x.nextValue().toString()
                }
            }
// The key is followed by ':'. We will also tolerate '=' or '=>'.
            c = x.nextClean()
            if (c == '=') {
                if (x.next() != '>') {
                    x.back()
                }
            } else if (c != ':') {
                throw x.syntaxError("Expected a ':' after a key")
            }
            this.putOnce(key, x.nextValue())
// Pairs are separated by ','. We will also tolerate ';'.
            when (x.nextClean()) {
                ';', ',' -> {
                    if (x.nextClean() == '}') {
                        return
                    }
                    x.back()
                }
                '}' -> return
                else -> throw x.syntaxError("Expected a ',' or '}'")
            }
        }
    }

    /**
     * Construct a JsonObject from a Map.
     *
     * @param map
     * A map object that can be used to initialize the contents of
     * the JsonObject.
     * @throws JsonException
     */
    constructor(map: Map<String, Any>) : this() {
        val i = map.iterator()
        while (i.hasNext()) {
            val e = i.next()
            val value = e.value
            this.map.put(e.key, wrap(value))
        }
    }

    /**
     * Construct a JsonObject from an Any using bean getters. It reflects on
     * all of the public methods of the object. For each of the methods with no
     * parameters and a name starting with <code>"get"</code> or
     * <code>"is"</code> followed by an uppercase letter, the method is invoked,
     * and a key and the value returned from the getter method are put into the
     * new JsonObject.
     *
     * The key is formed by removing the <code>"get"</code> or <code>"is"</code>
     * prefix. If the second remaining character is not upper case, then the
     * first character is converted to lower case.
     *
     * For example, if an object has a method named <code>"getName"</code>, and
     * if the result of calling <code>object.getName()</code> is
     * <code>"Larry Fine"</code>, then the JsonObject will contain
     * <code>"name": "Larry Fine"</code>.
     *
     * @param bean
     * An object that has getter methods that should be used to make
     * a JsonObject.
     */
    constructor(bean: Any) : this() {
        this.populateMap(bean)
    }

    /**
     * Construct a JsonObject from an Any, using reflection to find the
     * public members. The resulting JsonObject's keys will be the strings from
     * the names array, and the values will be the field values associated with
     * those keys in the object. If a key is not found or not visible, then it
     * will not be copied into the new JsonObject.
     *
     * @param object
     * An object that has fields that should be used to make a
     * JsonObject.
     * @param names
     * An array of strings, the names of the fields to be obtained
     * from the object.
     */
    constructor(value: Any, names: Array<String>) : this() {
        val c = value::class.java
        var i = 0
        while (i < names.size) {
            val name = names[i]
            try {
                this.putOpt(name, c.getField(name).get(value))
            } catch (ignore: Exception) {
            }
            i += 1
        }
    }

    /**
     * Construct a JsonObject from a source JSON text string. This is the most
     * commonly used JsonObject constructor.
     *
     * @param source
     * A string beginning with <code>{</code>&nbsp;<small>(left
     * brace)</small> and ending with <code>}</code>
     * &nbsp;<small>(right brace)</small>.
     * @exception JsonException
     * If there is a syntax error in the source string or a
     * duplicated key.
     */
    @Throws(JsonException::class)
    constructor(source: String) : this(JsonTokener(source)) {
    }

    /**
     * Construct a JsonObject from a ResourceBundle.
     *
     * @param baseName
     * The ResourceBundle base name.
     * @param locale
     * The Locale to load the ResourceBundle for.
     * @throws JsonException
     * If any JsonExceptions are detected.
     */
    @Throws(JsonException::class)
    constructor(baseName: String, locale: Locale) : this() {
        val bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader())
// Iterate through the keys in the bundle.
        val keys = bundle.getKeys()
        while (keys.hasMoreElements()) {
            val key = keys.nextElement()
            if (key is String) {
// Go through the path, ensuring that there is a nested
// JsonObject for each
// segment except the last. Add the value using the last
// segment's name into
// the deepest nested JsonObject.
                val path = key.split("\\.")
                val last = path.size - 1
                var target = this
                var i = 0
                while (i < last) {
                    val segment = path[i]
                    var nextTarget = target.optJsonObject(segment)
                    target.put(segment, nextTarget)
                    target = nextTarget
                    i += 1
                }
                target.put(path[last], bundle.getString(key))
            }
        }
    }

    /**
     * Accumulate values under a key. It is similar to the put method except
     * that if there is already an object stored under the key then a JsonArray
     * is stored under the key to hold all of the accumulated values. If there
     * is already a JsonArray, then the new value is appended to it. In
     * contrast, the put method replaces the previous value.
     *
     * If only one value is accumulated that is not a JsonArray, then the result
     * will be the same as using put. But if multiple values are accumulated,
     * then the result will be like append.
     *
     * @param key
     * A key string.
     * @param value
     * An object to be accumulated under the key.
     * @return this.
     * @throws JsonException
     * If the value is an invalid number or if the key is null.
     */
    @Throws(JsonException::class)
    fun accumulate(key: String, value: Any): JsonObject {
        testValidity(value)
        val objectValue = this.opt(key)
        if (objectValue == NULL) {
            this.put(key, if (value is JsonArray) JsonArray().put(value) else value)
        } else if (objectValue is JsonArray) {
            objectValue.put(value)
        } else {
            this.put(key, JsonArray().put(objectValue).put(value))
        }
        return this
    }

    /**
     * Append values to the array under a key. If the key does not exist in the
     * JsonObject, then the key is put in the JsonObject with its value being a
     * JsonArray containing the value parameter. If the key was already
     * associated with a JsonArray, then the value parameter is appended to it.
     *
     * @param key
     * A key string.
     * @param value
     * An object to be accumulated under the key.
     * @return this.
     * @throws JsonException
     * If the key is null or if the current value associated with
     * the key is not a JsonArray.
     */
    @Throws(JsonException::class)
    fun append(key: String, value: Any): JsonObject {
        testValidity(value)
        val objectValue = this.opt(key)
        if (objectValue == NULL) {
            this.put(key, JsonArray().put(value))
        } else if (objectValue is JsonArray) {
            this.put(key, objectValue.put(value))
        } else {
            throw JsonException("JsonObject[" + key + "] is not a JsonArray.")
        }
        return this
    }

    /**
     * Get the value object associated with a key.
     *
     * @param key
     * A key string.
     * @return The object associated with the key.
     * @throws JsonException
     * if the key is not found.
     */
    @Throws(JsonException::class)
    fun get(key: String): Any {
        val value = this.opt(key)
        if (value == NULL) {
            throw JsonException("JsonObject[" + quote(key) + "] not found.")
        }
        return value
    }

    /**
     * Get the boolean value associated with a key.
     *
     * @param key
     * A key string.
     * @return The truth.
     * @throws JsonException
     * if the value is not a Boolean or the String "true" or
     * "false".
     */
    @Throws(JsonException::class)
    fun getBoolean(key: String): Boolean {
        val value = this.get(key)
        if ((value is Boolean && value.equals(false) || ((value is String && value.equals("false", true))))) {
            return false
        } else if ((value is Boolean && value.equals(true) || ((value is String && value.equals("true", true))))) {
            return true
        }
        throw JsonException("JsonObject[" + quote(key) + "] is not a Boolean.")
    }

    /**
     * Get the double value associated with a key.
     *
     * @param key
     * A key string.
     * @return The numeric value.
     * @throws JsonException
     * if the key is not found or if the value is not a Number
     * object and cannot be converted to a number.
     */
    @Throws(JsonException::class)
    fun getDouble(key: String): Double {
        val value = this.get(key)
        try {
            return if (value is Number) value.toDouble() else value.toString().toDouble()
        } catch (e: Exception) {
            throw JsonException("JsonObject[" + quote(key) + "] is not a number.")
        }
    }

    /**
     * Get the int value associated with a key.
     *
     * @param key
     * A key string.
     * @return The integer value.
     * @throws JsonException
     * if the key is not found or if the value cannot be converted
     * to an integer.
     */
    @Throws(JsonException::class)
    fun getInt(key: String): Int {
        val value = this.get(key)
        try {
            return if (value is Number) value.toInt() else value.toString().toInt()
        } catch (e: Exception) {
            throw JsonException("JsonObject[" + quote(key) + "] is not an int.")
        }
    }

    /**
     * Get the JsonArray value associated with a key.
     *
     * @param key
     * A key string.
     * @return A JsonArray which is the value.
     * @throws JsonException
     * if the key is not found or if the value is not a JsonArray.
     */
    @Throws(JsonException::class)
    fun getJsonArray(key: String): JsonArray {
        val value = this.get(key)
        if (value is JsonArray) {
            return value
        }
        throw JsonException("JsonObject[" + quote(key) + "] is not a JsonArray.")
    }

    /**
     * Get the JsonObject value associated with a key.
     *
     * @param key
     * A key string.
     * @return A JsonObject which is the value.
     * @throws JsonException
     * if the key is not found or if the value is not a JsonObject.
     */
    @Throws(JsonException::class)
    fun getJsonObject(key: String): JsonObject {
        val value = this.get(key)
        if (value is JsonObject) {
            return value
        }
        throw JsonException("JsonObject[" + quote(key) + "] is not a JsonObject.")
    }

    /**
     * Get the long value associated with a key.
     *
     * @param key
     * A key string.
     * @return The long value.
     * @throws JsonException
     * if the key is not found or if the value cannot be converted
     * to a long.
     */
    @Throws(JsonException::class)
    fun getLong(key: String): Long {
        val value = this.get(key)
        try {
            return if (value is Number) value.toLong() else value.toString().toLong()
        } catch (e: Exception) {
            throw JsonException("JsonObject[" + quote(key) + "] is not a long.")
        }
    }

    /**
     * Get the string associated with a key.
     *
     * @param key
     * A key string.
     * @return A string which is the value.
     * @throws JsonException
     * if there is no string value for the key.
     */
    @Throws(JsonException::class)
    fun getString(key: String): String {
        val value = this.get(key)
        if (value is String) {
            return value
        }
        throw JsonException("JsonObject[" + quote(key) + "] not a string.")
    }

    /**
     * Determine if the JsonObject contains a specific key.
     *
     * @param key
     * A key string.
     * @return true if the key exists in the JsonObject.
     */
    fun has(key: String): Boolean {
        return this.map.containsKey(key)
    }

    /**
     * Increment a property of a JsonObject. If there is no such property,
     * create one with a value of 1. If there is such a property, and if it is
     * an Integer, Long, Double, or Float, then add one to it.
     *
     * @param key
     * A key string.
     * @return this.
     * @throws JsonException
     * If there is already a property with this name that is not an
     * Integer, Long, Double, or Float.
     */
    @Throws(JsonException::class)
    fun increment(key: String): JsonObject {
        val value = this.opt(key)
        if (value == NULL) {
            this.put(key, 1)
        } else if (value is Int) {
            this.put(key, value + 1)
        } else if (value is Long) {
            this.put(key, value + 1)
        } else if (value is Double) {
            this.put(key, value + 1)
        } else if (value is Float) {
            this.put(key, value + 1)
        } else {
            throw JsonException("Unable to increment [" + quote(key) + "].")
        }
        return this
    }

    /**
     * Determine if the value associated with the key is null or if there is no
     * value.
     *
     * @param key
     * A key string.
     * @return true if there is no value associated with the key or if the value
     * is the JsonObject.NULL object.
     */
    fun isNull(key: String): Boolean {
        return JsonObject.NULL.equals(this.opt(key))
    }

    /**
     * Get an enumeration of the keys of the JsonObject.
     *
     * @return An iterator of the keys.
     */
    fun keys(): Iterator<String> {
        return this.map.keys.iterator()
    }

    /**
     * Get the number of keys stored in the JsonObject.
     *
     * @return The number of keys in the JsonObject.
     */
    fun length(): Int {
        return this.map.size
    }

    /**
     * Produce a JsonArray containing the names of the elements of this
     * JsonObject.
     *
     * @return A JsonArray containing the key strings, or null if the JsonObject
     * is empty.
     */
    fun names(): JsonArray {
        val ja = JsonArray()
        val keys = this.keys()
        while (keys.hasNext()) {
            ja.put(keys.next())
        }
        return if (ja.length() == 0) JsonArray() else ja
    }

    /**
     * Get an optional value associated with a key.
     *
     * @param key
     * A key string.
     * @return An object which is the value, or null if there is no value.
     */
    fun opt(key: String): Any {
        return this.map.get(key) ?: NULL
    }

    /**
     * Get an optional boolean associated with a key. It returns false if there
     * is no such key, or if the value is not Boolean.TRUE or the String "true".
     *
     * @param key
     * A key string.
     * @return The truth.
     */
    fun optBoolean(key: String): Boolean {
        return this.optBoolean(key, false)
    }

    /**
     * Get an optional boolean associated with a key. It returns the
     * defaultValue if there is no such key, or if it is not a Boolean or the
     * String "true" or "false" (case insensitive).
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return The truth.
     */
    fun optBoolean(key: String, defaultValue: Boolean): Boolean {
        try {
            return this.getBoolean(key)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get an optional double associated with a key, or NaN if there is no such
     * key or if its value is not a number. If the value is a string, an attempt
     * will be made to evaluate it as a number.
     *
     * @param key
     * A string which is the key.
     * @return An object which is the value.
     */
    fun optDouble(key: String): Double {
        return this.optDouble(key, Double.NaN)
    }

    /**
     * Get an optional double associated with a key, or the defaultValue if
     * there is no such key or if its value is not a number. If the value is a
     * string, an attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    fun optDouble(key: String, defaultValue: Double): Double {
        try {
            return this.getDouble(key)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get an optional int value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @return An object which is the value.
     */
    fun optInt(key: String): Int {
        return this.optInt(key, 0)
    }

    /**
     * Get an optional int value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    fun optInt(key: String, defaultValue: Int): Int {
        try {
            return this.getInt(key)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get an optional JsonArray associated with a key. It returns null if there
     * is no such key, or if its value is not a JsonArray.
     *
     * @param key
     * A key string.
     * @return A JsonArray which is the value.
     */
    fun optJsonArray(key: String): JsonArray {
        val o = this.opt(key)
        return if (o is JsonArray) o else JsonArray()
    }

    /**
     * Get an optional JsonObject associated with a key. It returns null if
     * there is no such key, or if its value is not a JsonObject.
     *
     * @param key
     * A key string.
     * @return A JsonObject which is the value.
     */
    fun optJsonObject(key: String): JsonObject {
        val value = this.opt(key)
        return if (value is JsonObject) value else JsonObject()
    }

    /**
     * Get an optional long value associated with a key, or zero if there is no
     * such key or if the value is not a number. If the value is a string, an
     * attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @return An object which is the value.
     */
    fun optLong(key: String): Long {
        return this.optLong(key, 0)
    }

    /**
     * Get an optional long value associated with a key, or the default if there
     * is no such key or if the value is not a number. If the value is a string,
     * an attempt will be made to evaluate it as a number.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return An object which is the value.
     */
    fun optLong(key: String, defaultValue: Long): Long {
        try {
            return this.getLong(key)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get an optional string associated with a key. It returns an empty string
     * if there is no such key. If the value is not a string and is not null,
     * then it is converted to a string.
     *
     * @param key
     * A key string.
     * @return A string which is the value.
     */
    fun optString(key: String): String {
        return this.optString(key, "")
    }

    /**
     * Get an optional string associated with a key. It returns the defaultValue
     * if there is no such key.
     *
     * @param key
     * A key string.
     * @param defaultValue
     * The default.
     * @return A string which is the value.
     */
    fun optString(key: String, defaultValue: String): String {
        val value = this.opt(key)
        return if (NULL.equals(value)) defaultValue else value.toString()
    }

    private fun populateMap(bean: Any) {
        val klass = bean::class.java
// If klass is a System class then set includeSuperClass to false.
        val includeSuperClass = klass.getClassLoader() != null
        val methods = if (includeSuperClass) klass.getMethods() else klass.getDeclaredMethods()
        var i = 0
        while (i < methods.size) {
            try {
                val method = methods[i]
                if (Modifier.isPublic(method.getModifiers())) {
                    val name = method.getName()
                    var key = ""
                    if (name.startsWith("get")) {
                        if ("getClass".equals(name) || "getDeclaringClass".equals(name)) {
                            key = ""
                        } else {
                            key = name.substring(3)
                        }
                    } else if (name.startsWith("is")) {
                        key = name.substring(2)
                    }
                    if (key.length > 0 && Character.isUpperCase(key.get(0)) && method.getParameterTypes().size == 0) {
                        if (key.length == 1) {
                            key = key.toLowerCase()
                        } else if (!Character.isUpperCase(key.get(1))) {
                            key = key.substring(0, 1).toLowerCase() + key.substring(1)
                        }
                        val result = method.invoke(bean, null)
                        if (result != null) {
                            this.map.put(key, wrap(result))
                        }
                    }
                }
            } catch (ignore: Exception) {
            }
            i += 1
        }
    }

    /**
     * Put a key/boolean pair in the JsonObject.
     *
     * @param key
     * A key string.
     * @param value
     * A boolean which is the value.
     * @return this.
     * @throws JsonException
     * If the key is null.
     */
    @Throws(JsonException::class)
    fun put(key: String, value: Boolean): JsonObject {
        this.put(key, value)
        return this
    }

    /**
     * Put a key/value pair in the JsonObject, where the value will be a
     * JsonArray which is produced from a Collection.
     *
     * @param key
     * A key string.
     * @param value
     * A Collection value.
     * @return this.
     * @throws JsonException
     */
    @Throws(JsonException::class)
    fun put(key: String, value: Collection<Any>): JsonObject {
        this.put(key, JsonArray(value))
        return this
    }

    /**
     * Put a key/double pair in the JsonObject.
     *
     * @param key
     * A key string.
     * @param value
     * A double which is the value.
     * @return this.
     * @throws JsonException
     * If the key is null or if the number is invalid.
     */
    @Throws(JsonException::class)
    fun put(key: String, value: Double): JsonObject {
        this.put(key, value)
        return this
    }

    /**
     * Put a key/int pair in the JsonObject.
     *
     * @param key
     * A key string.
     * @param value
     * An int which is the value.
     * @return this.
     * @throws JsonException
     * If the key is null.
     */
    @Throws(JsonException::class)
    fun put(key: String, value: Int): JsonObject {
        this.put(key, value)
        return this
    }

    /**
     * Put a key/long pair in the JsonObject.
     *
     * @param key
     * A key string.
     * @param value
     * A long which is the value.
     * @return this.
     * @throws JsonException
     * If the key is null.
     */
    @Throws(JsonException::class)
    fun put(key: String, value: Long): JsonObject {
        this.put(key, value)
        return this
    }

    /**
     * Put a key/value pair in the JsonObject, where the value will be a
     * JsonObject which is produced from a Map.
     *
     * @param key
     * A key string.
     * @param value
     * A Map value.
     * @return this.
     * @throws JsonException
     */
    @Throws(JsonException::class)
    fun put(key: String, value: Map<String, Any>): JsonObject {
        this.put(key, JsonObject(value))
        return this
    }

    /**
     * Put a key/value pair in the JsonObject. If the value is null, then the
     * key will be removed from the JsonObject if it is present.
     *
     * @param key
     * A key string.
     * @param value
     * An object which is the value. It should be of one of these
     * types: Boolean, Double, Integer, JsonArray, JsonObject, Long,
     * String, or the JsonObject.NULL object.
     * @return this.
     * @throws JsonException
     * If the value is non-finite number or if the key is null.
     */
    @Throws(JsonException::class)
    fun put(key: String, value: Any): JsonObject {
        if (value != NULL) {
            testValidity(value)
            this.map.put(key, value)
        } else {
            this.remove(key)
        }
        return this
    }

    /**
     * Put a key/value pair in the JsonObject, but only if the key and the value
     * are both non-null, and only if there is not already a member with that
     * name.
     *
     * @param key
     * @param value
     * @return his.
     * @throws JsonException
     * if the key is a duplicate
     */
    @Throws(JsonException::class)
    fun putOnce(key: String, value: Any): JsonObject {
        if (this.opt(key) != NULL) {
            throw JsonException("Duplicate key \"" + key + "\"")
        }
        this.put(key, value)
        return this
    }

    /**
     * Put a key/value pair in the JsonObject, but only if the key and the value
     * are both non-null.
     *
     * @param key
     * A key string.
     * @param value
     * An object which is the value. It should be of one of these
     * types: Boolean, Double, Integer, JsonArray, JsonObject, Long,
     * String, or the JsonObject.NULL object.
     * @return this.
     * @throws JsonException
     * If the value is a non-finite number.
     */
    @Throws(JsonException::class)
    fun putOpt(key: String, value: Any): JsonObject {
        this.put(key, value)
        return this
    }

    /**
     * Remove a name and its value, if present.
     *
     * @param key
     * The name to be removed.
     * @return The value that was associated with the name, or null if there was
     * no value.
     */
    fun remove(key: String): Any {
        return this.map.remove(key) ?: NULL
    }

    /**
     * Produce a JsonArray containing the values of the members of this
     * JsonObject.
     *
     * @param names
     * A JsonArray containing a list of key strings. This determines
     * the sequence of the values in the result.
     * @return A JsonArray of values.
     * @throws JsonException
     * If any of the values are non-finite numbers.
     */
    @Throws(JsonException::class)
    fun toJsonArray(names: JsonArray): JsonArray {
        if (names.length() == 0) {
            return JsonArray()
        }
        val ja = JsonArray()
        var i = 0
        while (i < names.length()) {
            ja.put(this.opt(names.getString(i)))
            i += 1
        }
        return ja
    }

    /**
     * Make a JSON text of this JsonObject. For compactness, no whitespace is
     * added. If this would not result in a syntactically correct JSON text,
     * then null will be returned instead.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable representation
     * of the object, beginning with <code>{</code>&nbsp;<small>(left
     * brace)</small> and ending with <code>}</code>&nbsp;<small>(right
     * brace)</small>.
     */
    override fun toString(): String {
        try {
            return this.toString(0)
        } catch (e: Exception) {
            return super.toString()
        }
    }

    /**
     * Make a prettyprinted JSON text of this JsonObject.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor
     * The number of spaces to add to each level of indentation.
     * @return a printable, displayable, portable, transmittable representation
     * of the object, beginning with <code>{</code>&nbsp;<small>(left
     * brace)</small> and ending with <code>}</code>&nbsp;<small>(right
     * brace)</small>.
     * @throws JsonException
     * If the object contains an invalid number.
     */
    @Throws(JsonException::class)
    fun toString(indentFactor: Int): String {
        val w = StringWriter()
        synchronized(w.getBuffer()) {
            return this.write(w, indentFactor, 0).toString()
        }
    }

    /**
     * Write the contents of the JsonObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JsonException
     */
    @Throws(JsonException::class)
    fun write(writer: Writer): Writer {
        return this.write(writer, 0, 0)
    }

    /**
     * Write the contents of the JsonObject as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return The writer.
     * @throws JsonException
     */
    @Throws(JsonException::class)
    internal fun write(writer: Writer, indentFactor: Int, indent: Int): Writer {
        try {
            var commanate = false
            val length = this.length()
            val keys = this.keys()
            writer.write('{'.toInt())
            if (length == 1) {
                val key = keys.next()
                writer.write(quote(key.toString()))
                writer.write(':'.toInt())
                if (indentFactor > 0) {
                    writer.write(' '.toInt())
                }
                writeValue(writer, this.map.get(key) ?: NULL, indentFactor, indent)
            } else if (length != 0) {
                val newindent = indent + indentFactor
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (commanate) {
                        writer.write(','.toInt())
                    }
                    if (indentFactor > 0) {
                        writer.write('\n'.toInt())
                    }
                    indent(writer, newindent)
                    writer.write(quote(key.toString()))
                    writer.write(':'.toInt())
                    if (indentFactor > 0) {
                        writer.write(' '.toInt())
                    }
                    writeValue(writer, this.map.get(key) ?: NULL, indentFactor, newindent)
                    commanate = true
                }
                if (indentFactor > 0) {
                    writer.write('\n'.toInt())
                }
                indent(writer, indent)
            }
            writer.write('}'.toInt())
            return writer
        } catch (exception: IOException) {
            throw JsonException(exception)
        }
    }

    companion object {
        /**
         * It is sometimes more convenient and less ambiguous to have a
         * <code>NULL</code> object than to use Java's <code>null</code> value.
         * <code>JsonObject.NULL.equals(null)</code> returns <code>true</code>.
         * <code>JsonObject.NULL.toString()</code> returns <code>"null"</code>.
         */
        val NULL: Any = Null()

        /**
         * Produce a string from a double. The string "null" will be returned if the
         * number is not finite.
         *
         * @param d
         * A double.
         * @return A String.
         */
        fun doubleToString(d: Double): String {
            if (d.isInfinite() || d.isNaN()) {
                return "null"
            }
// Shave off trailing zeros and decimal point, if possible.
            var string = d.toString()
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length - 1)
                }
                if (string.endsWith(".")) {
                    string = string.substring(0, string.length - 1)
                }
            }
            return string
        }

        /**
         * Get an array of field names from a JsonObject.
         *
         * @return An array of field names, or null if there are no names.
         */
        fun getNames(jo: JsonObject): Array<String> {
            val length = jo.length()
            if (length == 0) {
                return emptyArray<String>()
            }
            val iterator = jo.keys()
            val names = Array<String>(length, { _ -> "" })
            var i = 0
            while (iterator.hasNext()) {
                names[i] = iterator.next()
                i += 1
            }
            return names
        }

        /**
         * Get an array of field names from an Any.
         *
         * @return An array of field names, or null if there are no names.
         */
        fun getNames(value: Any): Array<String> {
            val klass = value::class.java
            val fields = klass.getFields()
            val length = fields.size
            if (length == 0) {
                return emptyArray<String>()
            }
            val names = Array<String>(length, { _ -> "" })
            var i = 0
            while (i < length) {
                names[i] = fields[i].getName()
                i += 1
            }
            return names
        }

        /**
         * Produce a string from a Number.
         *
         * @param number
         * A Number
         * @return A String.
         * @throws JsonException
         * If n is a non-finite number.
         */
        @Throws(JsonException::class)
        fun numberToString(number: Number): String {
            testValidity(number)
// Shave off trailing zeros and decimal point, if possible.
            var string = number.toString()
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length - 1)
                }
                if (string.endsWith(".")) {
                    string = string.substring(0, string.length - 1)
                }
            }
            return string
        }

        /**
         * Produce a string in double quotes with backslash sequences in all the
         * right places. A backslash will be inserted within </, producing <\/,
         * allowing JSON text to be delivered in HTML. In JSON text, a string cannot
         * contain a control character or an unescaped quote or backslash.
         *
         * @param string
         * A String
         * @return A String correctly formatted for insertion in a JSON text.
         */
        fun quote(string: String): String {
            val sw = StringWriter()
            synchronized(sw.getBuffer()) {
                try {
                    return quote(string, sw).toString()
                } catch (ignored: IOException) {
// will never happen - we are writing to a string writer
                    return ""
                }
            }
        }

        @Throws(IOException::class)
        fun quote(string: String, w: Writer): Writer {
            if (string.length == 0) {
                w.write("\"\"")
                return w
            }
            var b: Char
            var c: Char = 0.toChar()
            var hhhh: String
            var i: Int
            val len = string.length
            w.write('"'.toInt())
            i = 0
            while (i < len) {
                b = c
                c = string.get(i)
                when (c) {
                    '\\', '"' -> {
                        w.write('\\'.toInt())
                        w.write(c.toInt())
                    }
                    '/' -> {
                        if (b == '<') {
                            w.write('\\'.toInt())
                        }
                        w.write(c.toInt())
                    }
                    '\b' -> w.write("\\b")
                    '\t' -> w.write("\\t")
                    '\n' -> w.write("\\n")
                    '\u000C' -> w.write("\\f")
                    '\r' -> w.write("\\r")
                    else -> if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                        hhhh = "000" + c.toInt().toString(16)
                        w.write("\\u" + hhhh.substring(hhhh.length - 4))
                    } else {
                        w.write(c.toInt())
                    }
                }
                i += 1
            }
            w.write('"'.toInt())
            return w
        }

        /**
         * Try to convert a string into a number, boolean, or null. If the string
         * can't be converted, return the string.
         *
         * @param string
         * A String.
         * @return A simple JSON value.
         */
        fun stringToValue(string: String): Any {
            var d: Double
            if (string.equals("")) {
                return string
            }
            if (string.equals("true", true)) {
                return true
            }
            if (string.equals("false", true)) {
                return false
            }
            if (string.equals("null", true)) {
                return JsonObject.NULL
            }
/*
* If it might be a number, try converting it. If a number cannot be
* produced, then the value will just be a string. Note that the plus
* and implied string conventions are non-standard. A JSON parser may
* accept non-JSON forms as long as it accepts all correct JSON forms.
*/
            val b = string.get(0)
            if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
                try {
                    if (string.indexOf('.') > -1 || string.indexOf('e') > -1 || string.indexOf('E') > -1) {
                        d = string.toDouble()
                        if (!d.isInfinite() && !d.isNaN()) {
                            return d
                        }
                    } else {
                        val myLong = string.toLong()
                        if (myLong == myLong.toInt().toLong()) {
                            return myLong.toInt()
                        } else {
                            return myLong
                        }
                    }
                } catch (ignore: Exception) {
                }
            }
            return string
        }

        /**
         * Throw an exception if the object is a NaN or infinite number.
         *
         * @param o
         * The object to test.
         * @throws JsonException
         * If o is a non-finite number.
         */
        @Throws(JsonException::class)
        fun testValidity(o: Any) {
            if (o is Double) {
                if (o.isInfinite() || o.isNaN()) {
                    throw JsonException("JSON does not allow non-finite numbers.")
                }
            } else if (o is Float) {
                if (o.isInfinite() || o.isNaN()) {
                    throw JsonException("JSON does not allow non-finite numbers.")
                }
            }
        }

        /**
         * Make a JSON text of an Any value. If the object has an
         * value.toJsonString() method, then that method will be used to produce the
         * JSON text. The method is required to produce a strictly conforming text.
         * If the object does not contain a toJsonString method (which is the most
         * common case), then a text will be produced by other means. If the value
         * is an array or Collection, then a JsonArray will be made from it and its
         * toJsonString method will be called. If the value is a MAP, then a
         * JsonObject will be made from it and its toJsonString method will be
         * called. Otherwise, the value's toString method will be called, and the
         * result will be quoted.
         *
         * <p>
         * Warning: This method assumes that the data structure is acyclical.
         *
         * @param value
         * The value to be serialized.
         * @return a printable, displayable, transmittable representation of the
         * object, beginning with <code>{</code>&nbsp;<small>(left
         * brace)</small> and ending with <code>}</code>&nbsp;<small>(right
         * brace)</small>.
         * @throws JsonException
         * If the value is or contains an invalid number.
         */
        @SuppressWarnings("unchecked")
        @Throws(JsonException::class)
        fun valueToString(value: Any): String {
            if (value is JsonString) {
                val objectValue: Any
                try {
                    objectValue = value.toJsonString()
                } catch (e: Exception) {
                    throw JsonException(e)
                }
                return objectValue
            }
            if (value is Number) {
                return numberToString(value)
            }
            if (value is Boolean || value is JsonObject || value is JsonArray) {
                return value.toString()
            }
            if (value is Map<*, *>) {
                return JsonObject(value).toString()
            }
            if (value is Collection<*>) {
                return JsonArray(value).toString()
            }
            if (value::class.java.isArray()) {
                return JsonArray(value).toString()
            }
            return quote(value.toString())
        }

        /**
         * Wrap an object, if necessary. If the object is null, return the NULL
         * object. If it is an array or collection, wrap it in a JsonArray. If it is
         * a map, wrap it in a JsonObject. If it is a standard property (Double,
         * String, et al) then it is already wrapped. Otherwise, if it comes from
         * one of the java packages, turn it into a string. And if it doesn't, try
         * to wrap it in a JsonObject. If the wrapping fails, then null is returned.
         *
         * @param value
         * The object to wrap
         * @return The wrapped value
         */
        @SuppressWarnings("unchecked")
        fun wrap(value: Any): Any {
            try {
                if ((value is JsonObject || value is JsonArray || NULL.equals(value) || value is JsonString || value is Byte || value is Char
                                || value is Short || value is Int || value is Long || value is Boolean || value is Float || value is Double
                                || value is String)) {
                    return value
                }
                if (value is Collection<*>) {
                    return JsonArray(value)
                }
                if (value::class.java.isArray()) {
                    return JsonArray(value)
                }
                if (value is Map<*, *>) {
                    return JsonObject(value)
                }
                val objectPackage = value::class.java.getPackage()
                val objectPackageName = if (objectPackage != null) objectPackage.getName() else ""
                if (objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || value::class.java.getClassLoader() == null) {
                    return value.toString()
                }
                return JsonObject(value)
            } catch (exception: Exception) {
                return NULL
            }
        }

        @SuppressWarnings("unchecked")
        @Throws(JsonException::class, IOException::class)
        internal fun writeValue(writer: Writer, value: Any, indentFactor: Int, indent: Int): Writer {
            if (value is JsonObject) {
                value.write(writer, indentFactor, indent)
            } else if (value is JsonArray) {
                value.write(writer, indentFactor, indent)
            } else if (value is Map<*, *>) {
                JsonObject(value).write(writer, indentFactor, indent)
            } else if (value is Collection<*>) {
                JsonArray(value).write(writer, indentFactor, indent)
            } else if (value::class.java.isArray()) {
                JsonArray(value).write(writer, indentFactor, indent)
            } else if (value is Number) {
                writer.write(numberToString(value))
            } else if (value is Boolean) {
                writer.write(value.toString())
            } else if (value is JsonString) {
                val o: String
                try {
                    o = value.toJsonString()
                } catch (e: Exception) {
                    throw JsonException(e)
                }
                writer.write(if (o.isNotBlank()) o else quote(value.toString()))
            } else {
                quote(value.toString(), writer)
            }
            return writer
        }

        @Throws(IOException::class)
        internal fun indent(writer: Writer, indent: Int) {
            var i = 0
            while (i < indent) {
                writer.write(' '.toInt())
                i += 1
            }
        }
    }
}