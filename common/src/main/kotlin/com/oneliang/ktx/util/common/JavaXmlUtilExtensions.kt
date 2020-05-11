package com.oneliang.ktx.util.common

import org.w3c.dom.Document
import java.io.File
import java.io.InputStream
import kotlin.reflect.KClass

fun <T : Any> String.xmlToObject(kClass: KClass<T>, mapping: Map<String, String>, classProcessor: KotlinClassUtil.KotlinClassProcessor = JavaXmlUtil.DEFAULT_CLASS_PROCESSOR): T = JavaXmlUtil.xmlToObject(this, kClass, mapping, classProcessor)

fun <T : Any> String.xmlToObjectList(xmlObjectTag: String, kClass: KClass<T>, mapping: Map<String, String>, classProcessor: KotlinClassUtil.KotlinClassProcessor = JavaXmlUtil.DEFAULT_CLASS_PROCESSOR): List<T> = JavaXmlUtil.xmlToObjectList(this, xmlObjectTag, kClass, mapping, classProcessor)

fun InputStream.parseXml(): Document = JavaXmlUtil.parse(this)

fun File.parseXml(): Document = JavaXmlUtil.parse(this)