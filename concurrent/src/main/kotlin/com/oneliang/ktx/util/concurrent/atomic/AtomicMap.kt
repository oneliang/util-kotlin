package com.oneliang.ktx.util.concurrent.atomic

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

class AtomicMap<K : Any, V> constructor() : AbstractMap<K, V>() {
    private val lock = ReentrantLock()
    private val map = ConcurrentHashMap<K, AtomicReference<V>>()

    constructor(map: Map<K, V>) : this() {
        map.forEach { (key, value) ->
            this.map[key] = AtomicReference(value)
        }
    }

    override val entries: Set<Map.Entry<K, V>>
        get() = this.snapshot().entries

    fun operate(key: K, create: () -> V, update: (V) -> V): V? {
        if (this.map.containsKey(key)) {
            return atomicUpdate(key, update)
        } else {
            return try {
                this.lock.lock()
                if (this.map.containsKey(key)) {
                    atomicUpdate(key, update)
                } else {
                    val value = create()
                    this.map[key] = AtomicReference(value)
                    value
                }
            } finally {
                this.lock.unlock()
            }
        }
    }

    private fun <T> lockOperate(operate: () -> T): T {
        return try {
            this.lock.lock()
            operate()
        } finally {
            this.lock.unlock()
        }
    }

    fun remove(key: K): V? {
        return lockOperate {
            this.map.remove(key)?.get()
        }
    }

    fun clear() {
        this.lockOperate {
            this.map.clear()
        }
    }

    private fun atomicUpdate(key: K, update: (V) -> V): V? {
        val atomicReference = this.map[key]
        return atomicReference?.getAndUpdate { old ->
            update(old).apply {
                if (old.hashCode() != this.hashCode()) {
                    return@apply
                }
                error("must create a new object after update.the object has not changed")
            }
        }
    }

    fun snapshot(): Map<K, V> {
        val snapshotMap = ConcurrentHashMap<K, V>()
        map.forEach { (key: K, atomicReference: AtomicReference<V>) ->
            snapshotMap[key] = atomicReference.get()
        }
        return snapshotMap
    }
}