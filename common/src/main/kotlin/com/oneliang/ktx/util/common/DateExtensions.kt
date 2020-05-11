package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.toDuration

fun Date.toFormatString(format: String = Constants.Time.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, locale: Locale = Locale.getDefault()): String {
    val simpleDateFormat = SimpleDateFormat(format, locale)
    return simpleDateFormat.format(this)
}

fun Date.getZeroTime(modulusTime: Long): Long {
    val currentTime = this.time//time zone is zero +0000
    val timeZoneMilliSecondOffset = TimeZoneUtil.getTimeZoneMilliSecondOffset()
    val currentTimeZoneTime = currentTime + timeZoneMilliSecondOffset
    val retainTime = currentTimeZoneTime % modulusTime//current time zone time
    return currentTimeZoneTime - retainTime - timeZoneMilliSecondOffset//recovery to 0 time zone
}

fun Date.getDayZeroTime(): Long {
    return getZeroTime(Constants.Time.MILLISECONDS_OF_DAY)
}

fun Date.getHourZeroTime(): Long {
    return getZeroTime(Constants.Time.MILLISECONDS_OF_HOUR)
}

fun Date.getMinuteZeroTime(): Long {
    return getZeroTime(Constants.Time.MILLISECONDS_OF_MINUTE)
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