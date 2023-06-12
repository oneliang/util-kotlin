package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants

class TimeRecord(
    private val startTimeProvider: () -> Long,
    private val stopTimeProvider: () -> Long,
    private val recordCallback: (category: String, recordTime: Long, stepKey: String) -> Unit = { _, _, _ -> }
) {
    enum class Category {
        START, STEP_RECORD, RECORD
    }

    private var beginTime = 0L
    private var stepBeginTime = 0L

    fun start() {
        this.beginTime = this.startTimeProvider()
        this.stepBeginTime = this.beginTime
        this.recordCallback(Category.START.name, 0L, Constants.String.BLANK)
    }

    /**
     * @param stepKey
     * @return Long, return cost time
     */
    fun stepRecord(stepKey: String = Constants.String.BLANK): Long {
        val newStepBeginTime = this.stopTimeProvider()
        val stepRecordTime = newStepBeginTime - this.stepBeginTime
        this.stepBeginTime = newStepBeginTime
        this.recordCallback(Category.STEP_RECORD.name, stepRecordTime, stepKey)
        return stepRecordTime
    }

    /**
     * @return Long, return cost time
     */
    fun record(): Long {
        val recordTime = this.stopTimeProvider() - this.beginTime
        this.recordCallback(Category.RECORD.name, recordTime, Constants.String.BLANK)
        return recordTime
    }
}