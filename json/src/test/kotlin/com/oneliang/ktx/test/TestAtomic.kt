package com.oneliang.ktx.test

import com.oneliang.ktx.util.concurrent.atomic.AtomicMap
import com.oneliang.ktx.util.json.toJson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicReference

class TestAtomic {
    var count = 0
}

fun main() {
    val atomicMap = AtomicMap<String, TestAtomic>()
    runBlocking {
        val jobList = mutableListOf<Job>()
        for (i in 0 until 10000) {
            if (i % 2 == 0) {
                jobList += launch {
                    atomicMap.operate("a", create = {
                        TestAtomic().apply { this.count = 1 }
                    }, update = {
                        TestAtomic().apply {
                            this.count = it.count + 1
                        }
                    })
                }
            } else {
                jobList += launch {
                    atomicMap.operate("b", create = {
                        TestAtomic().apply { this.count = 1 }
                    }, update = {
                        TestAtomic().apply {
                            this.count = it.count + 1
                        }
                    })
                }
            }
        }
        jobList.forEach {
            it.join()
        }
    }
    atomicMap.snapshot().forEach { (key: String, value: TestAtomic) ->
        println(key + "," + value.count)
    }
    println(atomicMap.toJson())

    val testAtomic = TestAtomic()
    val atomic = AtomicReference<TestAtomic>(testAtomic)
//    var count = 0
    runBlocking {
        val jobList = mutableListOf<Job>()
        for (i in 0 until 10000) {
            jobList += launch {
                delay(100)
//                testAtomic.apply { this.count = atomic.get().count + 1 }
                atomic.getAndSet(TestAtomic().apply { this.count = atomic.get().count + 1 })
            }
        }
//        jobList.forEach {
//            it.join()
//        }
        delay(1000)
    }
    println(atomic.get().count)
    println(testAtomic.count)
//    println(count)
}