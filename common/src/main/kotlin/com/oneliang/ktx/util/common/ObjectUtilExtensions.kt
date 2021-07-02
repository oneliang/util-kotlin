package com.oneliang.ktx.util.common

import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KClass

fun <T : Any> T.getterOrIsMethodInvoke(fieldName: String, ignoreFirstLetterCase: Boolean = false) = ObjectUtil.getterOrIsMethodInvoke(this, fieldName, ignoreFirstLetterCase)

fun <T : Any> T.isEntity(clazz: Class<*>) = ObjectUtil.isEntity(this, clazz)

fun <T : Any> T.isEntity(kClass: KClass<*>) = ObjectUtil.isEntity(this, kClass)

fun <T : java.io.Serializable> InputStream.readObject(): T? = ObjectUtil.readObject(this)

fun <T : java.io.Serializable> OutputStream.writeObject(instance: T) = ObjectUtil.writeObject(this, instance)

fun <T : Any> Class<T>.isInterfaceImplement(interfaceClass: Class<*>) = ObjectUtil.isInterfaceImplement(this, interfaceClass)