package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.TimerUtil
import java.util.*

fun main() {
    val timer = Timer()

    val timerTask = object : TimerTask() {
        override fun run() {
            println("timer task running")
        }
    }
    println(TimerUtil.generateTimerStartTime(0, 0, 0, 0))
    timer.schedule(timerTask, TimerUtil.generateTimerStartTime(0, 0, 0, 0), 10 * Constants.Time.MILLISECONDS_OF_SECOND)
}