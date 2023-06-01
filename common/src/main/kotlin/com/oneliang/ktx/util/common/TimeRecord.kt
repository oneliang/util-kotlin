package com.oneliang.ktx.util.common

class TimeRecord(private val startTimeProvider: () -> Long, private val stopTimeProvider: () -> Long) {

    private var beginTime = 0L

    fun start() {
        this.beginTime = this.startTimeProvider()
    }

    /**
     * @return Long, return cost time
     */
    fun stop(): Long {
        return this.stopTimeProvider() - this.beginTime
    }
}