package com.oneliang.ktx.util.concurrent.atomic

class LRUCacheMap<K : Any, V>(private val maxSize: Int) : Iterable<LRUCacheMap.ItemCounter<K, V>> {

    private val dataAtomicTreeSet = AtomicTreeSet<ItemCounter<K, V>> { o1, o2 ->
        when {
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
    private val dataAtomicMap = AtomicMap<K, ItemCounter<K, V>>(this.maxSize)

    override fun iterator(): Iterator<ItemCounter<K, V>> {
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

    fun operate(key: K, create: () -> Pair<V, InitializeItemCounter?>, removeWhenFull: ((itemCounter: ItemCounter<K, V>) -> Unit)? = null): ItemCounter<K, V>? {
        return this.dataAtomicMap.operate(key, create = {
            //check size
            val (value, initializeItemCounter) = create()
            val itemCounter = ItemCounter(key, value).also {
                it.lastUsedTime = initializeItemCounter?.lastUsedTime ?: it.lastUsedTime
                it.count = initializeItemCounter?.count ?: it.count
                it.update()
            }
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
            removeWhenFull?.invoke(itemCounter)
            this.dataAtomicTreeSet -= itemCounter
            itemCounter.key
        })
    }

    fun remove(key: K): ItemCounter<K, V>? {
        val itemCounter = this.dataAtomicMap - key
        if (itemCounter != null) {
            this.dataAtomicTreeSet -= itemCounter
        }
        return itemCounter
    }

    fun clear() {
        this.dataAtomicTreeSet.clear()
        this.dataAtomicMap.clear()
    }

    class InitializeItemCounter(val lastUsedTime: Long = System.currentTimeMillis(), val count: Int = 0)

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

        operator fun component1(): K {
            return this.key
        }

        operator fun component2(): V {
            return this.value
        }

        operator fun component3(): Long {
            return this.lastUsedTime
        }

        operator fun component4(): Int {
            return this.count
        }
    }
}