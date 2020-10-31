package com.oneliang.ktx.util.common

import java.math.BigDecimal

fun BigDecimal.addByBigDecimal(double: Double): BigDecimal {
    return this + double.toBigDecimal()
}

fun BigDecimal.minusByBigDecimal(double: Double): BigDecimal {
    return this - double.toBigDecimal()
}

fun BigDecimal.multiplyByBigDecimal(double: Double): BigDecimal {
    return this * double.toBigDecimal()
}

fun BigDecimal.divideByBigDecimal(double: Double): BigDecimal {
    return this / double.toBigDecimal()
}
