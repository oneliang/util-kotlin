package com.oneliang.ktx.util.state

import com.oneliang.ktx.util.logging.LoggerManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class StateMap<K, T : State<K>>(val startState: T, initializeStateMap: Map<K, T> = emptyMap()) {
    companion object {
        private val logger = LoggerManager.getLogger(StateMap::class)

        @Throws(Exception::class)
        private fun <K> printState(startState: State<K>) {
            logger.info("key:%s, name:%s", startState.key, startState.name)
            if (startState.hasNext()) {
                val nextStateKeySet = startState.nextKeySet
                for (nextKey in nextStateKeySet) {
                    val nextState = startState.next(nextKey)
                    printState(nextState)
                }
            }
        }
    }

    private val stateMap = ConcurrentHashMap<K, T>()

    init {
        initializeStateMap.forEach { (key, state) ->
            this.stateMap[key] = state
        }
        travelAllNextStateToStateMap(this.startState)
    }

    @Suppress("UNCHECKED_CAST")
    private fun travelAllNextStateToStateMap(state: T) {
        val queue = ConcurrentLinkedQueue<T>()
        queue += state
        this.stateMap[state.key] = state
        while (queue.isNotEmpty()) {
            val currentState = queue.poll()!!
            if (currentState.hasNext()) {
                val nextStateKeySet = currentState.nextKeySet
                for (nextKey in nextStateKeySet) {
                    val nextState = currentState.next(nextKey) as T
                    if (!this.stateMap.containsKey(nextState.key)) {
                        this.stateMap[nextState.key] = nextState
                        queue += nextState
                    }
                }
            }
        }
    }

    fun getState(key: K): T {
        if (this.stateMap.containsKey(key)) {
            return this.stateMap[key]!!
        } else {
            throw State.StateNotFoundException("state key:%s is not exist:%s".format(key))
        }
    }

    fun addNextState(key: K, nextState: T) {
        if (this.stateMap.containsKey(key)) {
            val previousState = this.stateMap[key]!!
            previousState.addNextState(nextState)
            this.stateMap[nextState.key] = nextState
        } else {
            logger.error("state key:%s is not exist", key)
        }
    }

    /**
     * check previous
     * @param key
     * @param previousKey
     * @return Boolean
     */
    fun checkPrevious(key: K, previousKey: K): Boolean {
        if (this.stateMap.containsKey(key)) {
            return this.stateMap[key]?.checkPrevious(previousKey) ?: return false
        } else {
            return false
        }
    }

    /**
     * check next
     * @param key
     * @param nextKey
     * @return Boolean
     */
    fun checkNext(key: K, nextKey: K): Boolean {
        if (this.stateMap.containsKey(key)) {
            return this.stateMap[key]?.checkNext(nextKey) ?: return false
        } else {
            return false
        }
    }

    fun printState() {
        try {
            printState(this.startState)
        } catch (e: Exception) {
            logger.error("state not found", e)
        }
    }
}
