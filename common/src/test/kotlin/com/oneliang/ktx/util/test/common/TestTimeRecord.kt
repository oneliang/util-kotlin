package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.TimeRecord

fun main() {
    val timeRecord = TimeRecord(System::nanoTime, System::nanoTime)
    timeRecord.start()
    Thread.sleep(1000)
    print(timeRecord.stop())
}