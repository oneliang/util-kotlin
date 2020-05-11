package com.oneliang.ktx.util.common

fun Char.isDigit(): Boolean = this in '0'..'9'

fun Char.isLowerCaseLetter(): Boolean = this in 'a'..'z'

fun Char.isUpperCaseLetter(): Boolean = this in 'A'..'Z'