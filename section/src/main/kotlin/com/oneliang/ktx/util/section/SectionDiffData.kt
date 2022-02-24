package com.oneliang.ktx.util.section

import com.oneliang.ktx.util.common.toByteArray
import com.oneliang.ktx.util.common.toInt
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class SectionDiffData(
    val sectionPositionMoveList: List<SectionPosition>,
    val sectionPositionIncreaseList: List<SectionPosition>
) {

    companion object {
        internal val logger = LoggerManager.getLogger(SectionDiffData::class)
        const val OPCODE_MOVE = 0
        const val OPCODE_INCREASE = 1
        const val OPCODE_MERGE_MOVE = 2
        const val EMPTY_FROM = -1 // 0xffffffff
        private const val MODE_MOVE = 0
        private const val MODE_MERGE_MOVE = 1
    }

    private var mode = MODE_MOVE
    private var sectionSize = 0
    private var mergeSectionPositionList: List<MergeSectionPosition> = emptyList()

    init {
        sectionSize += this.sectionPositionMoveList.size
        sectionSize += this.sectionPositionIncreaseList.size
        val moveByteSize = sectionPositionMoveList.size * 2 * 4
        logger.verbose("move total byte size:%s", moveByteSize)
//        val mergeSectionPositionList = mergeSectionPosition(sectionPositionMoveList)
//        val mergeMoveByteSize = mergeSectionPositionList.size * 4 * 4
//        logger.verbose("merge move total byte size:%s", mergeMoveByteSize)
//        if (mergeMoveByteSize < moveByteSize) {
//            mode = MODE_MERGE_MOVE
//            this.mergeSectionPositionList = mergeSectionPositionList
//        } else {
//            this.mergeSectionPositionList = emptyList()
//        }
        logger.verbose("mode:%s", mode)
    }

    @Deprecated("Deprecated")
    fun toByteArray2(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        if (sectionPositionMoveList.isNotEmpty()) {
            val moveListOutputStream = ByteArrayOutputStream()
            for (sectionPosition in sectionPositionMoveList) {
                moveListOutputStream.write(sectionPosition.fromIndex.toByteArray())
                moveListOutputStream.write(sectionPosition.toIndex.toByteArray())
            }
            byteArrayOutputStream.write(OPCODE_MOVE.toByteArray())
            val moveByteArray = moveListOutputStream.toByteArray()
            byteArrayOutputStream.write(moveByteArray.size.toByteArray())
            byteArrayOutputStream.write(moveByteArray)
        }
        if (sectionPositionIncreaseList.isNotEmpty()) {
            val increaseListOutputStream = ByteArrayOutputStream()
            for (sectionPosition in sectionPositionIncreaseList) {
                increaseListOutputStream.write(sectionPosition.toIndex.toByteArray())
                val sectionByteArray = sectionPosition.byteArray
                increaseListOutputStream.write(sectionByteArray.size.toByteArray())
                increaseListOutputStream.write(sectionByteArray)
            }
            byteArrayOutputStream.write(OPCODE_INCREASE.toByteArray())
            val increaseByteArray = increaseListOutputStream.toByteArray()
            byteArrayOutputStream.write(increaseByteArray.size.toByteArray())
            byteArrayOutputStream.write(increaseByteArray)
        }
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * move: type[4]length[4]data[[4][4][4]...]
     *
     * merge move:type[4]length[4]data[to[4][4]from[4][4]...]
     *
     * increase:type[4]length[4]data[index[4]length[4]data[n]]
     *
     * @return byte[]
     */
    fun toByteArray(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        if (mode == MODE_MOVE) {
            val moveArray = IntArray(sectionSize)
            for (i in moveArray.indices) {
                moveArray[i] = EMPTY_FROM
            }
            if (sectionPositionMoveList.isNotEmpty()) {
                byteArrayOutputStream.write(OPCODE_MOVE.toByteArray())
                byteArrayOutputStream.write((moveArray.size * 4).toByteArray())
                for (sectionPosition in sectionPositionMoveList) {
                    moveArray[sectionPosition.toIndex] = sectionPosition.fromIndex
                }
                for (i in moveArray.indices) {
                    byteArrayOutputStream.write(moveArray[i].toByteArray())
                }
            }
        } else {
            if (mergeSectionPositionList.isNotEmpty()) {
                val mergeMoveLength = mergeSectionPositionList.size
                byteArrayOutputStream.write(OPCODE_MERGE_MOVE.toByteArray())
                byteArrayOutputStream.write((mergeMoveLength * 4 * 4).toByteArray())
                for (mergeSectionPosition in mergeSectionPositionList) {
                    byteArrayOutputStream.write(mergeSectionPosition.newBegin.toByteArray())
                    byteArrayOutputStream.write(mergeSectionPosition.newEnd.toByteArray())
                    byteArrayOutputStream.write(mergeSectionPosition.oldBegin.toByteArray())
                    byteArrayOutputStream.write(mergeSectionPosition.oldEnd.toByteArray())
                }
            }
        }
        if (sectionPositionIncreaseList.isNotEmpty()) {
            val increaseListOutputStream = ByteArrayOutputStream()
            for (sectionPosition in sectionPositionIncreaseList) {
                val sectionByteArray = sectionPosition.byteArray
                increaseListOutputStream.write(sectionPosition.toIndex.toByteArray())
                increaseListOutputStream.write(sectionByteArray.size.toByteArray())
                increaseListOutputStream.write(sectionByteArray)
            }
            val increaseByteArray = increaseListOutputStream.toByteArray()
            byteArrayOutputStream.write(OPCODE_INCREASE.toByteArray())
            byteArrayOutputStream.write(increaseByteArray.size.toByteArray())
            byteArrayOutputStream.write(increaseByteArray)
        }
        return byteArrayOutputStream.toByteArray()
    }
}

/**
 * merge section position
 *
 * @param sectionPositionMoveList
 * @return List<MergeSectionPosition>
</MergeSectionPosition> */
fun SectionDiffData.Companion.mergeSectionPosition(sectionPositionMoveList: List<SectionPosition>): List<MergeSectionPosition> {
    val mergeSectionPositionList = mutableListOf<MergeSectionPosition>()
    var previous: SectionPosition? = null
    var merge: MergeSectionPosition? = null
    for (current in sectionPositionMoveList) {
        if (previous != null) {
            if (current.fromIndex - previous.fromIndex == 1 && current.toIndex - previous.toIndex == 1) {
                // System.out.println(String.format("Section
                // merge,f(%s->%s),t(%s->%s)",pre.getFromIndex(),cur.getFromIndex(),pre.getToIndex(),cur.getToIndex()));
                if (merge == null) {
                    merge = MergeSectionPosition()
                    mergeSectionPositionList.add(merge)
                }
                if (merge.oldBegin == -1) {
                    merge.oldBegin = previous.fromIndex
                }
                merge.oldEnd = current.fromIndex
                if (merge.newBegin == -1) {
                    merge.newBegin = previous.toIndex
                }
                merge.newEnd = current.toIndex
                // System.out.println(String.format("after
                // merge:(%s->%s),new:(%s->%s)",merge.oldBegin,merge.oldEnd,merge.newBegin,merge.newEnd));
            } else {
                merge = null
                val original = MergeSectionPosition()
                original.oldBegin = previous.fromIndex
                original.oldEnd = previous.fromIndex
                original.newBegin = previous.toIndex
                original.newEnd = previous.toIndex
                mergeSectionPositionList.add(original)
            }
        }
        previous = current
    }
    for (mergeSectionPosition in mergeSectionPositionList) {
        logger.verbose("after merge:to:(%s ~ %s),from:(%s ~ %s)", mergeSectionPosition.newBegin, mergeSectionPosition.newEnd, mergeSectionPosition.oldBegin, mergeSectionPosition.oldEnd)
    }
    return mergeSectionPositionList
}

/**
 * parse from
 *
 * @param byteArray
 * @return SectionDiffData
 */
fun SectionDiffData.Companion.parseFrom(byteArray: ByteArray): SectionDiffData {
    val byteArrayInputStream = ByteArrayInputStream(byteArray)
    return parseFrom(byteArrayInputStream)
}

/**
 * parse from
 *
 * @param inputStream
 * @return SectionDiffData
 */
fun SectionDiffData.Companion.parseFrom(inputStream: InputStream): SectionDiffData {
    val sectionPositionMoveList = mutableListOf<SectionPosition>()
    val sectionPositionIncreaseList = mutableListOf<SectionPosition>()
    val buffer = ByteArray(4)
    inputStream.read(buffer)
    val opCodeQueue: Queue<Int> = ConcurrentLinkedQueue()
    var opCode: Int = buffer.toInt()
    opCodeQueue.add(opCode)
    while (!opCodeQueue.isEmpty()) {
        opCode = opCodeQueue.poll()
        when (opCode) {
            OPCODE_MOVE -> {
                inputStream.read(buffer)
                val allSectionLength: Int = buffer.toInt() / 4 // all
                // section
                // length
                var i = 0
                while (i < allSectionLength) {
                    inputStream.read(buffer)
                    val fromIndex: Int = buffer.toInt()
                    sectionPositionMoveList.add(SectionPosition(fromIndex, i))
                    i++
                }
                val length = inputStream.read(buffer)
                if (length > 0) {
                    opCode = buffer.toInt()
                    opCodeQueue.add(opCode)
                }
            }
            OPCODE_INCREASE -> {
                inputStream.read(buffer)
                var totalLength: Int = buffer.toInt()
                while (totalLength > 0) {
                    totalLength -= inputStream.read(buffer)
                    val toIndex: Int = buffer.toInt()
                    totalLength -= inputStream.read(buffer)
                    val valueLength: Int = buffer.toInt()
                    val valueBuffer = ByteArray(valueLength)
                    totalLength -= inputStream.read(valueBuffer)
                    sectionPositionIncreaseList.add(SectionPosition(-1, toIndex, valueBuffer))
                }
            }
            OPCODE_MERGE_MOVE -> {
                inputStream.read(buffer)
                val allSectionLength: Int = buffer.toInt() / 4 / 4 // all
                // section
                // length
                var i = 0
                while (i < allSectionLength) {
                    inputStream.read(buffer)
                    val toBeginIndex: Int = buffer.toInt()
                    inputStream.read(buffer)
                    val toEndIndex: Int = buffer.toInt()
                    inputStream.read(buffer)
                    val fromBeginIndex: Int = buffer.toInt()
                    inputStream.read(buffer)
                    val fromEndIndex: Int = buffer.toInt()
                    if (toEndIndex - toBeginIndex != fromEndIndex - fromBeginIndex) {
                        error(String.format("diff data error.to(%s->%s)from(%s->%s),", toBeginIndex, toEndIndex, fromBeginIndex, fromEndIndex))
                    } else {
                        val count = toEndIndex - toBeginIndex
                        var j = 0
                        while (j <= count) {
                            sectionPositionMoveList.add(SectionPosition(fromBeginIndex + j, toBeginIndex + j))
                            j++
                        }
                    }
                    i++
                }
                val length = inputStream.read(buffer)
                if (length > 0) {
                    opCode = buffer.toInt()
                    opCodeQueue.add(opCode)
                }
            }
        }
    }
    return SectionDiffData(sectionPositionMoveList, sectionPositionIncreaseList)
}