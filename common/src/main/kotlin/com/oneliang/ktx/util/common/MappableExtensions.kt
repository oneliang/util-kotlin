package com.oneliang.ktx.util.common

import kotlin.reflect.KClass

fun <T : Any> T.isMappableObject(): Boolean = MappableUtil.isMappableObject(this)

fun <T : Any> T.pushToMappableObject(mappingKeyVisitor: (mappingKey: String, fieldType: KClass<*>) -> Any?) = MappableUtil.pushToMappableObject(this, mappingKeyVisitor)

fun <T : Any> T.pullFromMappableObject(mappingKeyVisitor: (mappingKey: String, fieldValue: Any?) -> Unit) = MappableUtil.pullFromMappableObject(this, mappingKeyVisitor)