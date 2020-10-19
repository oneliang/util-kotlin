package com.oneliang.ktx.util.common

import java.math.BigDecimal

fun BigDecimal.addByBigDecimal(double: Double): BigDecimal {
    return this.plus(double.toBigDecimal())
}

fun BigDecimal.minusByBigDecimal(double: Double): BigDecimal {
    return this.minus(double.toBigDecimal())
}

fun BigDecimal.multiplyByBigDecimal(double: Double): BigDecimal {
    return this.times(double.toBigDecimal())
}
