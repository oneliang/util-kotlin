package com.oneliang.ktx.util.section

import com.oneliang.ktx.util.common.MD5String
import com.oneliang.ktx.util.common.toHexString
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayOutputStream

object SectionDiff {
    private val logger = LoggerManager.getLogger(SectionDiff::class)

    fun diff(oldSectionList: List<Section>, newSectionList: List<Section>): SectionDiffData {
        val sectionPositionMoveList: MutableList<SectionPosition> = mutableListOf()
        val sectionPositionIncreaseList: MutableList<SectionPosition> = mutableListOf()
        val oldSectionMap: MutableMap<String, MutableSet<Int>> = HashMap()
        for (i in oldSectionList.indices) {
            val oldSection = oldSectionList[i]
            val key: String = oldSection.toByteArray().toHexString()
            val positionSet = oldSectionMap.getOrPut(key) { mutableSetOf() }
            positionSet += i
        }
        logger.verbose("old section list size:%s,old section map size:%s", oldSectionList.size, oldSectionMap.size)
        for (i in newSectionList.indices) {
            val newSection = newSectionList[i]
            val key: String = newSection.toByteArray().toHexString()
            if (oldSectionMap.containsKey(key)) {
                val oldPositionSet: Set<Int> = oldSectionMap[key]!!
                var j = i
                j = if (oldPositionSet.contains(i)) {
                    i // same position
                } else {
                    oldPositionSet.iterator().next() // pick one
                }
                sectionPositionMoveList.add(SectionPosition(j, i))
                if (i != j) {
                    logger.verbose("section find in old index(old->new)(index: %s -> %s)", j, i)
                } else {
                    logger.verbose("section find in old index(old->new)(index: %s -> %s),no need to move,but copy", j, i)
                }
            } else {
                sectionPositionIncreaseList.add(SectionPosition(-1, i, newSection.toByteArray()))
                logger.verbose("section increase in new index(new)(index: %s, value: %s)", i, newSection.toByteArray().toHexString())
            }
        }
        for (sectionPosition in sectionPositionMoveList) {
            logger.verbose("move: %s", sectionPosition)
        }
        for (sectionPosition in sectionPositionIncreaseList) {
            logger.verbose("increase: %s", sectionPosition)
        }
        return SectionDiffData(sectionPositionMoveList, sectionPositionIncreaseList)
    }

    fun patch(oldSectionList: List<Section>, newSectionPositionMoveList: List<SectionPosition>, newSectionPositionIncreaseList: List<SectionPosition>): ByteArray {
        logger.verbose("----------start patch----------")
        var newSectionSize = 0
        newSectionSize += newSectionPositionMoveList.size
        newSectionSize += newSectionPositionIncreaseList.size
        val newSectionArray = arrayOfNulls<Section>(newSectionSize)
        // move
        for (sectionPosition in newSectionPositionMoveList) {
            val fromIndex = sectionPosition.fromIndex
            val toIndex = sectionPosition.toIndex
            if (fromIndex == SectionDiffData.EMPTY_FROM) {
                logger.verbose("may be increase, index:%s", toIndex)
                continue
            }
            // if (toIndex + 1 > newSectionList.length) {
            // Section[] tempNewSectionArray = new Section[toIndex + 1];
            // System.arraycopy(newSectionList, 0, tempNewSectionArray, 0,
            // newSectionList.length);
            // newSectionList = tempNewSectionArray;
            // }
            // move fromIndex to toIndex
            logger.verbose("from: %s ,to: %s, hex: %s", fromIndex, toIndex, oldSectionList[fromIndex].toByteArray().toHexString())
            newSectionArray[toIndex] = oldSectionList[fromIndex]
        }
        // increase
        for (sectionPosition in newSectionPositionIncreaseList) {
            val toIndex = sectionPosition.toIndex // new index,maybe
            // bigger then old
            // index
            // if (toIndex + 1 > newSectionList.length) {
            // Section[] tempNewSectionArray = new Section[toIndex + 1];
            // System.arraycopy(newSectionList, 0, tempNewSectionArray, 0,
            // newSectionList.length);
            // newSectionList = tempNewSectionArray;
            // }
            val byteArray = sectionPosition.byteArray
            newSectionArray[toIndex] = UnitSection(byteArray)
        }
        // merge data
        val byteArrayOutputStream = ByteArrayOutputStream()
        for ((index, newSection) in newSectionArray.withIndex()) {
            if (newSection != null) {
                byteArrayOutputStream.write(newSection.toByteArray())
                logger.verbose("index: %s, value: %s", index, newSection.toByteArray().toHexString())
            } else {
                logger.error("error, can no be null, may be a bug, index:%s", index);
            }
        }
        return byteArrayOutputStream.toByteArray()
    }

    fun printSectionList(sectionList: List<Section>) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        for (section in sectionList) {
            byteArrayOutputStream.write(section.toByteArray())
        }
        logger.verbose("length: %s, MD5: %s", byteArrayOutputStream.toByteArray().size, byteArrayOutputStream.toByteArray().MD5String())
    }
}