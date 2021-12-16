package com.oneliang.ktx.util.concurrent.atomic

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class AtomicDataContainer<K : Any, V> {

    private val lock = ReentrantLock()
    private val map = ConcurrentHashMap<K, V>()
    private val reentrantLockMap = ConcurrentHashMap<K, ReentrantLock>()

    operator fun set(key: K, block: () -> V): V? {
        var reentrantLock = this.reentrantLockMap[key]
        return if (reentrantLock != null) {
            doLockBlock(key, reentrantLock, block)
        } else {
            try {
                this.lock.lock()
                reentrantLock = this.reentrantLockMap[key]
                if (reentrantLock == null) {
                    reentrantLock = ReentrantLock()
                    this.reentrantLockMap[key] = reentrantLock
                }
                doLockBlock(key, reentrantLock, block)
            } catch (e: Throwable) {
                null
            } finally {
                this.lock.unlock()
            }
        }
    }

    operator fun get(key: K): V? {
        val reentrantLock = this.reentrantLockMap[key] ?: return null
        try {
            reentrantLock.lock()
            return this.map[key]
        } finally {
            reentrantLock.unlock()
        }
    }

    fun remove(key: K): V? {
        val reentrantLock = this.reentrantLockMap[key] ?: return null
        try {
            reentrantLock.lock()
            this.reentrantLockMap.remove(key)
            return this.map.remove(key)
        } finally {
            reentrantLock.unlock()
        }
    }

    private fun doLockBlock(key: K, reentrantLock: ReentrantLock, block: () -> V): V? {
        val result: V?
        try {
            reentrantLock.lock()
            val blockResult = block()
            this.map[key] = blockResult
            result = blockResult
        } finally {
            reentrantLock.unlock()
        }
        return result
    }
}