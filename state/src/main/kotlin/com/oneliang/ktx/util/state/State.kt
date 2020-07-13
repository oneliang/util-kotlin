package com.oneliang.ktx.util.state

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.util.*

open class State<K>(val key: K, val name: String = Constants.String.BLANK, private val block: (currentState: State<K>) -> Unit = {}) {
    companion object {
        private val logger = LoggerManager.getLogger(State::class)
    }

    private val previousStateMap = mutableMapOf<K, State<K>>()
    private val nextStateMap = mutableMapOf<K, State<K>>()

    /**
     * get previous key set
     * @return Set<Integer>
    </Integer> */
    val previousKeySet: Set<K>
        get() = this.previousStateMap.keys

    /**
     * get next key set
     * @return Set<Integer>
    </Integer> */
    val nextKeySet: Set<K>
        get() = this.nextStateMap.keys

    /**
     * add previous state
     * @param state
     */
    private fun addPreviousState(state: State<K>) {
        this.previousStateMap[state.key] = state
    }

    /**
     * add next state
     * @param state
     */
    fun addNextState(state: State<K>) {
        this.nextStateMap[state.key] = state
        state.addPreviousState(this)
    }

    /**
     * has previous
     *
     * @return boolean
     */
    fun hasPrevious(): Boolean {
        return this.previousStateMap.isNotEmpty()
    }

    /**
     * check previous
     * @param key
     * @return Boolean
     */
    fun checkPrevious(key: K): Boolean {
        return previousStateMap.containsKey(key)
    }

    /**
     * previous
     * @param key
     * @return State
     * @throws StateNotFoundException
     */
    @Throws(StateNotFoundException::class)
    fun previous(key: K): State<K> {
        if (previousStateMap.containsKey(key)) {
            return previousStateMap[key]!!
        } else {
            logger.error("previous state key:%s is not exist", key)
            throw StateNotFoundException(String.format("previous state key:%s", key))
        }
    }

    /**
     * has next
     * @return Boolean
     */
    fun hasNext(): Boolean {
        return this.nextStateMap.isNotEmpty()
    }

    /**
     * check next
     * @param key
     * @return Boolean
     */
    fun checkNext(key: K): Boolean {
        return this.nextStateMap.containsKey(key)
    }

    /**
     * next
     * @param key
     * @return State
     * @throws StateNotFoundException
     */
    @Throws(StateNotFoundException::class)
    fun next(key: K): State<K> {
        if (this.nextStateMap.containsKey(key)) {
            return this.nextStateMap[key]!!
        } else {
            logger.error("next state key:%s is not exist", key)
            throw StateNotFoundException(String.format("next state key:%s", key))
        }
    }

    fun run() {
        block(this)
    }

    class StateNotFoundException : Exception {

        constructor() : super() {}

        constructor(message: String) : super(message) {}

        constructor(cause: Throwable) : super(cause) {}

        constructor(message: String, cause: Throwable) : super(message, cause) {}
    }
}
