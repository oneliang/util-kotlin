package com.oneliang.ktx.util.concurrent.atomic

import java.util.*

class AtomicTreeSet<T>(comparator: Comparator<T>) : Iterable<T> {

    private val lock = Lock()
    private val treeSet = TreeSet(comparator)

    override fun iterator(): Iterator<T> {
        return this.treeSet.iterator()
    }

    operator fun plus(value: T): Boolean {
        return this.lock.operate {
            this.treeSet.add(value)
        }
    }

    operator fun plusAssign(value: T) {
        this.lock.operate {
            this.treeSet += value
        }
    }

    operator fun minus(value: T): Boolean {
        return this.lock.operate {
            this.treeSet.remove(value)
        }
    }

    operator fun minusAssign(value: T) {
        this.lock.operate {
            this.treeSet -= value
        }
    }

    fun first(): T {
        return this.lock.operate {
            this.treeSet.first()
        }
    }

    fun last(): T {
        return this.lock.operate {
            this.treeSet.last()
        }
    }

    fun clear() {
        this.lock.operate {
            this.treeSet.clear()
        }
    }

}