package com.oneliang.ktx.util.concurrent.atomic

class AtomicTreeSet<T>(private val comparator: Comparator<T>?) : Iterable<T> {

    private val operationLock = OperationLock()
    private val list = mutableListOf<T>()

    constructor() : this(null)

    val size: Int
        get() = this.list.size

    override fun iterator(): Iterator<T> {
        return this.operationLock.operate {
            this.list.iterator()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun add(value: T): Boolean {
        return this.operationLock.operate {
            if (this.list.isEmpty()) {
                return@operate this.list.add(value)
            }
            var insertIndex = 0
            var needToInsert = false
            for ((index, item) in this.list.withIndex()) {
                val compareResult = when {
                    this.comparator != null -> {
                        this.comparator.compare(value, item)
                    }
                    value is Comparable<*> -> {
                        value as Comparable<T>
                        value.compareTo(item)
                    }
                    else -> {
                        error("value must implement Comparable<*>, value:%s".format(value))
                    }
                }
                if (compareResult > 0) {//insert after
                    needToInsert = true
                    insertIndex = index + 1
                    continue//next
                } else if (compareResult == 0) {
                    needToInsert = false
                    break//no need to insert
                } else {//insert before
                    needToInsert = true
                    insertIndex = index
                    break
                }
            }
            if (needToInsert) {
                this.list.add(insertIndex, value)
                true
            } else {
                false
            }
        }
    }

    operator fun plusAssign(value: T) {
        this.add(value)
    }

    @Suppress("UNCHECKED_CAST")
    fun remove(value: T): Boolean {
        return this.operationLock.operate {
            var deleteIndex = 0
            var needToDelete = false
            for ((index, item) in this.list.withIndex()) {
                val compareResult = when {
                    this.comparator != null -> {
                        this.comparator.compare(value, item)
                    }
                    value is Comparable<*> -> {
                        value as Comparable<T>
                        value.compareTo(item)
                    }
                    else -> {
                        error("value must implement Comparable<*>, value:%s".format(value))
                    }
                }

                if (compareResult == 0) {
                    needToDelete = true
                    deleteIndex = index
                    break
                }
            }
            if (needToDelete) {
                this.list.removeAt(deleteIndex)
                true
            } else {
                false
            }
        }
    }

    operator fun minusAssign(value: T) {
        this.remove(value)
    }

    fun first(): T {
        return this.operationLock.operate {
            this.list.first()
        }
    }

    fun last(): T {
        return this.operationLock.operate {
            this.list.last()
        }
    }

    fun clear() {
        this.operationLock.operate {
            this.list.clear()
        }
    }

    fun isEmpty(): Boolean {
        return this.operationLock.operate {
            this.list.isEmpty()
        }
    }
}