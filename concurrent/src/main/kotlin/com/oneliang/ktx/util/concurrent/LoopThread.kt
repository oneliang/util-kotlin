package com.oneliang.ktx.util.concurrent

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager

abstract class LoopThread : Runnable {
    companion object {
        private val logger = LoggerManager.getLogger(LoopThread::class)
    }

    private var thread: Thread? = null

    override fun run() {
        while (!Thread.currentThread().isInterrupted) {
            try {
                looping()
            } catch (e: InterruptedException) {
                logger.verbose("server need to interrupt, message:%s, instance:%s", e.message, this)
                Thread.currentThread().interrupt()
                break
            } catch (e: Throwable) {
                logger.error(Constants.Base.EXCEPTION, e)
            }
        }
    }

    @Throws(Throwable::class)
    abstract fun looping()

    @Synchronized
    open fun start() {
        if (this.thread == null) {
            this.thread = Thread(this)
            this.thread?.start()
        }
    }

    @Synchronized
    open fun interrupt() {
        if (this.thread != null) {
            this.thread?.interrupt()
            this.thread = null
        }
    }
}