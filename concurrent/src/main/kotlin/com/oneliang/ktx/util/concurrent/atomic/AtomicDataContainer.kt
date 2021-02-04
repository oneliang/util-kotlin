package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.util.common.perform
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class AtomicDataContainer<K : Any, V> {

    private val lock = ReentrantLock()
    private val map = ConcurrentHashMap<K, V>()
    private val reentrantLockMap = ConcurrentHashMap<K, ReentrantLock>()

    operator fun set(key: K, block: () -> V): V? {
        return if (this.reentrantLockMap.containsKey(key)) {
            val reentrantLock = this.reentrantLockMap[key] ?: error("not found ReentrantLock for key:$key, may be something error")
            doLockBlock(key, reentrantLock, block)
        } else {
            perform({
                this.lock.lock()
                if (!this.reentrantLockMap.containsKey(key)) {
                    this.reentrantLockMap[key] = ReentrantLock()
                }
                val reentrantLock = this.reentrantLockMap[key] ?: error("not found ReentrantLock for key:$key, may be something error")
                doLockBlock(key, reentrantLock, block)
            }, failure = {
                null
            }, finally = {
                this.lock.unlock()
            })
        }
    }

    operator fun get(key: K): V? {
        val reentrantLock = this.reentrantLockMap[key] ?: return null
        perform({
            reentrantLock.lock()
            return this.map[key]
        }, finally = {
            reentrantLock.unlock()
        })
        return null
    }

    private fun doLockBlock(key: K, reentrantLock: ReentrantLock, block: () -> V): V? {
        var result: V? = null
        perform({
            reentrantLock.lock()
            val blockResult = block()
            this.map[key] = blockResult
            result = blockResult
        }, finally = {
            reentrantLock.unlock()
        })
        return result
    }
}