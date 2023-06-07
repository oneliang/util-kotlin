package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.TimeRecord

fun main() {
    val timeRecord = TimeRecord(System::nanoTime, System::nanoTime) { category, recordTime ->
        println("%s, cost:%s".format(category, recordTime))
    }
    timeRecord.start()
    Thread.sleep(1000)
    timeRecord.stepRecord()
    timeRecord.record()
}