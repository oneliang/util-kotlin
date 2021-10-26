package com.oneliang.ktx.util.concurrent.atomic

class LRUCacheSet<V : Any>(private val maxSize: Int) : Iterable<LRUCacheSet.ItemCounter<V>> {

    private val dataAtomicTreeSet = AtomicTreeSet<ItemCounter<V>> { o1, o2 ->
        when {
            o1.value == o2.value -> {
                0
            }
            o1.lastUsedTime > o2.lastUsedTime -> {
                -1
            }
            o1.lastUsedTime == o2.lastUsedTime -> {
                if (o1.count >= o2.count) {
                    -1
                } else {
                    1
                }
            }
            else -> {
                1
            }
        }
    }
    private val dataAtomicMap = AtomicMap<V, ItemCounter<V>>(this.maxSize)

    override fun iterator(): Iterator<ItemCounter<V>> {
        return this.dataAtomicTreeSet.iterator()
    }

    val size: Int
        get() {
            if (this.dataAtomicMap.size == this.dataAtomicTreeSet.size) {
                return this.dataAtomicTreeSet.size
            } else {
                error("size not match, map size:%s, tree set size:%s".format(this.dataAtomicMap.size, this.dataAtomicTreeSet.size))
            }
        }

    fun add(value: V) {
        this.dataAtomicMap.operate(value, create = {
            //check size
            val itemCounter = ItemCounter(value).also { it.update() }
            this.dataAtomicTreeSet += itemCounter
            itemCounter
        }, update = {
            it.update()
            this.dataAtomicTreeSet -= it
            val newItemCounter = it.copy()
            this.dataAtomicTreeSet += newItemCounter//replace
            newItemCounter
        }, removeWhenFull = {
            val itemCounter = this.dataAtomicTreeSet.last()
            this.dataAtomicTreeSet -= itemCounter
            itemCounter.value
        })
    }

    fun remove(value: V) {
        val itemCounter = this.dataAtomicMap.remove(value)
        if (itemCounter != null) {
            this.dataAtomicTreeSet -= itemCounter
        }
    }

    operator fun plusAssign(value: V) {
        this.add(value)
    }

    operator fun minusAssign(value: V) {
        this.remove(value)
    }

    fun clear() {
        this.dataAtomicTreeSet.clear()
        this.dataAtomicMap.clear()
    }

    class ItemCounter<V>(val value: V) {

        var lastUsedTime: Long = 0
        var count = 0

        fun update() {
            this.lastUsedTime = System.currentTimeMillis()
            this.count++
        }

        fun copy(): ItemCounter<V> {
            val itemCounter = ItemCounter(this.value)
            itemCounter.lastUsedTime = this.lastUsedTime
            itemCounter.count = this.count
            return itemCounter
        }

        override fun toString(): String {
            return this.value.toString() + "," + this.count + "," + this.lastUsedTime
        }
    }
}