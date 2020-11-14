package com.oneliang.ktx.util.reflect

import java.util.concurrent.CopyOnWriteArrayList

open class ThrowableChain {
    private val throwableList: MutableList<Throwable> = CopyOnWriteArrayList()
    /**
     * add throwable
     * @param throwable
     * @return boolean
     */
    protected fun addThrowable(throwable: Throwable): Boolean {
        return throwableList.add(throwable)
    }

    /**
     * get throwable list
     * @return List<Throwable>
    </Throwable> */
    fun getThrowableList(): List<Throwable> {
        return throwableList
    }
}