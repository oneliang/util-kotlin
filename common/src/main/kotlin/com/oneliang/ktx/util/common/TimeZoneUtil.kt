package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.util.*

object TimeZoneUtil {
    fun getTimeZoneMilliSecondOffset(): Int {
        return TimeZone.getDefault().rawOffset
    }

    fun getTimeZoneInt(): Int {
        return (getTimeZoneMilliSecondOffset() / Constants.Time.MILLISECONDS_OF_HOUR).toInt()
    }
}