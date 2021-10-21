package com.oneliang.ktx.util.concurrent.atomic

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

class AtomicMap<K : Any, V> constructor(private val maxSize: Int = 0) : AbstractMap<K, V>() {
    private val lock = ReentrantLock()
    private val map = ConcurrentHashMap<K, AtomicReference<V>>()

    constructor(map: Map<K, V>, maxSize: Int = 0) : this(maxSize) {
        map.forEach { (key, value) ->
            this.map[key] = AtomicReference(value)
        }
    }

    override val entries: Set<Map.Entry<K, V>>
        get() = this.snapshot().entries

    fun operate(key: K, create: () -> V, update: ((V) -> V)? = null, removeWhenFull: (() -> K)? = null): V? {
        val createAndSetToMap: () -> V = {
            val value = create()
            this.map[key] = AtomicReference(value)
            value
        }
        if (this.map.containsKey(key)) {//update when exists
            return atomicUpdate(key, update)
        } else {//create
            return this.lockOperate {
                if (this.map.containsKey(key)) {
                    atomicUpdate(key, update)
                } else {//check size
                    val size = this.map.size
                    if (this.maxSize <= 0 || size < this.maxSize) {
                        createAndSetToMap()
                    } else {
                        if (removeWhenFull != null) {
                            val removeKey = removeWhenFull()
                            this.map.remove(removeKey)
                            createAndSetToMap()
                        } else {
                            //error
                            error("map is full, please implement block parameter(removeWhenMax), current size:%s, max size:%s".format(size, this.maxSize))
                        }
                    }
                }
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

    private fun atomicUpdate(key: K, update: ((V) -> V)?): V? {
        val atomicReference = this.map[key]
        return if (update == null) {
            atomicReference?.get()
        } else {
            atomicReference?.updateAndGet { old ->
                update(old).apply {
                    if (old.hashCode() != this.hashCode()) {
                        return@apply
                    }
                    error("must create a new object after update.the object has not changed")
                }
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