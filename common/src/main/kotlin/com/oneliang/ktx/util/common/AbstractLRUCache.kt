/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oneliang.ktx.util.common

import java.util.*

/**
 * BEGIN LAYOUTLIB CHANGE
 * This is a custom version that doesn't use the non standard LinkedHashMap#eldest.
 * END LAYOUTLIB CHANGE
 *
 * A cache that holds strong references to a limited number of values. Each time
 * a value is accessed, it is moved to the head of a queue. When a value is
 * added to a full cache, the value at the end of that queue is evicted and may
 * become eligible for garbage collection.
 *
 *
 * If your cached values hold resources that need to be explicitly released,
 * override [.entryRemoved].
 *
 *
 * If a cache miss should be computed on demand for the corresponding keys,
 * override [.create]. This simplifies the calling code, allowing it to
 * assume a value will always be returned, even when there's a cache miss.
 *
 *
 * By default, the cache size is measured in the number of entries. Override
 * [.sizeOf] to size the cache in different units. For example, this cache
 * is limited to 4MiB of bitmaps:
 * <pre>   `int cacheSize = 4 * 1024 * 1024; // 4MiB
 * LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
 * protected int sizeOf(String key, Bitmap value) {
 * return value.getByteCount();
 * }
 * }`</pre>
 *
 *
 * This class is thread-safe. Perform multiple cache operations atomically by
 * synchronizing on the cache: <pre>   `synchronized (cache) {
 * if (cache.get(key) == null) {
 * cache.put(key, value);
 * }
 * }`</pre>
 *
 *
 * This class does not allow null to be used as a key or value. A return
 * value of null from [.get], [.put] or [.remove] is
 * unambiguous: the key was not in the cache.
 *
 *
 * This class appeared in Android 3.1 (Honeycomb MR1); it's available as part
 * of [Android's
 * Support Package](http://developer.android.com/sdk/compatibility-library.html) for earlier releases.
 */
/**
 * @param maxSize for caches that do not override [.sizeOf], this is
 * the maximum number of entries in the cache. For all other caches,
 * this is the maximum sum of the sizes of the entries in this cache.
 */
abstract class AbstractLRUCache<K, V>(private var maxSize: Int) {

    private val map: LinkedHashMap<K, V>

    /** Size of this cache in units. Not necessarily the number of elements.  */
    private var size: Int = 0

    private var putCount: Int = 0
    private var createCount: Int = 0
    private var evictionCount: Int = 0
    private var hitCount: Int = 0
    private var missCount: Int = 0

    init {
        require(maxSize > 0) { "maxSize must > 0" }
        this.map = LinkedHashMap(0, 0.75f, true)
    }

    /**
     * Sets the size of the cache.
     * @param maxSize The new maximum size.
     *
     * @hide
     */
    fun resize(maxSize: Int) {
        require(maxSize > 0) { "maxSize must > 0" }
        synchronized(this) {
            this.maxSize = maxSize
        }
        trimToSize(maxSize)
    }

    /**
     * Returns the value for `key` if it exists in the cache or can be
     * created by `#create`. If a value was returned, it is moved to the
     * head of the queue. This returns null if a value is not cached and cannot
     * be created.
     */
    operator fun get(key: K): V? {
        if (key == null) {
            throw NullPointerException("key == null")
        }

        var mapValue: V?
        synchronized(this) {
            mapValue = map[key]
            if (mapValue != null) {
                hitCount++
                return mapValue
            }
            //map value is null, then need to create
            missCount++
        }

        /*
         * Attempt to create a value. This may take a long time, and the map
         * may be different when create() returns. If a conflicting value was
         * added to the map while create() was working, we leave that value in
         * the map and release the created value.
         */

        val createdValue = create(key)

        synchronized(this) {
            createCount++
            val oldMapValue = map.put(key, createdValue)
            mapValue = oldMapValue
            if (oldMapValue != null) {//already has key and value in map, maybe wrong logic branch
                // There was a conflict so undo that last put
                map.put(key, oldMapValue)
            } else {//no key or old value is null in map
                size += safeSizeOf(key, createdValue)
            }
        }

        return if (mapValue != null) {//maybe wrong logic branch
            entryRemoved(false, key, createdValue, mapValue)
            mapValue
        } else {
            trimToSize(maxSize)
            createdValue
        }
    }

    operator fun set(key: K, value: V) = put(key, value)

    /**
     * Caches `value` for `key`. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by `key`.
     */
    fun put(key: K, value: V): V? {
        if (key == null || value == null) {
            throw NullPointerException("key == null || value == null")
        }

        var previous: V?
        synchronized(this) {
            putCount++
            size += safeSizeOf(key, value)
            previous = map.put(key, value)
            val tempPrevious = previous
            if (tempPrevious != null) {
                size -= safeSizeOf(key, tempPrevious)
            }
        }
        val tempPrevious = previous
        if (tempPrevious != null) {
            entryRemoved(false, key, tempPrevious, value)
        }

        trimToSize(maxSize)
        return tempPrevious
    }

