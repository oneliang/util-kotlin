package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.util.*

object TimerUtil {

    fun generateTimerStartTime(hour: Int, minute: Int, second: Int): Date {
        val currentTime = System.currentTimeMillis()
        val currentTimeZoneTime = currentTime + TimeZoneUtil.getTimeZoneMilliSecondOffset()
        val currentTimeZoneTodayTime = currentTimeZoneTime % Constants.Time.MILLISECONDS_OF_DAY//current time zone
        //start time every day's time zone is current time zone
        val startTimeEveryDay = hour * Constants.Time.MILLISECONDS_OF_HOUR + minute * Constants.Time.MILLISECONDS_OF_MINUTE + second * Constants.Time.MILLISECONDS_OF_SECOND
        val todayZeroTime = currentTime - currentTimeZoneTodayTime//time zone is zero
        //time zone is zero
        return Date(if (currentTimeZoneTodayTime > startTimeEveryDay) {
            todayZeroTime + startTimeEveryDay + Constants.Time.MILLISECONDS_OF_DAY//next day time
        } else {
            todayZeroTime + startTimeEveryDay
        })
    }
}