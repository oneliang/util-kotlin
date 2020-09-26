package com.oneliang.ktx.util.test

import com.oneliang.ktx.util.math.Segmenter

fun main() {
    val segmentList = listOf(Segmenter.Segment(0L, 100L), Segmenter.Segment(200L, 300L))

    var splitSegmentList = Segmenter.splitSegment(segmentList, 50, listOf(100L, 20L, 20L))
    splitSegmentList.forEach {
        println("${it.begin}, ${it.end}, ${it.canUse}")
    }
    splitSegmentList = Segmenter.splitSegment(splitSegmentList, 0, 40)
    splitSegmentList.forEach {
        println("${it.begin}, ${it.end}, ${it.canUse}")
    }
    splitSegmentList = Segmenter.splitSegment(splitSegmentList, 40, 5)
    splitSegmentList.forEach {
        println("${it.begin}, ${it.end}, ${it.canUse}")
    }

    splitSegmentList = Segmenter.resetAndSplitSegment(splitSegmentList, 0, 5)
    splitSegmentList.forEach {
        println("${it.begin}, ${it.end}, ${it.canUse}")
    }
}