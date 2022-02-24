package com.oneliang.ktx.util.test.math

import com.oneliang.ktx.util.math.segment.Segmenter

fun main() {
//    System.setOut(PrintStream(FileOutputStream(File("/D:/log.txt"))))
    var segmentList = listOf(Segmenter.Segment<Any?>(begin = 0L, end = 1L), Segmenter.Segment(begin = 5L, end = 100L))

//    var segmentList = Segmenter.splitSegment(segmentList, 50, listOf(100L to null, 20L to null, 20L to null))
//    segmentList.forEach {
//        println("${it.id}, ${it.begin}, ${it.end}, ${it.canUse}")
//    }
//    segmentList = Segmenter.splitSegment(segmentList, 0, 40L to null)
//    segmentList.forEach {
//        println("${it.id}, ${it.begin}, ${it.end}, ${it.canUse}")
//    }
//    segmentList = Segmenter.splitSegment(segmentList, 40, 5L to null)
//    segmentList.forEach {
//        println("${it.id}, ${it.begin}, ${it.end}, ${it.canUse}")
//    }

    segmentList = Segmenter.resetAndSplitSegment(segmentList, 0, 5L to null)
    segmentList.forEach {
        println("${it.id}, ${it.begin}, ${it.end}, ${it.canUse}, ${it.data}")
    }
    segmentList = Segmenter.resetAndSplitSegment(segmentList, 5, 5L to null)
    segmentList.forEach {
        println("${it.id}, ${it.begin}, ${it.end}, ${it.canUse}")
    }
    segmentList = Segmenter.resetAndSplitSegment(segmentList, 5, 6L to null)
    segmentList.forEach {
        println("${it.id}, ${it.begin}, ${it.end}, ${it.canUse}")
    }
}