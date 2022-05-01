package com.oneliang.ktx.util.concurrent

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class ThreadPool : Runnable {
    companion object {
        private val logger = LoggerManager.getLogger(ThreadPool::class)
    }

    @Volatile
    private var hasBeenInitialized = false

    var minThreads = 0
    var maxThreads = 1
    private var totalTaskCount = 0
    private var currentTaskCount = 0
    private var allInnerThreads: Array<InnerThread?> = arrayOfNulls(0)
    private val threadTaskQueue = ConcurrentLinkedQueue<ThreadTask>()
    private var daemonThread: DaemonThread? = null
    private var thread: Thread? = null
    private var processor: Processor? = null
    private val lock = Object()

    private fun initialize() {
        this.daemonThread = DaemonThread()
        this.daemonThread?.start()
        this.allInnerThreads = arrayOfNulls(this.maxThreads)
        for (i in 0 until this.minThreads) {
            val innerThread = InnerThread(this)
            innerThread.start()
            this.allInnerThreads[i] = innerThread
            this.daemonThread?.addInnerThread(innerThread)
        }
        this.hasBeenInitialized = true
    }

    override fun run() {
        while (!Thread.currentThread().isInterrupted) {
            try {
                if (!this.threadTaskQueue.isEmpty()) {
                    var hasAllInnerThreadBusy = true
                    for ((index, innerThread) in this.allInnerThreads.withIndex()) {
                        if (innerThread != null && innerThread.isIdle) {
                            hasAllInnerThreadBusy = false
                            this.processor?.beforeRunTaskProcess(this.threadTaskQueue)
                            val threadTask = this.threadTaskQueue.poll()
                            if (threadTask != null) {
                                logger.verbose("old inner thread, thread task:%s", threadTask)
                                innerThread.setCurrentThreadTask(threadTask)
                            }
                        } else if (innerThread == null) {
                            hasAllInnerThreadBusy = false
                            this.processor?.beforeRunTaskProcess(this.threadTaskQueue)
                            val threadTask = this.threadTaskQueue.poll()
                            if (threadTask != null) {
                                logger.verbose("new inner thread, thread task:%s", threadTask)
                                val tempInnerThread = InnerThread(this)
                                tempInnerThread.setCurrentThreadTask(threadTask)
                                tempInnerThread.start()
                                this.allInnerThreads[index] = tempInnerThread
                                this.daemonThread?.addInnerThread(tempInnerThread)
                            }
                        }
                    }
                    if (hasAllInnerThreadBusy) {
                        synchronized(lock) {
                            logger.verbose("All inner thread busy,waiting")
                            lock.wait()
                        }
                        logger.verbose("after wait")
                    }
                } else {
                    synchronized(lock) {
                        logger.verbose("waiting")
                        lock.wait()
                    }
                    logger.verbose("after wait")
                }
            } catch (e: InterruptedException) {
                logger.verbose("Thread pool need to interrupt:" + e.message)
                Thread.currentThread().interrupt()
                break
            } catch (e: Exception) {
                logger.error(Constants.String.EXCEPTION, e)
            }

        }
    }

    /**
     * start
     */
    @Synchronized
    fun start() {
        if (this.thread == null) {
            this.thread = Thread(this)
            this.thread?.priority = Thread.NORM_PRIORITY
            this.thread?.start()
        }
        if (!this.hasBeenInitialized) {
            this.initialize()
        }
    }

    /**
     * stop, real stop
     */
    @Synchronized
    fun stop() {
        for ((i, innerThread) in this.allInnerThreads.withIndex()) {
            if (innerThread != null) {
                innerThread.stop()
                this.allInnerThreads[i] = null
            }
        }
        if (this.thread != null) {
            this.thread?.interrupt()
            this.thread = null
        }
        if (this.daemonThread != null) {
            this.daemonThread?.stop()
            this.daemonThread = null
        }
        this.threadTaskQueue.clear()
        this.hasBeenInitialized = false
    }

    /**
     * @param threadTask
     * the threadTask to add
     */
    fun addThreadTask(threadTask: ThreadTask) {
        this.threadTaskQueue.add(threadTask)
        synchronized(lock) {
            this.totalTaskCount++
            lock.notify()
        }
    }

    /**
     * @param threadTask
     * the threadTask to add
     */
    fun addThreadTask(threadTask: () -> Unit) {
        this.addThreadTask(threadTask, {}, {})
    }

    /**
     * @param threadTask
     * the threadTask to add
     */
    fun addThreadTask(threadTask: () -> Unit, failure: (t: Throwable) -> Unit = {}, finally: () -> Unit = {}) {
        this.addThreadTask(object : ThreadTask {
            override fun runTask() {
                try {
                    threadTask()
                } catch (t: Throwable) {
                    failure(t)
                } finally {
                    finally()
                }
            }
        })
    }

    /**
     * count current task
     */
    private fun countCurrentTask() {
        this.currentTaskCount++
    }

    /**
     * finalize
     */
    @Throws(Throwable::class)
    protected fun finalize() {
        this.stop()
    }

    private class InnerThread internal constructor(private val threadPool: ThreadPool) : Runnable {
        companion object {
            private val logger = LoggerManager.getLogger(InnerThread::class)
        }

        private var beginTimeMillis: Long = 0
        private var finishedTimeMillis: Long = 0
        private var currentThreadTask: ThreadTask? = null
        private val lock = Object()

        /**
         * @return the finishedCount
         */
        var finishedCount = 0
            private set

        /**
         * @return the executeCount
         */
        var executeCount = 0
            private set
        private var thread: Thread? = null

        /**
         * @return the idle
         */
        val isIdle: Boolean
            get() = this.currentThreadTask == null

        /**
         * @return the thread
         */
        val threadState: Thread.State
            get() {
                var state: Thread.State = Thread.State.TERMINATED
                if (this.thread != null) {
                    state = this.thread?.state ?: Thread.State.TERMINATED
                }
                return state
            }

        /**
         * start the thread to run task
         */
        @Synchronized
        fun start() {
            if (this.thread == null) {
                this.thread = Thread(this)
                this.thread?.priority = Thread.MIN_PRIORITY
                this.thread?.start()
            }
        }

        /**
         * stop
         */
        fun stop() {
            if (this.thread != null) {
                this.thread?.interrupt()
                this.thread = null
            }
        }

        /**
         * when thread is dead restart the thread
         */
        fun restart() {
            synchronized(this) {
                if (this.thread != null && this.thread?.state == Thread.State.TERMINATED) {
                    this.thread = Thread(this)
                    this.thread?.priority = Thread.MIN_PRIORITY
                    this.thread?.start()
                }
            }
        }

        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    synchronized(lock) {
                        var hasCurrentThreadTask = false
                        if (this.currentThreadTask != null) {
                            logger.verbose("$this--begin--")
                            this.beginTimeMillis = System.currentTimeMillis()
                            try {
                                this.currentThreadTask?.runTask()
                            } catch (t: Throwable) {
                                logger.error(Constants.String.EXCEPTION, t)
                            } finally {
                                this.finishedTimeMillis = System.currentTimeMillis()
                                this.finishedCount++
                                this.currentThreadTask = null
                                logger.verbose(this.toString() + "--end--cost:" + (this.finishedTimeMillis - this.beginTimeMillis))
                            }
                            hasCurrentThreadTask = true
                        }
                        synchronized(this.threadPool.lock) {
                            if (hasCurrentThreadTask) {
                                this.threadPool.countCurrentTask()
                            }
                            this.threadPool.lock.notify()
                        }
                        synchronized(lock) {
                            lock.wait()
                        }
                    }
                } catch (e: InterruptedException) {
                    logger.verbose("Inner thread need to interrupt:" + Thread.currentThread().name + ",message:" + e.message)
                    Thread.currentThread().interrupt()
                    break
                } catch (t: Throwable) {
                    logger.error(Constants.String.EXCEPTION, t)
                }
            }
        }

        /**
         * @param currentThreadTask
         * the currentThreadTask to set
         */
        fun setCurrentThreadTask(currentThreadTask: ThreadTask?) {
            synchronized(lock) {
                this.executeCount++
                this.currentThreadTask = currentThreadTask
                lock.notify()
            }
        }
    }

    private class DaemonThread : Runnable {

        companion object {
            private val logger = LoggerManager.getLogger(DaemonThread::class)
            private const val THREAD_WAIT_TIME: Long = 5000
        }

        private val innerThreadQueue = ConcurrentLinkedQueue<InnerThread>()
        private var thread: Thread? = null
        private val lock = Object()

        /**
         * start
         */
        @Synchronized
        fun start() {
            if (this.thread == null) {
                this.thread = Thread(this)
                this.thread?.priority = Thread.NORM_PRIORITY
                this.thread?.start()
            }
        }

        /**
         * stop
         */
        fun stop() {
            if (this.thread != null) {
                this.thread?.interrupt()
                this.thread = null
            }
        }

        override fun run() {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    if (!innerThreadQueue.isEmpty()) {
                        for (innerThread in innerThreadQueue) {
                            val state = innerThread.threadState
                            if (state == Thread.State.TERMINATED) {
                                innerThread.restart()
                            }
                            logger.verbose(innerThread.toString() + "---" + innerThread.isIdle + "---execute:" + innerThread.executeCount + "---finished:" + innerThread.finishedCount)
                        }
                        synchronized(lock) {
                            lock.wait(THREAD_WAIT_TIME)
                        }
                    } else {
                        synchronized(lock) {
                            lock.wait()
                        }
                    }
                } catch (e: InterruptedException) {
                    logger.verbose("Daemon thread need to interrupt:" + e.message)
                    Thread.currentThread().interrupt()
                    break
                } catch (e: Exception) {
                    logger.error(Constants.String.EXCEPTION, e)
                }

            }
        }

        fun addInnerThread(innerThread: InnerThread?) {
            if (innerThread != null) {
                this.innerThreadQueue.add(innerThread)
                synchronized(lock) {
                    lock.notify()
                }
            }
        }
    }

    /**
     * @param processor
     * the processor to set
     */
    fun setProcessor(processor: Processor) {
        this.processor = processor
    }

    interface Processor {
        /**
         * before run task
         *
         * @param threadTaskQueue
         */
        fun beforeRunTaskProcess(threadTaskQueue: Queue<ThreadTask>)
    }
}