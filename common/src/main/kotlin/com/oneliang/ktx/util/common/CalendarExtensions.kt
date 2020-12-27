package com.oneliang.ktx.util.common

import java.util.*

fun Calendar.toUtilDate(): Date {
    return this.time
}

fun Calendar.getMonth(offset: Int = 0): Calendar {
    this.add(Calendar.MONTH, offset);
    return this
}

fun Calendar.getFirstDayOfMonth(offset: Int = 0): Calendar {
    this.add(Calendar.MONTH, offset);
    this.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
    this.set(Calendar.HOUR_OF_DAY, 0)
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    return this
}

fun Calendar.getDayCountOfMonth(): Int {
    this[Calendar.DATE] = 1
    this.roll(Calendar.DATE, -1)
    return this[Calendar.DATE]
}

fun Calendar.getCurrentDayOfMonth(): Int {
    return this[Calendar.DAY_OF_MONTH]
}

fun main() {
    println(Calendar.getInstance().getFirstDayOfMonth(-1).time)
    println(Calendar.getInstance().getDayCountOfMonth())
    println(Calendar.getInstance().getCurrentDayOfMonth())
}