package com.oneliang.ktx.util.test

import com.oneliang.ktx.util.math.Segmenter

fun main() {
    var segmentList = listOf(Segmenter.Segment<Any?>(0L, 100L))//, Segmenter.Segment(200L, 300L))

//    var segmentList = Segmenter.splitSegment(segmentList, 50, listOf(100L to null, 20L to null, 20L to null))
//    segmentList.forEach {
//        println("${it.begin}, ${it.end}, ${it.canUse}")
//    }
//    segmentList = Segmenter.splitSegment(segmentList, 0, 40L to null)
//    segmentList.forEach {
//        println("${it.begin}, ${it.end}, ${it.canUse}")
//    }
//    segmentList = Segmenter.splitSegment(segmentList, 40, 5L to null)
//    segmentList.forEach {
//        println("${it.begin}, ${it.end}, ${it.canUse}")
//    }

    segmentList = Segmenter.resetAndSplitSegment(segmentList, 0, 5L to null)
    segmentList.forEach {
        println("${it.begin}, ${it.end}, ${it.canUse}")
    }

    segmentList = Segmenter.resetAndSplitSegment(segmentList, 5, 5L to null)
    segmentList.forEach {
        println("${it.begin}, ${it.end}, ${it.canUse}")
    }
    segmentList = Segmenter.resetAndSplitSegment(segmentList, 5, 6L to null)
    segmentList.forEach {
        println("${it.begin}, ${it.end}, ${it.canUse}")
    }
}