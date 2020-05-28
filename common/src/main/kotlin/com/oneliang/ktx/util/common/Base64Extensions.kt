package com.oneliang.ktx.util.common

fun String.toBase64String(): String = Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

fun String.base64StringToString(): String = String(Base64.decode(this, Base64.DEFAULT), Charsets.UTF_8)

fun ByteArray.toBase64(): ByteArray = Base64.encode(this, Base64.NO_WRAP)

fun ByteArray.base64ToByteArray(): ByteArray = Base64.decode(this, Base64.DEFAULT)