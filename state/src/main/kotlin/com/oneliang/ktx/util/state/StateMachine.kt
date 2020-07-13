package com.oneliang.ktx.util.state

import com.oneliang.ktx.util.logging.LoggerManager

class StateMachine<K, T : State<K>>(var currentState: T) {

    companion object {
        private val logger = LoggerManager.getLogger(StateMachine::class)
    }

    /**
     * get previous state key set
     * @return Set<Integer>
    </Integer> */
    val previousStateKeySet: Set<K>
        get() {
            return this.currentState.previousKeySet
        }

    /**
     * get next state key set
     *
     * @return Set<Integer>
    </Integer> */
    val nextStateKeySet: Set<K>
        get() {
            return this.currentState.nextKeySet
        }

    /**
     * has previous state
     * @return boolean
     */
    fun hasPreviousState(): Boolean {
        return this.currentState.hasPrevious()
    }

    /**
     * previous state
     * @param key
     * @throws State.StateNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(State.StateNotFoundException::class)
    fun previousState(key: K) {
        this.currentState = this.currentState.previous(key) as T
    }

    /**
     * has next state
     *
     * @return boolean
     */
    fun hasNextState(): Boolean {
        return this.currentState.hasNext()
    }

    /**
     * next state
     * @param key
     * @throws State.StateNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(State.StateNotFoundException::class)
    fun nextState(key: K) {
        this.currentState = this.currentState.next(key) as T
    }
}
