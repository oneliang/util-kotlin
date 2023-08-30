package com.oneliang.ktx.util.concurrent.atomic

import java.util.concurrent.atomic.AtomicReference

class AtomicList<V> constructor(private val maxSize: Int = 0) : Iterable<V> {
    private val operationLock = OperationLock()
    private val mutableList = mutableListOf<AtomicReference<V>>()

    constructor(list: List<V>, maxSize: Int = 0) : this(maxSize) {
        if (maxSize != 0 && list.size > maxSize) {
            error("max size is less than list.size")
        }
        list.forEach { value ->
            this.mutableList += AtomicReference(value)
        }
    }

    override fun iterator(): Iterator<V> {
        return this.snapshot().iterator()
    }

    fun operate(index: Int, create: () -> V, update: ((V) -> V)? = null, removeWhenFull: (() -> Int)? = null): V? {
        val createAndSetToList: () -> V = {
            val value = create()
            this.mutableList[index] = AtomicReference(value)
            value
        }
        if (this.mutableList.getOrNull(index) != null) {//update when exists
            return atomicUpdate(index, update)
        } else {//create
            return this.operationLock.operate {
                if (this.mutableList.getOrNull(index) != null) {
                    atomicUpdate(index, update)
                } else {//check size
                    val size = this.mutableList.size
                    if (this.maxSize <= 0 || size < this.maxSize) {
                        createAndSetToList()
                    } else {
                        if (removeWhenFull != null) {
                            val removeIndex = removeWhenFull()
//                            println("what?[$removeKey],before:"+this.map.entries)//for debug
                            this.mutableList.removeAt(removeIndex) ?: error("map remove error, remove key:[$removeIndex] not exists")
                            createAndSetToList()
                        } else {
                            //error
                            error("map is full, please implement block parameter(removeWhenMax), current size:%s, max size:%s".format(size, this.maxSize))
                        }
                    }
                }
            }
        }
    }

    /**
     * get
     * @param index
     * @return V?
     */
    operator fun get(index: Int): V? {
        return this.mutableList.getOrNull(index)?.get()
    }

    /**
     * remove
     * @param index
     * @return V
     */
    fun remove(index: Int): V {
        return this.operationLock.operate {
            this.mutableList.removeAt(index).get()
        }
    }

    /**
     * minus
     * @param index
     * @return V
     */
    operator fun minus(index: Int): V {
        return remove(index)
    }

    /**
     * clear
     */
    fun clear() {
        this.operationLock.operate {
            this.mutableList.clear()
        }
    }

    /**
     * atomic update
     * @param index
     * @param update
     * @return V?
     */
    private fun atomicUpdate(index: Int, update: ((V) -> V)?): V? {
        val atomicReference = this.mutableList.getOrNull(index)
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
     * @return List<V>
     */
    fun snapshot(): List<V> {
        return this.mutableList.map { atomicReference -> atomicReference.get() }
    }
}