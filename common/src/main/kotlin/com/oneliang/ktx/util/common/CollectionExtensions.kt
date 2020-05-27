package com.oneliang.ktx.util.common

import java.util.*
import kotlin.reflect.KClass

/**
 * for Collection<Any> most time
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> Collection<T>.toArray(kClass: KClass<out T>): Array<T> {
    val objectArray = java.lang.reflect.Array.newInstance(kClass.java, this.size) as Array<T>
    this.forEachIndexed { index, instance ->
        objectArray[index] = instance
    }
    return objectArray
}

inline fun <T, reified R> Collection<T>.toArray(transform: (T) -> R): Array<R> {
    return Array(size) {
        transform(elementAt(it))
    }
}

/**
 * for tree node data
 */
fun <T : Any> Collection<T>.findAllChild(isChild: (T) -> Boolean, hasChild: (T) -> Boolean, whenHasChild: (T) -> T): List<T> {
    val list = mutableListOf<T>()
    val queue = LinkedList<T>()
    queue += this
    while (queue.isNotEmpty()) {
        val item = queue.poll()
        if (isChild(item)) {
            list += item
        } else if (hasChild(item)) {
            queue += whenHasChild(item)
        }
    }
    return list
}


fun <T> Collection<T>.sameAs(collection: Collection<T>): Boolean = this.size == collection.size && this.differs(collection).isEmpty()

fun <T> Collection<T>.includes(collection: Collection<T>): Boolean = collection.differs(this).isEmpty()

fun <T> Collection<T>.matches(collection: Collection<T>): Boolean = this.includes(collection)