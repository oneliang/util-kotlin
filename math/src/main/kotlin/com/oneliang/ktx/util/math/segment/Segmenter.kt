package com.oneliang.ktx.util.math.segment

import com.oneliang.ktx.util.common.forEachWithIndex
import com.oneliang.ktx.util.logging.LoggerManager
import java.util.*

object Segmenter {
    class Segment<T>(var id: Long = 0L, var begin: Long, var end: Long, var canUse: Boolean = true, var data: T? = null) {
        fun copy(): Segment<T> {
            return Segment(this.id, this.begin, this.end, this.canUse, this.data)
        }
    }

    private class BeginLengthData<T>(var begin: Long, var length: Long, var data: T? = null) {
        override fun toString(): String {
            return String.format("begin:%s, length:%s, data:%s", this.begin, this.length, this.data)
        }
    }

    private val logger = LoggerManager.getLogger(Segmenter::class)

    fun findSuitableBegin(segmentList: List<Segment<*>>, begin: Long, length: Long = 0L): Pair<Boolean, Long> {
        for (segment in segmentList) {
            if (!segment.canUse) {
                continue
            }
            val end = begin + length
            return if (segment.begin <= begin && begin < segment.end && segment.begin <= end && end < segment.end) {
                true to begin
            } else {
                if (begin < segment.begin) {
                    true to segment.begin
                } else {
                    continue
                }
            }
        }
        return false to begin
    }

    fun <T> splitSegment(segmentList: List<Segment<T>>, begin: Long, lengthData: Pair<Long, T>): List<Segment<T>> {
        return splitSegment(segmentList, begin, listOf(lengthData))
    }

    fun <T> resetAndSplitSegment(segmentList: List<Segment<T>>, insertBegin: Long, insertLengthData: Pair<Long, T?>): List<Segment<T>> {
        if (segmentList.isEmpty()) {
            error("segment list is empty")
        }
        val newSegmentList = mutableListOf<Segment<T>>()
        val segmentIdLengthDataMap = TreeMap<Long, BeginLengthData<T>>()
        for (segment in segmentList) {
            newSegmentList += segment.copy().apply { this.canUse = true }
            if (segment.id == 0L) continue
            val length = segment.end - segment.begin
            val beginLengthData = segmentIdLengthDataMap.getOrPut(segment.id) { BeginLengthData(segment.begin, 0L, segment.data) }
            beginLengthData.length = beginLengthData.length + length
        }
        logger.info("segment id length data map:%s", segmentIdLengthDataMap)
        val lengthDataList = mutableListOf<Pair<Long, T?>>()
        var fixBegin = insertBegin
        var insertBeginIndex = 0
        var insertAtLast = false
        val segmentIdLengthDataMapLastIndex = segmentIdLengthDataMap.size - 1
        segmentIdLengthDataMap.forEachWithIndex { index, _, beginLengthData ->
            if (fixBegin > beginLengthData.begin) {
                fixBegin = beginLengthData.begin
            }
            if (insertBegin <= beginLengthData.begin) {
                insertBeginIndex = index
            } else {
                if (index == segmentIdLengthDataMapLastIndex) {
                    insertAtLast = true
                }
            }
            lengthDataList += beginLengthData.length to beginLengthData.data
        }
        if (insertAtLast) {
            lengthDataList += insertLengthData
        } else {
            lengthDataList.add(insertBeginIndex, insertLengthData)
        }
        logger.verbose("insert begin:%s, fix begin:%s, length data list:%s", insertBegin, fixBegin, lengthDataList)
        return splitSegment(newSegmentList, fixBegin, lengthDataList)
    }

    private fun <T> resetAndSplitSegment2(segmentList: List<Segment<T>>, insertBegin: Long, insertLengthData: Pair<Long, T?>): List<Segment<T>> {
        if (segmentList.isEmpty()) {
            error("segment list is empty")
        }
        val (firstCanNotUseSegment, _) = findFirstAndLastSegment(segmentList, false)
        val firstSegment = segmentList.first()
        val lengthDataList = mutableListOf<Pair<Long, T?>>()
        var fixBegin = insertBegin
        var found = false//found the insert position
        if (insertBegin <= firstSegment.begin) {
            lengthDataList += insertLengthData
            fixBegin = firstSegment.begin
            found = true
        }
        val newSegmentList = mutableListOf<Segment<T>>()
        for (segment in segmentList) {
            if (segment.canUse) {
                //segment can use, then add insert length
                if (!found && segment.begin <= insertBegin && insertBegin <= segment.end) {
                    lengthDataList += insertLengthData
                    found = true
                    fixBegin = insertBegin
                }
            } else {//segment can not use, add segment length first, then add insert length
                lengthDataList += (segment.end - segment.begin) to segment.data
                if (!found && segment.begin <= insertBegin && insertBegin <= segment.end) {
                    lengthDataList += insertLengthData
                    found = true
                    if (firstCanNotUseSegment != null) {
                        fixBegin = firstCanNotUseSegment.begin
                    }
                }
            }
            newSegmentList += segment.copy().apply { this.canUse = true }
        }
        //last check, insert length data
        if (!found) {
            lengthDataList += insertLengthData
        }
        logger.verbose("fix begin:%s, length data list:%s", fixBegin, lengthDataList)
        return splitSegment(newSegmentList, fixBegin, lengthDataList)
    }

