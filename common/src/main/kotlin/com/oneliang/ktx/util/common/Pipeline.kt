package com.oneliang.ktx.util.common

fun <F, M, L> simplePipeline(firstBlock: () -> F, middleBlock: (F) -> M, lastBlock: (F, M) -> L): L {
    val first = firstBlock()
    val middle = middleBlock(first)
    return lastBlock(first, middle)
}