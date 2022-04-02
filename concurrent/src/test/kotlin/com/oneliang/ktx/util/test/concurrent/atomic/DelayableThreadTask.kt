package com.oneliang.ktx.util.test.concurrent.atomic

import com.oneliang.ktx.util.concurrent.ThreadTask
import java.util.*

open class DelayableThreadTask : ThreadTask {

    private var executeFlag = 0
    private val executeMap = mutableMapOf<Int, () -> Unit>()
    private val delayMap = mutableMapOf<Int, Long>()
    private val timer = Timer()

    private fun delayExecute(millisTime: Long) {
        this.timer.schedule(object : TimerTask() {
            override fun run() {
                innerDelayExecute()
            }
        }, millisTime)
    }

    protected fun execute(block: () -> Unit) {
        this.executeMap[this.executeMap.size] = block
        if (this.delayMap.isNotEmpty()) {
            return
        } else {//execute when no delay appear
            onlyExecute(this.executeFlag)
            this.executeFlag++//next execute flag
        }
    }

    private fun onlyExecute(executeFlag: Int): Boolean {
        val executeBlock = this.executeMap[executeFlag]
        if (executeBlock == null) {//finish all block
            return true
        } else {
            executeBlock.invoke()
        }
        return false
    }

    private fun innerDelayExecute() {
        delayMap -= this.executeFlag
        val finishedFlag = innerNormalExecute()
        if (finishedFlag) {
            return
        }
    }

    private fun innerNormalExecute(): Boolean {
//        println("innerExecute:%s".format(this.executeFlag))
        val finishedFlag = onlyExecute(this.executeFlag)
        if (finishedFlag) {
            return true
        }
        this.executeFlag++//next execute flag
        checkNextAndExecute(this.executeFlag)
        return false
    }

    private fun checkNextAndExecute(executeFlag: Int) {
//        println("check next and execute:%s".format(executeFlag))
        val delayMillisTime = this.delayMap[executeFlag]
        if (delayMillisTime != null) {//next block is delay block
            delayExecute(delayMillisTime)
        } else {//next block is not delay block
            innerNormalExecute()
        }
    }

    protected fun delay(millisTime: Long) {
        val firstDelay = this.delayMap.isEmpty()
        this.delayMap.merge(this.executeMap.size, millisTime) { _, oldValue ->
            oldValue + millisTime
        }
        if (firstDelay) {
            delayExecute(millisTime)
        }
    }

    override fun runTask() {
        execute {
            println(System.currentTimeMillis().toString() + "," + 1)
        }
        this.delay(1000)
        execute {
            println(System.currentTimeMillis().toString() + "," + 2)
        }
        this.delay(2000)
        execute {
            println(System.currentTimeMillis().toString() + "," + 3)
        }
    }
}

fun main() {
    val delayableThreadTask = DelayableThreadTask()
    delayableThreadTask.runTask()
    Thread.sleep(10000)
    println("finished")
}