    /**
     * @param maxSize the maximum size of the cache before returning. May be -1
     * to evict even 0-sized elements.
     */
    private fun trimToSize(maxSize: Int) {
        run loop@{
            while (true) {
                var key: K? = null
                var value: V? = null
                synchronized(this) {
                    if (size < 0 || map.isEmpty() && size != 0) {
                        error(javaClass.name + ".sizeOf() is reporting inconsistent results!")
                    }

                    if (size <= maxSize) {
                        return@loop
                    }

                    // BEGIN LAYOUTLIB CHANGE
                    // get the last item in the linked list.
                    // This is not efficient, the goal here is to minimize the changes
                    // compared to the platform version.
                    var toEvict: MutableMap.MutableEntry<K, V>? = null
                    for (entry in map.entries) {
                        toEvict = entry
                    }
                    // END LAYOUTLIB CHANGE

                    if (toEvict == null) {
                        return@loop
                    }
                    key = toEvict.key
                    value = toEvict.value
                    val tempKey = key
                    map.remove(tempKey)
                    size -= safeSizeOf(tempKey!!, value!!)
                    evictionCount++
                }
                entryRemoved(true, key!!, value!!, null)
            }
        }
    }

    /**
     * Removes the entry for `key` if it exists.
     *
     * @return the previous value mapped by `key`.
     */
    fun remove(key: K): V? {
        var previous: V?
        synchronized(this) {
            previous = map.remove(key)
            val tempPrevious = previous
            if (tempPrevious != null) {
                size -= safeSizeOf(key, tempPrevious)
            }
        }

        val tempPrevious = previous
        if (tempPrevious != null) {
            entryRemoved(false, key, tempPrevious, null)
        }

        return previous
    }

    /**
     * Called for entries that have been evicted or removed. This method is
     * invoked when a value is evicted to make space, removed by a call to
     * [.remove], or replaced by a call to [.put]. The default
     * implementation does nothing.
     *
     *
     * The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     * @param evicted true if the entry is being removed to make space, false
     * if the removal was caused by a [.put] or [.remove].
     * @param newValue the new value for `key`, if it exists. If non-null,
     * this removal was caused by a [.put]. Otherwise it was caused by
     * an eviction or a [.remove].
     */
    protected open fun entryRemoved(evicted: Boolean, key: K, oldValue: V, newValue: V?) {}

    /**
     * Called after a cache miss to compute a value for the corresponding key.
     * Returns the computed value or null if no value can be computed. The
     * default implementation returns null.
     *
     *
     * The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     *
     * If a value for `key` exists in the cache when this method
     * returns, the created value will be released with [.entryRemoved]
     * and discarded. This can occur when multiple threads request the same key
     * at the same time (causing multiple values to be created), or when one
     * thread calls [.put] while another is creating a value for the same
     * key.
     */
    protected abstract fun create(key: K): V

    private fun safeSizeOf(key: K, value: V): Int {
        val result = sizeOf(key, value)
        if (result < 0) {
            error("Negative size: $key=$value")
        }
        return result
    }

    /**
     * Returns the size of the entry for `key` and `value` in
     * user-defined units.  The default implementation returns 1 so that size
     * is the number of entries and max size is the maximum number of entries.
     *
     *
     * An entry's size must not change while it is in the cache.
     */
    protected open fun sizeOf(key: K, value: V): Int {
        return 1
    }

    /**
     * Clear the cache, calling [.entryRemoved] on each removed entry.
     */
    fun evictAll() {
        trimToSize(-1) // -1 will evict 0-sized elements
    }

    /**
     * For caches that do not override [.sizeOf], this returns the number
     * of entries in the cache. For all other caches, this returns the sum of
     * the sizes of the entries in this cache.
     */
    @Synchronized
    fun size(): Int {
        return size
    }

    /**
     * For caches that do not override [.sizeOf], this returns the maximum
     * number of entries in the cache. For all other caches, this returns the
     * maximum sum of the sizes of the entries in this cache.
     */
    @Synchronized
    fun maxSize(): Int {
        return maxSize
    }

    /**
     * Returns the number of times [.get] returned a value that was
     * already present in the cache.
     */
    @Synchronized
    fun hitCount(): Int {
        return hitCount
    }

    /**
     * Returns the number of times [.get] returned null or required a new
     * value to be created.
     */
    @Synchronized
    fun missCount(): Int {
        return missCount
    }

    /**
     * Returns the number of times [.create] returned a value.
     */
    @Synchronized
    fun createCount(): Int {
        return createCount
    }

    /**
     * Returns the number of times [.put] was called.
     */
    @Synchronized
    fun putCount(): Int {
        return putCount
    }

    /**
     * Returns the number of values that have been evicted.
     */
    @Synchronized
    fun evictionCount(): Int {
        return evictionCount
    }

    /**
     * Returns a copy of the current contents of the cache, ordered from least
     * recently accessed to most recently accessed.
     */
    @Synchronized
    fun snapshot(): Map<K, V> {
        return LinkedHashMap(map)
    }

    @Synchronized
    override fun toString(): String {
        val accesses = hitCount + missCount
        val hitPercent = if (accesses != 0) 100 * hitCount / accesses else 0
        return String.format("LruCache[maxSize=%d, hits=%d, misses=%d, hitRate=%d%%]",
                maxSize, hitCount, missCount, hitPercent)
    }
}
