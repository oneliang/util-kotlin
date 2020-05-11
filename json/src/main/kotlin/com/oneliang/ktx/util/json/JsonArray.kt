package com.oneliang.ktx.util.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Array

class JsonArray() {
    /**
     * The arrayList where the JsonArray's properties are kept.
     */
    private val myArrayList = mutableListOf<Any>()

    /**
     * Construct an empty JsonArray.
     */
//    constructor() {
//        this.myArrayList = ArrayList<Any>()
//    }

    /**
     * Construct a JsonArray from a JsonTokener.
     * @param x A JsonTokener
     * @throws JsonException If there is a syntax error.
     */
    @Throws(JsonException::class)
    constructor(x: JsonTokener) : this() {
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JsonArray text must start with '['")
        }
        if (x.nextClean() != ']') {
            x.back()
            while (true) {
                if (x.nextClean() == ',') {
                    x.back()
                    this.myArrayList.add(JsonObject.NULL)
                } else {
                    x.back()
                    this.myArrayList.add(x.nextValue())
                }
                when (x.nextClean()) {
                    ';', ',' -> {
                        if (x.nextClean() == ']') {
                            return
                        }
                        x.back()
                    }
                    ']' -> return
                    else -> throw x.syntaxError("Expected a ',' or ']'")
                }
            }
        }
    }

    /**
     * Construct a JsonArray from a source JSON text.
     * @param source A string that begins with
     * <code>[</code>&nbsp;<small>(left bracket)</small>
     * and ends with <code>]</code>&nbsp;<small>(right bracket)</small>.
     * @throws JsonException If there is a syntax error.
     */
    @Throws(JsonException::class)
    constructor(source: String) : this(JsonTokener(source)) {
    }

    /**
     * Construct a JsonArray from a Collection.
     * @param collection A Collection.
     */
    constructor(collection: Collection<Any>) : this() {
        val iter = collection.iterator()
        while (iter.hasNext()) {
            this.myArrayList.add(JsonObject.wrap(iter.next()))
        }
    }

    /**
     * Construct a JsonArray from an array
     * @throws JsonException If not an array.
     */
    @Throws(JsonException::class)
    constructor(array: Any) : this() {
        if (array::class.java.isArray()) {
            val length = Array.getLength(array)
            var i = 0
            while (i < length) {
                this.put(JsonObject.wrap(Array.get(array, i)))
                i += 1
            }
        } else {
            throw JsonException(
                    "JsonArray initial value should be a string or collection or array.")
        }
    }

    /**
     * Get the object value associated with an index.
     * @param index
     * The index must be between 0 and length() - 1.
     * @return An object value.
     * @throws JsonException If there is no value for the index.
     */
    @Throws(JsonException::class)
    fun get(index: Int): Any {
        val value = this.opt(index)
        if (value == JsonObject.NULL) {
            throw JsonException("JsonArray[" + index + "] not found.")
        }
        return value
    }

    /**
     * Get the boolean value associated with an index.
     * The string values "true" and "false" are converted to boolean.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The truth.
     * @throws JsonException If there is no value for the index or if the
     * value is not convertible to boolean.
     */
    @Throws(JsonException::class)
    fun getBoolean(index: Int): Boolean {
        val value = this.get(index)
        if ((value is Boolean && value.equals(false) || ((value is String && value.equals("false", true))))) {
            return false
        } else if ((value is Boolean && value.equals(true) || ((value is String && value.equals("true", true))))) {
            return true
        }
        throw JsonException("JsonArray[" + index + "] is not a boolean.")
    }

    /**
     * Get the double value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JsonException If the key is not found or if the value cannot
     * be converted to a number.
     */
    @Throws(JsonException::class)
    fun getDouble(index: Int): Double {
        val value = this.get(index)
        try {
            return if (value is Number)
                value.toDouble()
            else
                value.toString().toDouble()
        } catch (e: Exception) {
            throw JsonException(("JsonArray[" + index +
                    "] is not a number."))
        }
    }

    /**
     * Get the int value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JsonException If the key is not found or if the value is not a number.
     */
    @Throws(JsonException::class)
    fun getInt(index: Int): Int {
        val value = this.get(index)
        try {
            return if (value is Number)
                value.toInt()
            else
                value.toString().toInt()
        } catch (e: Exception) {
            throw JsonException(("JsonArray[" + index +
                    "] is not a number."))
        }
    }

    /**
     * Get the JsonArray associated with an index.
     * @param index The index must be between 0 and length() - 1.
     * @return A JsonArray value.
     * @throws JsonException If there is no value for the index. or if the
     * value is not a JsonArray
     */
    @Throws(JsonException::class)
    fun getJsonArray(index: Int): JsonArray {
        val value = this.get(index)
        if (value is JsonArray) {
            return value
        }
        throw JsonException(("JsonArray[" + index +
                "] is not a JsonArray."))
    }

    /**
     * Get the JsonObject associated with an index.
     * @param index subscript
     * @return A JsonObject value.
     * @throws JsonException If there is no value for the index or if the
     * value is not a JsonObject
     */
    @Throws(JsonException::class)
    fun getJsonObject(index: Int): JsonObject {
        val value = this.get(index)
        if (value is JsonObject) {
            return value
        }
        throw JsonException(("JsonArray[" + index +
                "] is not a JsonObject."))
    }

    /**
     * Get the long value associated with an index.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JsonException If the key is not found or if the value cannot
     * be converted to a number.
     */
    @Throws(JsonException::class)
    fun getLong(index: Int): Long {
        val value = this.get(index)
        try {
            return if (value is Number)
                value.toLong()
            else
                value.toString().toLong()
        } catch (e: Exception) {
            throw JsonException(("JsonArray[" + index +
                    "] is not a number."))
        }
    }

    /**
     * Get the string associated with an index.
     * @param index The index must be between 0 and length() - 1.
     * @return A string value.
     * @throws JsonException If there is no string value for the index.
     */
    @Throws(JsonException::class)
    fun getString(index: Int): String {
        val value = this.get(index)
        if (value is String) {
            return value
        }
        throw JsonException("JsonArray[" + index + "] not a string.")
    }

    /**
     * Determine if the value is null.
     * @param index The index must be between 0 and length() - 1.
     * @return true if the value at the index is null, or if there is no value.
     */
    fun isNull(index: Int): Boolean {
        return JsonObject.NULL.equals(this.opt(index))
    }

    /**
     * Make a string from the contents of this JsonArray. The
     * <code>separator</code> string is inserted between each element.
     * Warning: This method assumes that the data structure is acyclical.
     * @param separator A string that will be inserted between the elements.
     * @return a string.
     * @throws JsonException If the array contains an invalid number.
     */
    @Throws(JsonException::class)
    fun join(separator: String): String {
        val len = this.length()
        val sb = StringBuffer()
        var i = 0
        while (i < len) {
            if (i > 0) {
                sb.append(separator)
            }
            sb.append(JsonObject.valueToString(this.myArrayList.get(i)))
            i += 1
        }
        return sb.toString()
    }

    /**
     * Get the number of elements in the JsonArray, included nulls.
     *
     * @return The length (or size).
     */
    fun length(): Int {
        return this.myArrayList.size
    }

    /**
     * Get the optional object value associated with an index.
     * @param index The index must be between 0 and length() - 1.
     * @return An object value, or null if there is no
     * object at that index.
     */
    fun opt(index: Int): Any {
        return if ((index < 0 || index >= this.length()))
            JsonObject.NULL
        else
            this.myArrayList.get(index)
    }

    /**
     * Get the optional boolean value associated with an index.
     * It returns false if there is no value at that index,
     * or if the value is not Boolean.TRUE or the String "true".
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The truth.
     */
    fun optBoolean(index: Int): Boolean {
        return this.optBoolean(index, false)
    }

    /**
     * Get the optional boolean value associated with an index.
     * It returns the defaultValue if there is no value at that index or if
     * it is not a Boolean or the String "true" or "false" (case insensitive).
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue A boolean default.
     * @return The truth.
     */
    fun optBoolean(index: Int, defaultValue: Boolean): Boolean {
        try {
            return this.getBoolean(index)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get the optional double value associated with an index.
     * NaN is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    fun optDouble(index: Int): Double {
        return this.optDouble(index, Double.NaN)
    }

    /**
     * Get the optional double value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index subscript
     * @param defaultValue The default value.
     * @return The value.
     */
    fun optDouble(index: Int, defaultValue: Double): Double {
        try {
            return this.getDouble(index)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get the optional int value associated with an index.
     * Zero is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    fun optInt(index: Int): Int {
        return this.optInt(index, 0)
    }

    /**
     * Get the optional int value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    fun optInt(index: Int, defaultValue: Int): Int {
        try {
            return this.getInt(index)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get the optional JsonArray associated with an index.
     * @param index subscript
     * @return A JsonArray value, or null if the index has no value,
     * or if the value is not a JsonArray.
     */
    fun optJsonArray(index: Int): JsonArray {
        val o = this.opt(index)
        return if (o is JsonArray) o else JsonArray()
    }

    /**
     * Get the optional JsonObject associated with an index.
     * Null is returned if the key is not found, or null if the index has
     * no value, or if the value is not a JsonObject.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A JsonObject value.
     */
    fun optJsonObject(index: Int): JsonObject {
        val o = this.opt(index)
        return if (o is JsonObject) o else JsonObject()
    }

    /**
     * Get the optional long value associated with an index.
     * Zero is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return The value.
     */
    fun optLong(index: Int): Long {
        return this.optLong(index, 0)
    }

    /**
     * Get the optional long value associated with an index.
     * The defaultValue is returned if there is no value for the index,
     * or if the value is not a number and cannot be converted to a number.
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return The value.
     */
    fun optLong(index: Int, defaultValue: Long): Long {
        try {
            return this.getLong(index)
        } catch (e: Exception) {
            return defaultValue
        }
    }

    /**
     * Get the optional string value associated with an index. It returns an
     * empty string if there is no value at that index. If the value
     * is not a string and is not null, then it is coverted to a string.
     *
     * @param index The index must be between 0 and length() - 1.
     * @return A String value.
     */
    fun optString(index: Int): String {
        return this.optString(index, "")
    }

    /**
     * Get the optional string associated with an index.
     * The defaultValue is returned if the key is not found.
     *
     * @param index The index must be between 0 and length() - 1.
     * @param defaultValue The default value.
     * @return A String value.
     */
    fun optString(index: Int, defaultValue: String): String {
        val value = this.opt(index)
        return if (JsonObject.NULL.equals(value))
            defaultValue
        else
            value.toString()
    }

    /**
     * Append a boolean value. This increases the array's length by one.
     *
     * @param value A boolean value.
     * @return this.
     */
    fun put(value: Boolean): JsonArray {
        this.put(value)
        return this
    }

    /**
     * Put a value in the JsonArray, where the value will be a
     * JsonArray which is produced from a Collection.
     * @param value A Collection value.
     * @return this.
     */
    fun put(value: Collection<Any>): JsonArray {
        this.put(JsonArray(value))
        return this
    }

    /**
     * Append a double value. This increases the array's length by one.
     *
     * @param value A double value.
     * @throws JsonException if the value is not finite.
     * @return this.
     */
    @Throws(JsonException::class)
    fun put(value: Double): JsonArray {
        val d = value
        JsonObject.testValidity(d)
        this.put(d)
        return this
    }

    /**
     * Append an int value. This increases the array's length by one.
     *
     * @param value An int value.
     * @return this.
     */
    fun put(value: Int): JsonArray {
        this.put(Integer.valueOf(value))
        return this
    }

    /**
     * Append an long value. This increases the array's length by one.
     *
     * @param value A long value.
     * @return this.
     */
    fun put(value: Long): JsonArray {
        this.put(value)
        return this
    }

    /**
     * Put a value in the JsonArray, where the value will be a
     * JsonObject which is produced from a Map.
     * @param value A Map value.
     * @return this.
     */
    fun put(value: Map<String, Any>): JsonArray {
        this.put(JsonObject(value))
        return this
    }

    /**
     * Append an object value. This increases the array's length by one.
     * @param value An object value. The value should be a
     * Boolean, Double, Integer, JsonArray, JsonObject, Long, or String, or the
     * JsonObject.NULL object.
     * @return this.
     */
    fun put(value: Any): JsonArray {
        this.myArrayList.add(value)
        return this
    }

    /**
     * Put or replace a boolean value in the JsonArray. If the index is greater
     * than the length of the JsonArray, then null elements will be added as
     * necessary to pad it out.
     * @param index The subscript.
     * @param value A boolean value.
     * @return this.
     * @throws JsonException If the index is negative.
     */
    @Throws(JsonException::class)
    fun put(index: Int, value: Boolean): JsonArray {
        this.put(index, value)
        return this
    }

    /**
     * Put a value in the JsonArray, where the value will be a
     * JsonArray which is produced from a Collection.
     * @param index The subscript.
     * @param value A Collection value.
     * @return this.
     * @throws JsonException If the index is negative or if the value is
     * not finite.
     */
    @Throws(JsonException::class)
    fun put(index: Int, value: Collection<Any>): JsonArray {
        this.put(index, JsonArray(value))
        return this
    }

    /**
     * Put or replace a double value. If the index is greater than the length of
     * the JsonArray, then null elements will be added as necessary to pad
     * it out.
     * @param index The subscript.
     * @param value A double value.
     * @return this.
     * @throws JsonException If the index is negative or if the value is
     * not finite.
     */
    @Throws(JsonException::class)
    fun put(index: Int, value: Double): JsonArray {
        this.put(index, value)
        return this
    }

    /**
     * Put or replace an int value. If the index is greater than the length of
     * the JsonArray, then null elements will be added as necessary to pad
     * it out.
     * @param index The subscript.
     * @param value An int value.
     * @return this.
     * @throws JsonException If the index is negative.
     */
    @Throws(JsonException::class)
    fun put(index: Int, value: Int): JsonArray {
        this.put(index, Integer.valueOf(value))
        return this
    }

    /**
     * Put or replace a long value. If the index is greater than the length of
     * the JsonArray, then null elements will be added as necessary to pad
     * it out.
     * @param index The subscript.
     * @param value A long value.
     * @return this.
     * @throws JsonException If the index is negative.
     */
    @Throws(JsonException::class)
    fun put(index: Int, value: Long): JsonArray {
        this.put(index, value)
        return this
    }

    /**
     * Put a value in the JsonArray, where the value will be a
     * JsonObject that is produced from a Map.
     * @param index The subscript.
     * @param value The Map value.
     * @return this.
     * @throws JsonException If the index is negative or if the the value is
     * an invalid number.
     */
    @Throws(JsonException::class)
    fun put(index: Int, value: Map<String, Any>): JsonArray {
        this.put(index, JsonObject(value))
        return this
    }

    /**
     * Put or replace an object value in the JsonArray. If the index is greater
     * than the length of the JsonArray, then null elements will be added as
     * necessary to pad it out.
     * @param index The subscript.
     * @param value The value to put into the array. The value should be a
     * Boolean, Double, Integer, JsonArray, JsonObject, Long, or String, or the
     * JsonObject.NULL object.
     * @return this.
     * @throws JsonException If the index is negative or if the the value is
     * an invalid number.
     */
    @Throws(JsonException::class)
    fun put(index: Int, value: Any): JsonArray {
        JsonObject.testValidity(value)
        if (index < 0) {
            throw JsonException("JsonArray[" + index + "] not found.")
        }
        if (index < this.length()) {
            this.myArrayList.set(index, value)
        } else {
            while (index != this.length()) {
                this.put(JsonObject.NULL)
            }
            this.put(value)
        }
        return this
    }

    /**
     * Remove an index and close the hole.
     * @param index The index of the element to be removed.
     * @return The value that was associated with the index,
     * or null if there was no value.
     */
    fun remove(index: Int): Any {
        val o = this.opt(index)
        if (o != JsonObject.NULL) this.myArrayList.remove(index)
        return o
    }

    /**
     * Produce a JsonObject by combining a JsonArray of names with the values
     * of this JsonArray.
     * @param names A JsonArray containing a list of key strings. These will be
     * paired with the values.
     * @return A JsonObject, or null if there are no names or if this JsonArray
     * has no values.
     * @throws JsonException If any of the names are null.
     */
    @Throws(JsonException::class)
    fun toJsonObject(names: JsonArray): JsonObject {
        if (names.length() == 0 || this.length() == 0) {
            return JsonObject()
        }
        val jo = JsonObject()
        var i = 0
        while (i < names.length()) {
            jo.put(names.getString(i), this.opt(i))
            i += 1
        }
        return jo
    }

    /**
     * Make a JSON text of this JsonArray. For compactness, no
     * unnecessary whitespace is added. If it is not possible to produce a
     * syntactically correct JSON text then null will be returned instead. This
     * could occur if the array contains an invalid number.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, transmittable
     * representation of the array.
     */
    override fun toString(): String {
        try {
            return ('[' + this.join(",") + ']')
        } catch (e: Exception) {
            return super.toString()
        }
    }

    /**
     * Make a prettyprinted JSON text of this JsonArray.
     * Warning: This method assumes that the data structure is acyclical.
     * @param indentFactor The number of spaces to add to each level of
     * indentation.
     * @return a printable, displayable, transmittable
     * representation of the object, beginning
     * with <code>[</code>&nbsp;<small>(left bracket)</small> and ending
     * with <code>]</code>&nbsp;<small>(right bracket)</small>.
     * @throws JsonException
     */
    @Throws(JsonException::class)
    fun toString(indentFactor: Int): String {
        val sw = StringWriter()
        synchronized(sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString()
        }
    }

    /**
     * Write the contents of the JsonArray as JSON text to a writer. For
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
     * Write the contents of the JsonArray as JSON text to a writer. For
     * compactness, no whitespace is added.
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param indentFactor
     * The number of spaces to add to each level of indentation.
     * @param indent
     * The indention of the top level.
     * @return The writer.
     * @throws JsonException
     */
    @Throws(JsonException::class)
    internal fun write(writer: Writer, indentFactor: Int, indent: Int): Writer {
        try {
            var commanate = false
            val length = this.length()
            writer.write('['.toInt())
            if (length == 1) {
                JsonObject.writeValue(writer, this.myArrayList.get(0),
                        indentFactor, indent)
            } else if (length != 0) {
                val newindent = indent + indentFactor
                var i = 0
                while (i < length) {
                    if (commanate) {
                        writer.write(','.toInt())
                    }
                    if (indentFactor > 0) {
                        writer.write('\n'.toInt())
                    }
                    JsonObject.indent(writer, newindent)
                    JsonObject.writeValue(writer, this.myArrayList.get(i),
                            indentFactor, newindent)
                    commanate = true
                    i += 1
                }
                if (indentFactor > 0) {
                    writer.write('\n'.toInt())
                }
                JsonObject.indent(writer, indent)
            }
            writer.write(']'.toInt())
            return writer
        } catch (e: IOException) {
            throw JsonException(e)
        }
    }
}