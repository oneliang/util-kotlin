package com.oneliang.ktx.util.math

fun Map<String, String>.operate(operateString: String) = Operator.operate(this, operateString)