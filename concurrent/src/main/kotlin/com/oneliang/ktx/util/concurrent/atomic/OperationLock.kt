package com.oneliang.ktx.util.concurrent.atomic

import java.util.concurrent.locks.ReentrantLock

class OperationLock {

    private val lock = ReentrantLock()

    fun <T> operate(operate: () -> T): T {
        return try {
            this.lock.lock()
            operate()
        } finally {
            this.lock.unlock()
        }
    }
}