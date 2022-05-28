package com.oneliang.ktx.util.concurrent.atomic

class LRUCacheSet<V : Any>(private val maxSize: Int = 0, type: Type = Type.DESCENT) : Iterable<LRUCacheSet.ItemCounter<V>> {

    enum class Type {
        ASCENT, DESCENT
    }

    private val descentComparator: Comparator<ItemCounter<V>> = Comparator { o1, o2 ->
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

    private val ascentComparator: Comparator<ItemCounter<V>> = Comparator { o1, o2 ->
        when {
            o1.value == o2.value -> {
                0
            }
            o1.lastUsedTime > o2.lastUsedTime -> {
                1
            }
            o1.lastUsedTime == o2.lastUsedTime -> {
                if (o1.count >= o2.count) {
                    1
                } else {
                    -1
                }
            }
            else -> {
                -1
            }
        }
    }

    private val dataAtomicTreeSet: AtomicTreeSet<ItemCounter<V>> = AtomicTreeSet(if (type == Type.ASCENT) this.ascentComparator else this.descentComparator)

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

    fun operate(value: V, initializeItemCounter: InitializeItemCounter? = null, removeWhenFull: ((itemCounter: ItemCounter<V>) -> Unit)? = null): ItemCounter<V>? {
        return this.dataAtomicMap.operate(value, create = {
            //check size
            val itemCounter = ItemCounter(value).also {
                it.lastUsedTime = initializeItemCounter?.lastUsedTime ?: it.lastUsedTime
                it.count = initializeItemCounter?.count ?: it.count
                it.update()
            }
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
            removeWhenFull?.invoke(itemCounter)
            this.dataAtomicTreeSet -= itemCounter
            itemCounter.value
        })
    }

    fun remove(value: V): ItemCounter<V>? {
        val itemCounter = this.dataAtomicMap.remove(value)
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

        operator fun component1(): V {
            return this.value
        }

        operator fun component2(): Long {
            return this.lastUsedTime
        }

        operator fun component3(): Int {
            return this.count
        }

        override fun toString(): String {
            return this.value.toString() + "," + this.count + "," + this.lastUsedTime
        }
    }
}