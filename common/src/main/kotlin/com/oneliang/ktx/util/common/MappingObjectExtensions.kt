package com.oneliang.ktx.util.common

import kotlin.reflect.KClass

fun <T : Any> T.isMappingObject(): Boolean = MappingObjectUtil.isMappingObject(this)

fun <T : Any> T.initializeMappingObject(mappingKeyVisitor: (mappingKey: String, fieldType: KClass<*>) -> Any?) = MappingObjectUtil.initializeMappingObject(this, mappingKeyVisitor)

fun <T : Any> T.operateMappingObject(mappingKeyVisitor: (mappingKey: String, fieldValue: Any?) -> Unit) = MappingObjectUtil.operateMappingObject(this, mappingKeyVisitor)