package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
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

@Suppress("UNCHECKED_CAST")
inline fun <T, reified R> Collection<T>.toArray(transform: (T) -> R): Array<R> {
    val array = arrayOfNulls<R>(this.size)
    this.forEachIndexed { index, t ->
        array[index] = transform(t)
    }
    return array as Array<R>
}

fun <T> Collection<T>.sameAs(collection: Collection<T>): Boolean = this.size == collection.size && this.differs(collection).isEmpty()

fun <T> Collection<T>.includes(collection: Collection<T>): Boolean = collection.differs(this).isEmpty()

fun <T> Collection<T>.matches(collection: Collection<T>): Boolean = this.includes(collection)

/**
 * to element relative map
 * key: PreviousElement -> NextElement
 * value: Pair<PreviousElement, NextElement>
 * @param joinSymbol
 * @param keyTransform
 * @param valueTransform
 * @param filter
 * @return Map<String, Pair<T, T>>
 */
@Suppress("UNCHECKED_CAST")
fun <T, R> Collection<T>.toElementRelativeMap(
    joinSymbol: String = (Constants.Symbol.MINUS + Constants.Symbol.GREATER_THAN),
    keyTransform: (T) -> String = { it.toString() },
    valueTransform: (T) -> R,
    filter: (T) -> Boolean = { true }
): Map<String, Pair<R, R>> {
    val map = mutableMapOf<String, Pair<R, R>>()
    var currentElement: T? = null
    var previousElement: T? = null
    for (value in this) {
        if (!filter(value)) {
            continue
        }
        if (previousElement == null) {
            previousElement = value
        } else {
            currentElement = value
            val key = keyTransform(previousElement) + joinSymbol + keyTransform(currentElement)
            map[key] = valueTransform(previousElement) to valueTransform(currentElement)
            previousElement = currentElement
        }
    }
    //check case about the single element
    if (previousElement != null && currentElement == null) {
        val key = keyTransform(previousElement) + joinSymbol + keyTransform(previousElement)
        map[key] = valueTransform(previousElement) to valueTransform(previousElement)
    }
    return map
}

/**
 * to element relative map
 * key: PreviousElement -> NextElement
 * value: Pair<PreviousElement, NextElement>
 * @param joinSymbol
 * @param keyTransform
 * @param filter
 * @return Map<String, Pair<T, T>>
 */
@Suppress("UNCHECKED_CAST")
fun <T> Collection<T>.toElementRelativeMap(
    joinSymbol: String = (Constants.Symbol.MINUS + Constants.Symbol.GREATER_THAN),
    keyTransform: (T) -> String = { it.toString() },
    filter: (T) -> Boolean = { true }
): Map<String, Pair<T, T>> {
    return this.toElementRelativeMap(joinSymbol, keyTransform, valueTransform = { it }, filter)
}