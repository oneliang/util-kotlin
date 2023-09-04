package com.oneliang.ktx.util.concurrent.atomic

import com.oneliang.ktx.util.common.toMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class AtomicMap<K : Any, V> constructor(private val maxSize: Int = 0) : AbstractMap<K, V>() {
    private val operationLock = OperationLock()
    private val map = ConcurrentHashMap<K, AtomicReference<V>>()

    constructor(map: Map<K, V>, maxSize: Int = 0) : this(maxSize) {
        if (maxSize != 0 && map.size > maxSize) {
            error("max size is less than map.size")
        }
        map.forEach { (key, value) ->
            this.map[key] = AtomicReference(value)
        }
    }

    override val entries: Set<Map.Entry<K, V>>
        get() = this.snapshot().entries

    /**
     * operate
     * @param key
     * @param create
     * @param update
     * @param removeWhenFull
     * @return V?
     */
    fun operate(key: K, create: () -> V, update: ((V) -> V)? = null, removeWhenFull: (() -> K)? = null): V? {
        val createAndSetToMap: () -> V = {
            val value = create()
            this.map[key] = AtomicReference(value)
            value
        }
        if (this.map.containsKey(key)) {//update when exists
            return atomicUpdate(key, update)
        } else {//create
            return this.operationLock.operate {
                if (this.map.containsKey(key)) {
                    atomicUpdate(key, update)
                } else {//check size
                    val size = this.map.size
                    if (this.maxSize <= 0 || size < this.maxSize) {
                        createAndSetToMap()
                    } else {
                        if (removeWhenFull != null) {
                            val removeKey = removeWhenFull()
//                            println("what?[$removeKey],before:"+this.map.entries)//for debug
                            this.map.remove(removeKey) ?: error("map remove error, remove key:[$removeKey] not exists")
                            createAndSetToMap()
                        } else {
                            //error
                            error("map is full, please implement block parameter(removeWhenFull), current size:%s, max size:%s".format(size, this.maxSize))
                        }
                    }
                }
            }
        }
    }

    /**
     * remove
     * @param key
     * @return V?
     */
    fun remove(key: K): V? {
        return this.operationLock.operate {
            this.map.remove(key)?.get()
        }
    }

    /**
     * minus
     * @param key
     * @return V?
     */
    operator fun minus(key: K): V? {
        return remove(key)
    }

    /**
     * clear
     */
    fun clear() {
        this.operationLock.operate {
            this.map.clear()
        }
    }

    /**
     * atomic update
     * @param key
     * @param update
     * @return V?
     */
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

    /**
     * snapshot
     * @return Map<K, V>
     */
    fun snapshot(): Map<K, V> {
        return this.map.toMap(ConcurrentHashMap<K, V>()) { key: K, atomicReference: AtomicReference<V> ->
            key to atomicReference.get()
        }
    }
}