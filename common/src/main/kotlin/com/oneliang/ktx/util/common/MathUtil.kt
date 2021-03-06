package com.oneliang.ktx.util.common

import java.util.*

object MathUtil {
    fun calculatePiece(number: Long, modulus: Long): Long {
        return calculatePieceAndRemainder(number, modulus).first
    }

    fun calculatePiece(number: Int, modulus: Int): Int {
        return calculatePieceAndRemainder(number, modulus).first
    }

    fun calculateRemainder(number: Long, modulus: Long): Long {
        return calculatePieceAndRemainder(number, modulus).second
    }

    fun calculateRemainder(number: Int, modulus: Int): Int {
        return calculatePieceAndRemainder(number, modulus).second
    }

    fun calculatePieceAndRemainder(number: Int, modulus: Int): Pair<Int, Int> {
        val pair = calculatePieceAndRemainder(number.toLong(), modulus.toLong())
        return pair.first.toInt() to pair.second.toInt()
    }

    fun calculatePieceAndRemainder(number: Long, modulus: Long): Pair<Long, Long> {
        if (number < 0) {
            error("number must >= 0")
        }
        if (modulus <= 0) {
            error("modulus must > 0")
        }
        val remainder = number % modulus
        val piece = if (remainder == 0L) {
            number / modulus
        } else {
            number / modulus + 1
        }
        return piece to remainder
    }

    fun calculateCompose(totalSize: Int, composeSize: Int, outputIndex: Boolean = false): List<Array<Int>> {
        val list = mutableListOf<Array<Int>>()
        calculateCompose(totalSize, composeSize, outputIndex) {
            list += it
        }
        return list
    }

    fun calculateCompose(totalSize: Int, composeSize: Int, block: (Array<Int>) -> Unit) {
        calculateCompose(Stack(), totalSize, composeSize, 0, 0, false, block)
    }

    fun calculateCompose(totalSize: Int, composeSize: Int, outputIndex: Boolean = false, block: (Array<Int>) -> Unit) {
        calculateCompose(Stack(), totalSize, composeSize, 0, 0, outputIndex, block)
    }

    private fun calculateCompose(stack: Stack<Int>, totalSize: Int, composeSize: Int, depth: Int, startIndex: Int, outputIndex: Boolean = false, block: (Array<Int>) -> Unit) {
        if (depth == composeSize) {
            block(stack.toTypedArray())
            return
        }

        for (index in startIndex until totalSize) {
            stack.push(if (outputIndex) index else index + 1)
            calculateCompose(stack, totalSize, composeSize, depth + 1, index + 1, outputIndex, block)
            stack.pop()
        }
    }

    fun calculateIndex(totalRows: Int, rowsPerPage: Int, currentPage: Int): Pair<Int, Int> {
        val fromIndex = (currentPage - 1) * rowsPerPage
        val toIndex = if (currentPage * rowsPerPage >= totalRows) {
            totalRows
        } else {
            currentPage * rowsPerPage
        }
        return fromIndex to toIndex
    }
}