    fun <T> splitSegment(segmentList: List<Segment<T>>, begin: Long, lengthDataList: List<Pair<Long, T?>>): List<Segment<T>> {
        if (segmentList.isEmpty()) {
            error("segment list is empty")
        }
        if (lengthDataList.isEmpty()) {
            error("length list is empty")
        }
        val checked = checkSegmentList(segmentList)
        if (!checked) {
            return emptyList()
        }
        //check begin and length list
        var sumLength = 0L
        lengthDataList.forEach { (length, _) ->
            if (length < 0) {
                error("length do not support less than 0")
            }
            sumLength += length
        }
        val (firstSegment, lastSegment) = findFirstAndLastSegment(segmentList, true)
        if (firstSegment == null || lastSegment == null) {
            return emptyList()
        }
        if (begin >= lastSegment.end || (begin + sumLength) > lastSegment.end) {
            error("out of range(%s), begin:%s, (begin + sumLength):%s".format(lastSegment.end, begin, (begin + sumLength)))
        }
        val splitSegmentList = mutableListOf<Segment<T>>()
        val fixBegin = if (begin <= firstSegment.begin) {
            firstSegment.begin
        } else {
            splitSegmentList += Segment(begin = firstSegment.begin, end = begin, canUse = true)
            begin
        }
        val lengthDataIterator = lengthDataList.iterator()
        var newBegin = fixBegin
        var (remainLength, data) = lengthDataIterator.next()
        val segmentIterator = segmentList.iterator()
        var segment: Segment<T>?
        segment = segmentIterator.next()
        var id = 1L
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
                splitSegmentList += Segment(id, begin = newBegin, end = end, canUse = false, data = data)
                newBegin = end
                remainLength = 0
                data = null
            } else {
                splitSegmentList += Segment(id, begin = newBegin, end = segment.end, canUse = false, data = data)
                newBegin = segment.end
                remainLength = end - segment.end
            }
            if (remainLength == 0L) {
                id++
                if (lengthDataIterator.hasNext()) {
                    val remainLengthData = lengthDataIterator.next()
                    remainLength = remainLengthData.first
                    data = remainLengthData.second
                } else {//last segment and other segment in rear
                    if (end < segment.end) {
                        splitSegmentList += Segment(begin = end, end = segment.end, canUse = true)
                    }
                    while (segmentIterator.hasNext()) {
                        splitSegmentList += segmentIterator.next()
                    }
                    break
                }
            }
        }
        if (remainLength > 0 || lengthDataIterator.hasNext()) {
            logger.error("has remain, remain length in midway, remain:%s", remainLength)
            lengthDataIterator.forEach {
                logger.error("has remain, remain length in length list, remain:%s", it)
            }
        }
        return mergeSegmentList(splitSegmentList)
    }

    private fun <T> findFirstAndLastSegment(segmentList: List<Segment<T>>, canUse: Boolean): Pair<Segment<T>?, Segment<T>?> {
        var firstSegment: Segment<T>? = null
        var lastSegment: Segment<T>? = null
        for (segment in segmentList) {
            if (segment.canUse != canUse) {
                continue
            }
            if (firstSegment == null) {
                firstSegment = segment
            }
            lastSegment = segment
        }
        return firstSegment to lastSegment
    }

    private fun <T> mergeSegmentList(segmentList: List<Segment<T>>): List<Segment<T>> {
        if (segmentList.isEmpty() || segmentList.size == 1) {
            return segmentList
        }
        val mergeSegmentList = mutableListOf<Segment<T>>()
        val lastIndex = segmentList.size - 1
        var previousSegment: Segment<T>? = null
        var begin = 0L
        var end = 0L
        for ((index, segment) in segmentList.withIndex()) {
            if (previousSegment != null) {
                if (previousSegment.id != segment.id) {
                    mergeSegmentList += Segment(previousSegment.id, begin, end, previousSegment.canUse, previousSegment.data)
                    begin = segment.begin
                    end = segment.end
                    if (index == lastIndex) {//add the last
                        mergeSegmentList += segment
                    }
                } else {//previous id == segment id
                    if (previousSegment.canUse == segment.canUse) {
                        if (previousSegment.end == segment.begin) {
                            end = segment.end
                            if (index == lastIndex) {//add the last
                                mergeSegmentList += Segment(previousSegment.id, begin, end, previousSegment.canUse, previousSegment.data)
                            }
                        } else {
                            mergeSegmentList += Segment(previousSegment.id, begin, end, previousSegment.canUse, previousSegment.data)
                            begin = segment.begin
                            end = segment.end
                            if (index == lastIndex) {//add the last
                                mergeSegmentList += segment
                            }
                        }
                    } else {
                        logger.error("may be segment error, previous segment id:%s, segment id:%s", previousSegment.id, segment.id)
                    }
                }
            } else {
                begin = segment.begin
                end = segment.end
            }
            previousSegment = segment
        }
        return mergeSegmentList
    }

    private fun checkSegmentList(segmentList: List<Segment<*>>): Boolean {
        if (segmentList.isEmpty()) {
            return false
        }
        val segmentListSize = segmentList.size
        for (i in segmentList.indices) {
            val segment = segmentList[i]
            if (segment.end <= segment.begin) {
                error("segment end must greater than segment begin, begin:%s, end:%s".format(segment.begin, segment.end))
            }
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