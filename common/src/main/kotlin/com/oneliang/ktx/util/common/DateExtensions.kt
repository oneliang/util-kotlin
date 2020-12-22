package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.toDuration

fun Date.toFormatString(format: String = Constants.Time.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, locale: Locale = Locale.getDefault()): String {
    val simpleDateFormat = SimpleDateFormat(format, locale)
    return simpleDateFormat.format(this)
}

fun Long.getZeroTime(modulusTime: Long): Long {
    val currentTime = this//time zone is zero +0000
    val timeZoneMilliSecondOffset = TimeZoneUtil.getTimeZoneMilliSecondOffset()
    val currentTimeZoneTime = currentTime + timeZoneMilliSecondOffset
    val retainTime = currentTimeZoneTime % modulusTime//current time zone time
    return currentTimeZoneTime - retainTime - timeZoneMilliSecondOffset//recovery to 0 time zone
}

fun Long.getDayZeroTime(): Long {
    return this.getZeroTime(Constants.Time.MILLISECONDS_OF_DAY)
}

fun Long.getHourZeroTime(): Long {
    return this.getZeroTime(Constants.Time.MILLISECONDS_OF_HOUR)
}

fun Long.getMinuteZeroTime(): Long {
    return this.getZeroTime(Constants.Time.MILLISECONDS_OF_MINUTE)
}

fun Long.getDayZeroTimeNext(offset: Int = 1): Long {
    return this.getDayZeroTime() + offset * Constants.Time.MILLISECONDS_OF_DAY
}

fun Long.getHourZeroTimeNext(offset: Int = 1): Long {
    return this.getHourZeroTime() + offset * Constants.Time.MILLISECONDS_OF_HOUR
}

fun Long.getMinuteZeroTimeNext(offset: Int = 1): Long {
    return this.getMinuteZeroTime() + offset * Constants.Time.MILLISECONDS_OF_MINUTE
}

fun Long.getDayZeroTimePrevious(offset: Int = 1): Long {
    return this.getDayZeroTime() - offset * Constants.Time.MILLISECONDS_OF_DAY
}

fun Long.getHourZeroTimePrevious(offset: Int = 1): Long {
    return this.getHourZeroTime() - offset * Constants.Time.MILLISECONDS_OF_HOUR
}

fun Long.getMinuteZeroTimePrevious(offset: Int = 1): Long {
    return this.getMinuteZeroTime() - offset * Constants.Time.MILLISECONDS_OF_MINUTE
}

fun Date.getZeroTime(modulusTime: Long): Long {
    return this.time.getZeroTime(modulusTime)
}

fun Date.getDayZeroTime(): Long {
    return this.getZeroTime(Constants.Time.MILLISECONDS_OF_DAY)
}

fun Date.getHourZeroTime(): Long {
    return this.getZeroTime(Constants.Time.MILLISECONDS_OF_HOUR)
}

fun Date.getMinuteZeroTime(): Long {
    return this.getZeroTime(Constants.Time.MILLISECONDS_OF_MINUTE)
}

fun Date.getDayZeroTimeNext(offset: Int = 1): Long {
    return this.getDayZeroTime() + offset * Constants.Time.MILLISECONDS_OF_DAY
}

fun Date.getHourZeroTimeNext(offset: Int = 1): Long {
    return this.getHourZeroTime() + offset * Constants.Time.MILLISECONDS_OF_HOUR
}

fun Date.getMinuteZeroTimeNext(offset: Int = 1): Long {
    return this.getMinuteZeroTime() + offset * Constants.Time.MILLISECONDS_OF_MINUTE
}

fun Date.getDayZeroTimePrevious(offset: Int = 1): Long {
    return this.getDayZeroTime() - offset * Constants.Time.MILLISECONDS_OF_DAY
}

fun Date.getHourZeroTimePrevious(offset: Int = 1): Long {
    return this.getHourZeroTime() - offset * Constants.Time.MILLISECONDS_OF_HOUR
}

fun Date.getMinuteZeroTimePrevious(offset: Int = 1): Long {
    return this.getMinuteZeroTime() - offset * Constants.Time.MILLISECONDS_OF_MINUTE
}

fun Date.getDayOfMonth(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.DAY_OF_MONTH)
}