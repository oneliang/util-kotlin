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
    return currentTime - retainTime//recovery to 0 time zone
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

fun Long.getSecondZeroTime(): Long {
    return this.getZeroTime(Constants.Time.MILLISECONDS_OF_SECOND)
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

fun Long.getSecondZeroTimeNext(offset: Int = 1): Long {
    return this.getSecondZeroTime() + offset * Constants.Time.MILLISECONDS_OF_SECOND
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

fun Long.getSecondZeroTimePrevious(offset: Int = 1): Long {
    return this.getSecondZeroTime() - offset * Constants.Time.MILLISECONDS_OF_MINUTE
}

fun Date.getZeroTime(modulusTime: Long): Long {
    return this.time.getZeroTime(modulusTime)
}

fun Date.getDayZeroTime(): Long {
    return this.time.getDayZeroTime()
}

fun Date.getHourZeroTime(): Long {
    return this.time.getHourZeroTime()
}

fun Date.getMinuteZeroTime(): Long {
    return this.time.getMinuteZeroTime()
}

fun Date.getSecondZeroTime(): Long {
    return this.time.getSecondZeroTime()
}

fun Date.getDayZeroTimeNext(offset: Int = 1): Long {
    return this.time.getDayZeroTimeNext(offset)
}

fun Date.getHourZeroTimeNext(offset: Int = 1): Long {
    return this.time.getHourZeroTimeNext(offset)
}

fun Date.getMinuteZeroTimeNext(offset: Int = 1): Long {
    return this.time.getMinuteZeroTimeNext(offset)
}

fun Date.getSecondZeroTimeNext(offset: Int = 1): Long {
    return this.time.getSecondZeroTimeNext(offset)
}

fun Date.getDayZeroTimePrevious(offset: Int = 1): Long {
    return this.time.getDayZeroTimePrevious(offset)
}

fun Date.getHourZeroTimePrevious(offset: Int = 1): Long {
    return this.time.getHourZeroTimePrevious(offset)
}

fun Date.getMinuteZeroTimePrevious(offset: Int = 1): Long {
    return this.time.getMinuteZeroTimePrevious(offset)
}

fun Date.getSecondZeroTimePrevious(offset: Int = 1): Long {
    return this.time.getSecondZeroTimePrevious(offset)
}

fun Date.getZeroTimeDate(modulusTime: Long): Date {
    return this.getZeroTime(modulusTime).toUtilDate()
}

fun Date.getDayZeroTimeDate(): Date {
    return this.getZeroTimeDate(Constants.Time.MILLISECONDS_OF_DAY)
}

fun Date.getHourZeroTimeDate(): Date {
    return this.getZeroTimeDate(Constants.Time.MILLISECONDS_OF_HOUR)
}

fun Date.getMinuteZeroTimeDate(): Date {
    return this.getZeroTimeDate(Constants.Time.MILLISECONDS_OF_MINUTE)
}

fun Date.getSecondZeroTimeDate(): Date {
    return this.getZeroTimeDate(Constants.Time.MILLISECONDS_OF_SECOND)
}

fun Date.getDayZeroTimeDateNext(offset: Int = 1): Date {
    return this.getDayZeroTimeNext(offset).toUtilDate()
}

fun Date.getHourZeroTimeDateNext(offset: Int = 1): Date {
    return this.getHourZeroTimeNext(offset).toUtilDate()
}

fun Date.getMinuteZeroTimeDateNext(offset: Int = 1): Date {
    return this.getMinuteZeroTimeNext(offset).toUtilDate()
}

fun Date.getSecondZeroTimeDateNext(offset: Int = 1): Date {
    return this.getSecondZeroTimeNext(offset).toUtilDate()
}

fun Date.getDayZeroTimeDatePrevious(offset: Int = 1): Date {
    return this.getDayZeroTimePrevious(offset).toUtilDate()
}

fun Date.getHourZeroTimeDatePrevious(offset: Int = 1): Date {
    return this.getHourZeroTimePrevious(offset).toUtilDate()
}

fun Date.getMinuteZeroTimeDatePrevious(offset: Int = 1): Date {
    return this.getMinuteZeroTimePrevious(offset).toUtilDate()
}

fun Date.getSecondZeroTimeDatePrevious(offset: Int = 1): Date {
    return this.getSecondZeroTimePrevious(offset).toUtilDate()
}

fun Date.getCurrentDayOfMonth(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.getCurrentDayOfMonth()
}