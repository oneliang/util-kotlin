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

fun Double.divideByBigDecimal(double: Double): BigDecimal {
    return this.toBigDecimal().div(double.toBigDecimal())
}

fun Double.divideByBigDecimal(bigDecimal: BigDecimal): BigDecimal {
    return this.toBigDecimal().div(bigDecimal)
}

fun Double.toLongBits(): Long {
    return java.lang.Double.doubleToLongBits(this)
}

fun Double.toRawLongBits(): Long {
    return java.lang.Double.doubleToRawLongBits(this)
}

fun Double.fixNaN(defaultValue: Double = 0.0): Double {
    return if (this.isNaN()) {
        defaultValue
    } else {
        this
    }
}

/**
 * @param value default zero, it is no need to round, and no decimal
 */
fun Double.roundToFix(value: Int = 0): String {
    return "%.${value}f".format(this)
}

fun Double.toByteArray(): ByteArray {
    return this.toLongBits().toByteArray()
}