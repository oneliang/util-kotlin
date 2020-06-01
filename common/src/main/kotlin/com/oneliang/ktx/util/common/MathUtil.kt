package com.oneliang.ktx.util.common

import java.util.*

object MathUtil {
    fun calculateCompose(totalSize: Int, composeSize: Int, block: (Array<Int>) -> Unit) {
        calculateCompose(Stack(), totalSize, composeSize, 0, 0, block)
    }

    private fun calculateCompose(stack: Stack<Int>, totalSize: Int, composeSize: Int, depth: Int, startIndex: Int, block: (Array<Int>) -> Unit) {
        if (depth == composeSize) {
            block(stack.toTypedArray())
            return
        }

        for (index in startIndex + 1..totalSize) {
            stack.push(index)
            calculateCompose(stack, totalSize, composeSize, depth + 1, index, block)
            stack.pop()
        }
    }
}