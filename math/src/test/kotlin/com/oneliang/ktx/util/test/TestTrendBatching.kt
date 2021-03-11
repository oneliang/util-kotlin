package com.oneliang.ktx.util.test

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.math.algebra.Batching
import java.io.File

class TestTrendBatching(override val batchSize: Int) : Batching(batchSize) {

    private val fullFilename = "/C:/Users/Administrator/Desktop/input.csv"
    private var reader = File(this.fullFilename).bufferedReader()

    private var lineCount = 0
    private var lineIndex = 0

    override fun reset() {
        this.lineCount = 0
        this.lineIndex = 0
        this.reader = File(this.fullFilename).bufferedReader()
    }

    private fun parseLine(line: String): Pair<Double, Array<Double>> {
        val rowDataList = line.split(Constants.Symbol.COMMA)
        var result = 0.0
        val dataArray = Array(rowDataList.size - 1) { 0.0 }
        rowDataList.forEachIndexed { index: Int, string: String ->
            val value = string.trim().toDouble()
            if (index == 0) {
                result = value / 1000
            } else if (index in 1..2 || index == 4 || index == 6 || index in 10..15) {
                dataArray[index - 1] = value / 1000
            } else if (index == 5 || index in 7..9) {
                dataArray[index - 1] = value / 10000
            } else if (index == 16) {
                dataArray[index - 1] = value / 100000
            } else if (index == 17) {
                dataArray[index - 1] = value / 100
            } else {
                dataArray[index - 1] = value
            }
        }
        return result to dataArray
    }

    override fun fetch(): Result {
        var currentLineCount = 0
        var line = this.reader.readLine() ?: null
        val dataList = mutableListOf<Pair<Double, Array<Double>>>()
        while (line != null) {//break when finished
            if (line.isNotBlank()) {
                if (this.lineIndex == 0) {
                    this.lineIndex++
                    line = this.reader.readLine() ?: null
                    continue
                }
                dataList += parseLine(line)
                currentLineCount++
                this.lineIndex++
                this.lineCount++
                if (currentLineCount == this.batchSize) {
                    break
                }
            }
            line = this.reader.readLine() ?: null
        }
        return if (dataList.isEmpty()) {
            Result(true)
        } else {
            Result(false, dataList)
        }
    }
}