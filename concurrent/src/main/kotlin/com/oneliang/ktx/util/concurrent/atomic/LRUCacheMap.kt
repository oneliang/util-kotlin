package com.oneliang.ktx.util.concurrent.atomic

class LRUCacheMap<K : Any, V>(private val maxSize: Int) : Iterable<LRUCacheMap.ItemCounter<K, V>> {
    private val dataAtomicTreeSet = AtomicTreeSet(object : Comparator<ItemCounter<K, V>> {
        override fun compare(o1: ItemCounter<K, V>?, o2: ItemCounter<K, V>?): Int {
            if (o1 != null && o2 != null) {
                return when {
                    o1.key == o2.key -> {//same item
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

    private val dataAtomicMap = AtomicMap<K, ItemCounter<K, V>>(this.maxSize)

    override fun iterator(): Iterator<ItemCounter<K, V>> {
        return this.dataAtomicTreeSet.iterator()
    }

    fun operate(key: K, create: () -> V): V? {
        return this.dataAtomicMap.operate(key, create = {
            //check size
            val value = create()
            val itemCounter = ItemCounter(key, value).also { it.update() }
            this.dataAtomicTreeSet += itemCounter
            itemCounter
        }, update = {
            it.update()
            this.dataAtomicTreeSet -= it
            val newItemCounter = it.copy(it.value)
            this.dataAtomicTreeSet += newItemCounter//replace
            newItemCounter
        }, removeWhenFull = {
            val itemCounter = this.dataAtomicTreeSet.last()
            this.dataAtomicTreeSet -= itemCounter
            itemCounter.key
        })?.value
    }

    fun remove(key: K): V? {
        val itemCounter = this.dataAtomicMap - key
        this.dataAtomicTreeSet - itemCounter
        return itemCounter?.value
    }

    operator fun minus(key: K): V? {
        return this.remove(key)
    }

    fun clear() {
        this.dataAtomicTreeSet.clear()
        this.dataAtomicMap.clear()
    }

    class ItemCounter<K, V>(val key: K, val value: V) {
        var lastUsedTime: Long = 0
        var count = 0

        fun update() {
            this.lastUsedTime = System.currentTimeMillis()
            this.count++
        }

        fun copy(newValue: V): ItemCounter<K, V> {
            val itemCounter = ItemCounter(this.key, newValue)
            itemCounter.lastUsedTime = this.lastUsedTime
            itemCounter.count = this.count
            return itemCounter
        }
    }
}