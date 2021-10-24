package com.oneliang.ktx.util.concurrent.atomic

class LRUCacheSet<V : Any>(private val maxSize: Int) : Iterable<LRUCacheSet.ItemCounter<V>> {
    private val dataSortedSet = AtomicTreeSet(object : Comparator<ItemCounter<V>> {
        override fun compare(o1: ItemCounter<V>?, o2: ItemCounter<V>?): Int {
            if (o1 != null && o2 != null) {
                return when {
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
            return 0//o1 is null or o2 is null
        }
    })
    private val dataAtomicMap = AtomicMap<V, ItemCounter<V>>(this.maxSize)

    override fun iterator(): Iterator<ItemCounter<V>> {
        return this.dataSortedSet.iterator()
    }

    operator fun plusAssign(value: V) {
        this.dataAtomicMap.operate(value, create = {
            //check size
            val itemCounter = ItemCounter(value).also { it.update() }
            this.dataSortedSet += itemCounter
            itemCounter
        }, update = {
            it.update()
            this.dataSortedSet -= it
            val newItemCounter = it.copy()
            this.dataSortedSet += newItemCounter//replace
            newItemCounter
        }, removeWhenFull = {
            val itemCounter = this.dataSortedSet.last()
            this.dataSortedSet -= itemCounter
            itemCounter.value
        })
    }

    fun remove(value: V) {
        val itemCounter = this.dataAtomicMap - value
        if (itemCounter != null) {
            this.dataSortedSet - itemCounter
        }
    }

    operator fun minusAssign(value: V) {
        this.remove(value)
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
    }
}