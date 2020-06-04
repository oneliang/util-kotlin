package com.oneliang.ktx.util.common

import java.util.*

object MathUtil {
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
}