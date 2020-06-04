package com.oneliang.ktx.util.common

fun Int.calculateCompose(composeSize: Int, outputIndex: Boolean = false) = MathUtil.calculateCompose(this, composeSize, outputIndex)

fun Int.calculateCompose(composeSize: Int, outputIndex: Boolean = false, block: (Array<Int>) -> Unit) = MathUtil.calculateCompose(this, composeSize, outputIndex, block)