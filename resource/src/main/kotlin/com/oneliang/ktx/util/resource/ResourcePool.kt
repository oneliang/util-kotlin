package com.oneliang.ktx.util.resource

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

/**
 * class Pool,resource pool
 *
 * @author Dandelion
 * @since 2011-09-19
 */
abstract class ResourcePool<T : Any> : Runnable {
    companion object {
        internal val logger = LoggerManager.getLogger(ResourcePool::class)
    }

    var resourcePoolName: String = Constants.String.BLANK
    var minResourceSize = 1
    var maxResourceSize = 1
    var maxStableResourceSize = 1
    var resourceAliveTime: Long = 0
    var threadSleepTime = 5 * Constants.Time.MILLISECONDS_OF_MINUTE
    var resourceStatusArray = emptyArray<ResourceStatus<T>?>()

    private lateinit var resourceSource: ResourceSource<T>

    @Volatile
    private var hasBeenInitialized = false

    private var resourceCurrentSize = 0
    private var thread: Thread? = null
    private val initializeLock = ReentrantLock()
    private val resourceLock = ReentrantLock()
    private val stableResourceLock = ReentrantLock()
    private val stableResourceStatusMap = ConcurrentHashMap<Int, StableResourceStatus<T>>()

    private fun getStableResourceStatusKey(): Int {
        val currentThreadHashCode = Thread.currentThread().hashCode()
        return currentThreadHashCode % maxStableResourceSize
    }

    val stableResource: T
        get() {
            if (!this.hasBeenInitialized) {
                this.initialize()
            }
            val stableResource: T
            try {
                this.stableResourceLock.lock()
                logger.info("resource pool name:%s, stable resource current size:%s, stable resource map:%s", this.resourcePoolName, this.stableResourceStatusMap.size, this.stableResourceStatusMap)
                val stableResourceStatusKey = getStableResourceStatusKey()
                val stableResourceStatus = this.stableResourceStatusMap.getOrPut(stableResourceStatusKey) {
                    val resource = this.resourceSource.resource
                    StableResourceStatus(resource).apply {
                        this.lastNotInUseTime = 0L//System.currentTimeMillis()//initialize the not in use time
                    }
                }
                stableResource = stableResourceStatus.resource
            } finally {
                this.stableResourceLock.unlock()
            }
            return stableResource
        }

    /**
     * get resource from resource pool
     * @return T
     * @throws Exception
     */
    //true prove if have the resource which have not in use
    //current size > 0 prove the resource pool have the resource
    open val resource: T
        @Throws(ResourcePoolException::class)
        get() {
            if (!this.hasBeenInitialized) {
                initialize()
            }
            var resource: T? = null
            try {
                this.resourceLock.lock()
                logger.info("resource pool name:%s, resource current size:%s", this.resourcePoolName, this.resourceCurrentSize)
                if (this.resourceCurrentSize > 0) {
                    for (resourceStatus in this.resourceStatusArray) {
                        if (resourceStatus == null || resourceStatus.inUse) {
                            continue
                        }
                        resource = resourceStatus.resource
                        resourceStatus.inUse = true
                        break
                    }
                }
                if (resource != null) {
                    return resource
                }
                if (this.resourceCurrentSize < this.maxResourceSize) {
                    var index = 0
                    for (resourceStatus in this.resourceStatusArray) {
                        if (resourceStatus != null) {
                            index++//next resource status
                            continue
                        }
                        resource = this.resourceSource.resource
                        val oneStatus: ResourceStatus<T> = ResourceStatus(resource)
                        oneStatus.inUse = true
                        this.resourceStatusArray[index] = oneStatus
                        this.resourceCurrentSize++
                        break
                    }
                } else {
                    throw ResourcePoolException("The resource pool is max,current:" + this.resourceCurrentSize)
                }
            } finally {
                this.resourceLock.unlock()
            }
            if (resource == null) {
                throw ResourcePoolException("resource can not be null")
            }
            return resource
        }

    /**
     * initialize
     */
    open fun initialize() {
        if (this.hasBeenInitialized) {
            return
        }
        try {
            this.initializeLock.lock()
            if (this.hasBeenInitialized) {//double check
                return//return will trigger finally, but use unlock safety
            }
            this.resourceStatusArray = arrayOfNulls<ResourceStatus<T>?>(this.maxResourceSize)
            for (i in 0 until this.minResourceSize) {
                val resource = this.resourceSource.resource
                val resourceStatus = ResourceStatus(resource)
                this.resourceStatusArray[i] = resourceStatus
                this.resourceCurrentSize++
            }
            if (this.maxStableResourceSize == 0) this.maxStableResourceSize = 1
            this.thread = Thread(this).apply { start() }
        } finally {
            this.hasBeenInitialized = true
            this.initializeLock.unlock()
        }
    }

