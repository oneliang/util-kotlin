package com.oneliang.ktx.util.concurrent

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue

class WaitingLatch {
    private val runnableQueue = ConcurrentLinkedQueue<Runnable>()
    private val threadQueue = ConcurrentLinkedQueue<Thread>()
    @Volatile
    private var finish = false

    fun addRunnable(runnable: Runnable?) {
        runnableQueue.offer(runnable)
    }

    fun startAll() {
        for (runnable in runnableQueue) {
            val thread = Thread(runnable)
            thread.start()
            threadQueue.add(thread)
        }
    }

    fun waiting() {
        while (!finish) {
            finish = true
            for (thread in threadQueue) {
                if (thread.isAlive) {
                    finish = false
                    break
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val pool = WaitingLatch()
    pool.addRunnable(object : Runnable {
        override fun run() {
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            System.out.println("run 1")
        }
    })
    pool.addRunnable(object : Runnable {
        override fun run() {
            System.out.println("run 2")
        }
    })
    pool.addRunnable(object : Runnable {
        override fun run() {
            System.out.println("run 3")
        }
    })
    pool.addRunnable(object : Runnable {
        override fun run() {
            System.out.println("run 4")
        }
    })
    pool.startAll()
    pool.waiting()
    System.out.println("---all finish---")
}