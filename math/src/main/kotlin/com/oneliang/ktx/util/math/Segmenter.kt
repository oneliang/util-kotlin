package com.oneliang.ktx.util.math

import com.oneliang.ktx.util.logging.LoggerManager

object Segmenter {
    class Segment(var begin: Long, var end: Long, var canUse: Boolean = true) {
        fun copy(): Segment {
            return Segment(this.begin, this.end, this.canUse)
        }
    }

    private val logger = LoggerManager.getLogger(Segmenter::class)

    fun findSuitableBegin(segmentList: List<Segment>, begin: Long): Long {
        for (segment in segmentList) {
            if (!segment.canUse) {
                continue
            }
            return if (segment.begin <= begin && begin < segment.end) {
                begin
            } else {
                if (begin <= segment.begin) {
                    segment.begin
                } else {
                    continue
                }
            }
        }
        error("begin(${begin}) is out of range")
    }

    fun splitSegment(segmentList: List<Segment>, begin: Long, length: Long): List<Segment> {
        return splitSegment(segmentList, begin, listOf(length))
    }

    fun resetAndSplitSegment(segmentList: List<Segment>, begin: Long, length: Long): List<Segment> {
        if (segmentList.isEmpty()) {
            error("segment list is empty")
        }
        val firstSegmentBegin = segmentList.first().begin
        val lengthList = mutableListOf<Long>()
        val found = if (begin <= firstSegmentBegin) {
            lengthList += length
            true
        } else {
            false
        }
        val newSegmentList = mutableListOf<Segment>()
        for (segment in segmentList) {
            if (!found && segment.begin <= begin && begin <= segment.end) {
                lengthList += length
            }
            if (!segment.canUse) {
                lengthList += (segment.end - segment.begin)
            }
            newSegmentList += segment.copy().apply { this.canUse = true }
        }
        return splitSegment(newSegmentList, begin, lengthList)
    }

    fun splitSegment(segmentList: List<Segment>, begin: Long, lengthList: List<Long>): List<Segment> {
        if (segmentList.isEmpty()) {
            error("segment list is empty")
        }
        if (lengthList.isEmpty()) {
            error("length list is empty")
        }
        val checked = checkSegmentList(segmentList)
        if (!checked) {
            return emptyList()
        }
        //check begin and length list
        var sumLength = 0L
        lengthList.forEach { length ->
            if (length < 0) {
                error("length do not support less than 0")
            }
            sumLength += length
        }
        val (firstSegment, lastSegment) = findFirstAndLastSegment(segmentList)
        if (firstSegment == null || lastSegment == null) {
            return emptyList()
        }
        if (begin >= lastSegment.end || (begin + sumLength) >= lastSegment.end) {
            error("out of range(${lastSegment.end})")
        }
        val splitSegmentList = mutableListOf<Segment>()
        val fixBegin = if (begin <= firstSegment.begin) {
            firstSegment.begin
        } else {
            splitSegmentList += Segment(firstSegment.begin, begin, true)
            begin
        }
        val lengthIterator = lengthList.iterator()
        var newBegin = fixBegin
        var remainLength = lengthIterator.next()
        val segmentIterator = segmentList.iterator()
        var segment: Segment?
        segment = segmentIterator.next()
        while (segment != null) {
            if (!segment.canUse) {
                splitSegmentList += segment
                if (segmentIterator.hasNext()) {
                    segment = segmentIterator.next()
                    continue
                } else {//no segment
                    break
                }
            }
            if (!(segment.begin <= newBegin && newBegin < segment.end)) {//begin out of segment, fix begin
                if (newBegin <= segment.begin) {
                    newBegin = segment.begin
                } else {//newBegin >= segment.end
                    segment = if (segmentIterator.hasNext()) {
                        segmentIterator.next()
                    } else {
                        null
                    }
                    continue
                }
            }//else //begin is in segment
            logger.verbose("(%s,%s)(%s)-(%s,%s)", segment.begin, segment.end, segment.canUse, newBegin, remainLength)
            val end = newBegin + remainLength
            if (segment.begin <= end && end < segment.end) {//end is in segment
                splitSegmentList += Segment(newBegin, end, false)
                newBegin = end
                remainLength = 0
            } else {
                splitSegmentList += Segment(newBegin, segment.end, false)
                newBegin = segment.end
                remainLength = end - segment.end
            }
            if (remainLength == 0L) {
                if (lengthIterator.hasNext()) {
                    remainLength = lengthIterator.next()
                } else {
                    if (end < segment.end) {
                        splitSegmentList += Segment(end, segment.end, true)
                    }
                    while (segmentIterator.hasNext()) {
                        splitSegmentList += segmentIterator.next()
                    }
                    break
                }
            }
        }
        if (remainLength > 0 || lengthIterator.hasNext()) {
            logger.error("has remain, remain length in midway, remain:%s", remainLength)
            lengthIterator.forEach {
                logger.error("has remain, remain length in length list, remain:%s", it)
            }
        }
        return splitSegmentList
    }

    private fun findFirstAndLastSegment(segmentList: List<Segment>): Pair<Segment?, Segment?> {
        var firstSegment: Segment? = null
        var lastSegment: Segment? = null
        for (segment in segmentList) {
            if (!segment.canUse) {
                continue
            }
            if (firstSegment == null) {
                firstSegment = segment
            }
            lastSegment = segment
        }
        return firstSegment to lastSegment
    }

    private fun checkSegmentList(segmentList: List<Segment>): Boolean {
        if (segmentList.isEmpty()) {
            return false
        }
        val segmentListSize = segmentList.size
        for (i in segmentList.indices) {
            val segment = segmentList[i]
            val nextSegment = if (i + 1 < segmentListSize) {//check not the last
                segmentList[i + 1]
            } else {
                null
            }
            if (nextSegment != null) {//i is the last
                if (nextSegment.begin < segment.end && segment.end <= nextSegment.end) {
                    logger.error("check segment list failure, segment end:%s, next segment begin:%s, next segment end:%s", segment.end, nextSegment.begin, nextSegment.end)
                    return false
                }
            }
        }
        return true
    }
}