    open fun releaseStableResource(stableResource: T?, destroy: Boolean = false) {
        if (stableResource == null) {
            return
        }
        val stableResourceStatusKey = getStableResourceStatusKey()
        if (this.stableResourceStatusMap.containsKey(stableResourceStatusKey)) {
            val stableResourceStatus = this.stableResourceStatusMap[stableResourceStatusKey]
            if (stableResourceStatus != null && stableResource == stableResourceStatus.resource) {
                if (destroy) {
                    try {
                        this.stableResourceLock.lock()
                        realDestroyStableResource(stableResourceStatusKey, stableResource)
                    } catch (e: Throwable) {
                        logger.error(Constants.String.EXCEPTION, e)
                    } finally {
                        this.stableResourceLock.unlock()
                    }
                } else {
                    stableResourceStatus.lastNotInUseTime = System.currentTimeMillis()
                }
            } else {
                logger.error("release stable resource, stable resource status is null or stable resource is not the same, stable resource status:%s, stable resource:%s", stableResourceStatus ?: Constants.String.NULL, stableResourceStatus?.resource ?: Constants.String.NULL)
            }
        } else {
            logger.error("release stable resource, this stable resource maybe haven't got from method named stableResource ? getStableResource and releaseStableResource maybe not in same thread, or stableResource had been cleaned,  Stable resource:%s", stableResource)
        }
    }

    /**
     * release resource to pool, only concurrent for different resource
     * @param resource
     */
    open fun releaseResource(resource: T?, destroy: Boolean = false) {
        if (resource == null) {
            return
        }
        for ((index, resourceStatus) in this.resourceStatusArray.withIndex()) {
            if (resourceStatus == null) {
                continue
            }
            //find the resource and set in use false
            if (resource == resourceStatus.resource) {
                if (destroy) {
                    try {
                        this.resourceLock.lock()
                        realDestroyResource(index, resource)
                    } catch (e: Throwable) {
                        logger.error(Constants.String.EXCEPTION, e)
                    } finally {
                        this.resourceLock.unlock()
                    }
                } else {
                    resourceStatus.inUse = false
                    resourceStatus.lastNotInUseTime = System.currentTimeMillis()
                }
                break
            }
        }
    }

    /**
     * destroy resource
     * @param resource
     * @throws ResourcePoolException
     */
    @Throws(ResourcePoolException::class)
    protected abstract fun destroyResource(resource: T?)

    /**
     * clean the timeout resource
     * @throws Exception
     */
    open fun clean() {
        try {
            this.resourceLock.lock()
            for ((index, resourceStatus) in this.resourceStatusArray.withIndex()) {
                if (resourceStatus == null || resourceStatus.inUse) {
                    continue
                }
                val lastTime = resourceStatus.lastNotInUseTime
                val currentTime = System.currentTimeMillis()
                val resource = resourceStatus.resource
                if (currentTime - lastTime >= this.resourceAliveTime) {
                    realDestroyResource(index, resource)
                }
            }
        } finally {
            this.resourceLock.unlock()
        }
        //stable resource
        try {
            this.stableResourceLock.lock()
            this.stableResourceStatusMap.forEach { (stableResourceKey: Int, stableResourceStatus: StableResourceStatus<T>) ->
                val lastTime = stableResourceStatus.lastNotInUseTime
                val currentTime = System.currentTimeMillis()
                val resource = stableResourceStatus.resource
                if (currentTime - lastTime >= this.resourceAliveTime) {
                    realDestroyStableResource(stableResourceKey, resource)
                }
            }
        } finally {
            this.stableResourceLock.unlock()
        }
    }

    /**
     * must lock before use this method
     */
    private fun realDestroyResource(index: Int, resource: T?) {
        try {
            destroyResource(resource)
        } catch (e: Throwable) {
            logger.error(Constants.String.EXCEPTION, e)
        }
        this.resourceStatusArray[index] = null
        this.resourceCurrentSize--
    }

    /**
     * must lock before use this method
     */
    private fun realDestroyStableResource(stableResourceKey: Int, resource: T?) {
        try {
            destroyResource(resource)
        } catch (e: Throwable) {
            logger.error(Constants.String.EXCEPTION, e)
        } finally {
            this.stableResourceStatusMap.remove(stableResourceKey)
        }
    }

    /**
     * thread run
     */
    override fun run() {
        while (!Thread.currentThread().isInterrupted) {
            try {
                Thread.sleep(threadSleepTime)
                logger.debug("--The resource pool is:'%s', before clean resources number:%s, stable resource number:%s", this.resourcePoolName, this.resourceCurrentSize, this.stableResourceStatusMap.size)
                this.clean()
                logger.debug("--The resource pool is:'%s', after clean resources number:%s, stable resource number:%s", this.resourcePoolName, this.resourceCurrentSize, this.stableResourceStatusMap.size)
            } catch (e: InterruptedException) {
                logger.debug("need to interrupt:" + e.message)
                Thread.currentThread().interrupt()
            } catch (e: Throwable) {
                logger.error(Constants.String.EXCEPTION, e)
            }
        }
    }

    fun setResourceSource(resourceSource: ResourceSource<T>) {
        this.resourceSource = resourceSource
    }

    /**
     * destroy
     */
    fun destroy() {
        clean()
        if (this.thread != null) {
            this.thread?.interrupt()
            this.thread = null
            this.hasBeenInitialized = false
        }
    }
}