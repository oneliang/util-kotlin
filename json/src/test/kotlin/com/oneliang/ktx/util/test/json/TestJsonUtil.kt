package com.oneliang.ktx.util.test.json

import com.oneliang.ktx.util.common.*
import com.oneliang.ktx.util.json.*
import java.util.*


fun main() {
//    val testJson = "{b:'',g:'',d:'{}'}"
//    val testJsonBean = testJson.jsonToObject(JsonBean::class, ignoreFieldNameArray = arrayOf("d"))
//    println(testJsonBean.toJson())
    val jsonBean1 = JsonBean()
    println(JsonUtil.objectToJson(jsonBean1,extendValueMap = mapOf("inner" to jsonBean1)))

    return
    val jsonBean1Json = jsonBean1.toJson()
    println("to json:$jsonBean1Json")
    println("from json:" + jsonBean1Json.jsonToObject(JsonBean::class).toJson())
    val string = "{\"productTypeCode\":\"STEEL\",\"optionArray\":[{\"key\":\"STEEL_P_SURFACE_PROCESS_TYPE\",\"value\":\"SB砂\"}]}"
    val aString = "{\"count\":2,\"orderNumberSet\":[\"20200302092031074000\",\"20200302151531057000\"],\"totalPrice\":55184.28,\"totalWeight\":6.924,\"orderCount\":2}"
    val a = arrayOf(arrayOf("a"))
    val testString = JsonBean().also {
//        it.b = "{\"aa\":\"\"b\"}"
    }
    val testStringJson = testString.toJson()
    println(testStringJson)
//    println(testStringJson.jsonToObject(JsonBean::class).b)
    return
    println(a.toJson())
    println(string.jsonToMap())
    val bb = KotlinClassUtil.changeType(Array<Array<String>>::class, arrayOf("[[\"a\"]]"), classProcessor = JsonUtil.DEFAULT_JSON_KOTLIN_CLASS_PROCESSOR)
    println(bb!![0][0])
    val map = listOf(mapOf("a" to arrayOf("1", "2")))
    println(map.toJson())
    println(Date(0))
    println("2020-01-01 00:00:00".toUtilDate().getDayZeroTime().toUtilDate())
    println(Date().getDayZeroTime())
    println(Date().getDayZeroTimeNext().toUtilDate())
    val z = BooleanArray(1) { false }
    z.toJson()
    return
    val instance = string.jsonToObject(TestBean::class)
    println(JsonUtil.objectToJson(instance, emptyArray()))
    val array = mutableListOf<JsonSubBean>()
    array += JsonSubBean()
    println(array.toTypedArray().javaClass)
    val json = JsonUtil.objectToJson(JsonBean().apply {
//        b = "first line\r\nsecond line"
    }, emptyArray())
    println("json:$json")
    val jsonBean = json.jsonToObject(JsonBean::class)
    println(JsonUtil.objectToJson(jsonBean, emptyArray()))
}