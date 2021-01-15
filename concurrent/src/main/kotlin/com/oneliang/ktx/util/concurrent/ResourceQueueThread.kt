package com.oneliang.ktx.util.concurrent

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * it will process all resources which are in queue when interrupt
 * ResourceQueueThread
 *
 * @param <T>
 */
class ResourceQueueThread<T>(private val resourceProcessor: ResourceProcessor<T>) : LoopThread() {
    companion object {
        private val logger = LoggerManager.getLogger(ResourceQueueThread::class)
    }

    // addResource() may do before the start(),so must initialize it in self
// instance initializing
    private val resourceQueue = ConcurrentLinkedQueue<T>()

    // always binding in self instance(ResourceQueueThread),finalize self
// instance will set null(release the resourceProcessor)
    @Volatile
    private var needToInterrupt = false
    private val lock = Object()

    @Throws(Throwable::class)
    override fun looping() {
        if (!this.resourceQueue.isEmpty()) {
            val resource = this.resourceQueue.poll()
            this.resourceProcessor.process(resource)
        } else {
            synchronized(lock) {
                // check for the scene which notify first,so do it in synchronized block
                if (this.needToInterrupt) {
                    this.realInterrupt()
                }
                lock.wait()
            }
        }
    }

    /**
     * interrupt
     */
    override fun interrupt() {
        this.needToInterrupt = true
        synchronized(lock) {
            lock.notify()
        }
    }

    /**
     * real interrupt
     */
    private fun realInterrupt() {
        super.interrupt()
        this.needToInterrupt = false
    }

    /**
     * @param resource
     * the resource to add
     */
    fun addResource(resource: T?) {
        if (resource != null) {
            this.resourceQueue.add(resource)
            synchronized(lock) {
                lock.notify()
            }
        }
    }

    /**
     * remove resource
     *
     * @param resource
     */
    fun removeResource(resource: T): Boolean {
        return this.resourceQueue.remove(resource)
    }

    /**
     * finalize
     */
    @Throws(Throwable::class)
    protected fun finalize() {
        this.interrupt()
    }

    interface ResourceProcessor<T> {
        /**
         * process the resource
         *
         * @param resource
         */
        fun process(resource: T)
    }
}