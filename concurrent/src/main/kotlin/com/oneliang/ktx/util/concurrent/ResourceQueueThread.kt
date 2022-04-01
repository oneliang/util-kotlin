package com.oneliang.ktx.util.concurrent

import com.oneliang.ktx.util.logging.LoggerManager
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * it will process all resources which are in queue when stop
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
    private var needToStop = false
    private val lock = Object()

    @Throws(Throwable::class)
    override fun looping() {
        if (!this.resourceQueue.isEmpty()) {
            val resource = this.resourceQueue.poll()
            this.resourceProcessor.process(resource)
        } else {
            synchronized(lock) {
                // check for the scene which notify first,so do it in synchronized block
                if (this.needToStop) {
                    this.realStop()
                }
                lock.wait()
            }
        }
    }

    /**
     * stop when process all resource
     */
    override fun stop() {
        this.needToStop = true
        synchronized(lock) {
            lock.notify()
        }
    }

    /**
     * stop immediately, will not process all resource
     */
    fun stopNow() {
        realStop()
    }

    /**
     * real stop
     */
    private fun realStop() {
        super.stop()
        this.needToStop = false
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
        this.stop()
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