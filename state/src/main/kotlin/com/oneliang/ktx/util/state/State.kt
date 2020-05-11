package com.oneliang.ktx.util.state

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.util.*

open class State(val key: String = Constants.String.BLANK, val name: String = Constants.String.BLANK, private val block: (currentState: State) -> Unit = {}) {
    companion object {
        private val logger = LoggerManager.getLogger(State::class)
    }

    private val previousStateMap = HashMap<String, State>()
    private val nextStateMap = HashMap<String, State>()

    /**
     * get previous key set
     *
     * @return Set<Integer>
    </Integer> */
    val previousKeySet: Set<String>
        get() = this.previousStateMap.keys

    /**
     * get next key set
     *
     * @return Set<Integer>
    </Integer> */
    val nextKeySet: Set<String>
        get() = this.nextStateMap.keys

    /**
     * add previous state
     *
     * @param state
     */
    private fun addPreviousState(state: State) {
        this.previousStateMap[state.key] = state
    }

    /**
     * add next state
     *
     * @param state
     */
    fun addNextState(state: State) {
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
     * previous
     *
     * @param key
     * @return State
     * @throws StateNotFoundException
     */
    @Throws(StateNotFoundException::class)
    fun previous(key: String): State {
        if (previousStateMap.containsKey(key)) {
            return previousStateMap[key]!!
        } else {
            logger.error("previous state key:%s is not exist", key)
            throw StateNotFoundException(String.format("previous state key:%s", key))
        }
    }

    /**
     * has next
     *
     * @return boolean
     */
    operator fun hasNext(): Boolean {
        return this.nextStateMap.isNotEmpty()
    }

    /**
     * next
     *
     * @param key
     * @return State
     * @throws StateNotFoundException
     */
    @Throws(StateNotFoundException::class)
    fun next(key: String): State {
        if (nextStateMap.containsKey(key)) {
            return nextStateMap[key]!!
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
