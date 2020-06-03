package com.oneliang.ktx.util.common

fun Int.calculateCompose(composeSize: Int) = MathUtil.calculateCompose(this, composeSize)

fun Int.calculateCompose(composeSize: Int, block: (Array<Int>) -> Unit) = MathUtil.calculateCompose(this, composeSize, block)