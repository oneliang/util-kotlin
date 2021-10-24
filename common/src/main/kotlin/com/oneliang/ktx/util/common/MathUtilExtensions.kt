package com.oneliang.ktx.util.common

fun Int.calculateCompose(composeSize: Int, outputIndex: Boolean = false) = MathUtil.calculateCompose(this, composeSize, outputIndex)

fun Int.calculateCompose(composeSize: Int, outputIndex: Boolean = false, block: (Array<Int>) -> Unit) = MathUtil.calculateCompose(this, composeSize, outputIndex, block)

fun Int.calculatePiece(modulus: Int): Int = MathUtil.calculatePiece(this, modulus)

fun Int.calculateRemainder(modulus: Int): Int = MathUtil.calculateRemainder(this, modulus)

fun Int.calculatePieceAndRemainder(modulus: Int): Pair<Int, Int> = MathUtil.calculatePieceAndRemainder(this, modulus)

fun Long.calculatePiece(modulus: Long): Long = MathUtil.calculatePiece(this, modulus)

fun Long.calculateRemainder(modulus: Long): Long = MathUtil.calculateRemainder(this, modulus)

fun Long.calculatePieceAndRemainder(modulus: Long): Pair<Long, Long> = MathUtil.calculatePieceAndRemainder(this, modulus)

fun Int.calculateIndex(rowsPerPage: Int, currentPage: Int): Pair<Int, Int> = MathUtil.calculateIndex(this, rowsPerPage, currentPage)

inline fun <reified T> Array<T>.calculateCompose(composeSize: Int) = MathUtil.calculateCompose(this, composeSize)

fun <T> Array<T>.calculatePermutation(start: Int = 0) = MathUtil.calculatePermutation(this, 0)

fun <T> Array<T>.calculatePermutation(start: Int = 0, block: (Array<T>) -> Unit) = MathUtil.calculatePermutation(this, 0, block)