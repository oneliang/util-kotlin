package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.util.*

object TimerUtil {

    fun generateTimerStartTime(hour: Int, minute: Int, second: Int, dayOffset: Int = 1): Date {
        val currentTime = System.currentTimeMillis()//time zone is zero
        val todayZeroTime = currentTime.getDayZeroTime()//time zone is zero
        val currentTimeZoneTodayTime = currentTime - todayZeroTime//current time zone
        //start time every day's time zone is current time zone
        val startTimeEveryDay = hour * Constants.Time.MILLISECONDS_OF_HOUR + minute * Constants.Time.MILLISECONDS_OF_MINUTE + second * Constants.Time.MILLISECONDS_OF_SECOND
        //time zone is zero
        return Date(
            if (currentTimeZoneTodayTime > startTimeEveryDay) {
                todayZeroTime + startTimeEveryDay + dayOffset * Constants.Time.MILLISECONDS_OF_DAY//next day time
            } else {
                todayZeroTime + startTimeEveryDay
            }
        )
    }
}