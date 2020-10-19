package com.oneliang.ktx.util.common

import java.math.BigDecimal

fun Double.addByBigDecimal(double: Double): BigDecimal {
    return this.toBigDecimal().plus(double.toBigDecimal())
}

fun Double.addByBigDecimal(bigDecimal: BigDecimal): BigDecimal {
    return this.toBigDecimal().plus(bigDecimal)
}

fun Double.minusByBigDecimal(double: Double): BigDecimal {
    return this.toBigDecimal().minus(double.toBigDecimal())
}

fun Double.minusByBigDecimal(bigDecimal: BigDecimal): BigDecimal {
    return this.toBigDecimal().minus(bigDecimal)
}

fun Double.multiplyByBigDecimal(double: Double): BigDecimal {
    return this.toBigDecimal().times(double.toBigDecimal())
}

fun Double.multiplyByBigDecimal(bigDecimal: BigDecimal): BigDecimal {
    return this.toBigDecimal().times(bigDecimal)
}