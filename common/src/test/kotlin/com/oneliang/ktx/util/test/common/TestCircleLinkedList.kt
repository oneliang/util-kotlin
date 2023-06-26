package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.util.common.CircleIterator

fun main() {
    val circleLinkedList = CircleIterator(arrayOf(1, 2, 3))
    println(circleLinkedList.next())
    println(circleLinkedList.next())
    println(circleLinkedList.next())
    println(circleLinkedList.next())
    println(circleLinkedList.next())
}