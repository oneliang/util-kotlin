package com.oneliang.ktx.util.concurrent.atomic

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

class AwaitAndSignal<K : Any> {

    private val lock = ReentrantLock()
    private val conditionMap = ConcurrentHashMap<K, Condition>()

    fun await(conditionKey: K, beforeAwait: () -> Unit = {}, afterAwait: () -> Unit = {}) {
        try {
            this.lock.lock()
            beforeAwait()
            val condition = this.conditionMap.getOrPut(conditionKey) { this.lock.newCondition() }
            condition.await()
            afterAwait()
        } finally {
            this.lock.unlock()
        }
    }

    fun await(conditionKey: K, time: Long, timeUnit: TimeUnit, beforeAwait: () -> Unit = {}, afterAwait: () -> Unit = {}) {
        try {
            this.lock.lock()
            beforeAwait()
            val condition = this.conditionMap.getOrPut(conditionKey) { this.lock.newCondition() }
            condition.await(time, timeUnit)
            afterAwait()
        } finally {
            this.lock.unlock()
        }
    }

    fun signal(conditionKey: K, autoRemoveConditionKey: Boolean = true, beforeSignal: () -> Unit = {}, afterSignal: () -> Unit = {}) {
        try {
            this.lock.lock()
            beforeSignal()
            val condition = this.conditionMap.getOrPut(conditionKey) { this.lock.newCondition() }
            if (autoRemoveConditionKey) {
                this.conditionMap.remove(conditionKey)
            }
            condition.signal()
            afterSignal()
        } finally {
            this.lock.unlock()
        }
    }

    fun signalAll(conditionKey: K, autoRemoveConditionKey: Boolean = true, beforeSignalAll: () -> Unit = {}, afterSignalAll: () -> Unit = {}) {
        try {
            this.lock.lock()
            beforeSignalAll()
            val condition = this.conditionMap.getOrPut(conditionKey) { this.lock.newCondition() }
            if (autoRemoveConditionKey) {
                this.conditionMap.remove(conditionKey)
            }
            condition.signalAll()
            afterSignalAll()
        } finally {
            this.lock.unlock()
        }
    }
}