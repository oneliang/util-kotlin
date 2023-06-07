package com.oneliang.ktx.util.common

class TimeRecord(
    private val startTimeProvider: () -> Long,
    private val stopTimeProvider: () -> Long,
    private val recordCallback: (category: String, recordTime: Long) -> Unit = { _, _ -> }
) {
    enum class Category {
        START, STEP_RECORD, RECORD
    }

    private var beginTime = 0L
    private var stepBeginTime = 0L

    fun start() {
        this.beginTime = this.startTimeProvider()
        this.stepBeginTime = this.beginTime
        this.recordCallback(Category.START.name, 0L)
    }

    fun stepRecord(): Long {
        val newStepBeginTime = this.stopTimeProvider()
        val stepRecordTime = newStepBeginTime - this.stepBeginTime
        this.stepBeginTime = newStepBeginTime
        this.recordCallback(Category.STEP_RECORD.name, stepRecordTime)
        return stepRecordTime
    }

    /**
     * @return Long, return cost time
     */
    fun record(): Long {
        val recordTime = this.stopTimeProvider() - this.beginTime
        this.recordCallback(Category.RECORD.name, recordTime)
        return recordTime
    }
}