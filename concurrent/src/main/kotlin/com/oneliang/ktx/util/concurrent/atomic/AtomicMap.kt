package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.util.common.perform
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

class AtomicMap<K, V> constructor() : AbstractMap<K, V>() {
    private val lock = ReentrantLock()
    private val map = ConcurrentHashMap<K, AtomicReference<V>>()

    constructor(map: Map<K, V>) : this() {
        map.forEach { (key, value) ->
            this.map[key] = AtomicReference(value)
        }
    }

    override val entries: Set<Map.Entry<K, V>>
        get() = this.snapshot().entries

    fun operate(key: K, create: () -> V, update: (V) -> V) {
        if (this.map.containsKey(key)) {
            atomicUpdate(key, update)
        } else {
            perform({
                this.lock.lock()
                if (this.map.containsKey(key)) {
                    atomicUpdate(key, update)
                } else {
                    this.map[key] = AtomicReference(create())
                }
            }, finally = {
                lock.unlock()
            })
        }
    }

    private fun atomicUpdate(key: K, update: (V) -> V) {
        val atomicReference = this.map[key]
        atomicReference?.getAndUpdate { old ->
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