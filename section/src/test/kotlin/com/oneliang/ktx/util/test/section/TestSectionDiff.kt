package com.oneliang.ktx.util.test.section

import com.oneliang.ktx.util.common.toHexString
import com.oneliang.ktx.util.section.Section
import com.oneliang.ktx.util.section.SectionDiff

fun main() {
    val oldSectionList = listOf(
        object : Section {
            override fun toByteArray(): ByteArray {
                return "12345".toByteArray()
            }
        },
        object : Section {
            override fun toByteArray(): ByteArray {
                return "67890".toByteArray()
            }
        }
    )
    val newSectionList = listOf(
        object : Section {
            override fun toByteArray(): ByteArray {
                return "12345".toByteArray()
            }
        }
    )
    val sectionDiffData = SectionDiff.diff(oldSectionList, newSectionList)
    println(sectionDiffData.sectionPositionMoveList.size)
    println(sectionDiffData.toByteArray().toHexString())
    val newSectionByteArray = SectionDiff.patch(oldSectionList, sectionDiffData.sectionPositionMoveList, sectionDiffData.sectionPositionIncreaseList)
    println(newSectionList[0].toByteArray().toHexString() + "," + newSectionByteArray.toHexString())
}