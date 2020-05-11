package com.oneliang.ktx.util.json

interface JsonString {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON
     * serialization.
     *
     * @return A strictly syntactically correct JSON text.
     */
    fun toJsonString(): String
}