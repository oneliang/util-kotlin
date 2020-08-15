package com.oneliang.ktx.test.state

import com.oneliang.ktx.util.common.finds
import com.oneliang.ktx.util.state.State
import com.oneliang.ktx.util.state.StateMap

fun main() {
    //A->B->C,B->A
    val aState = State("A")
    val bState = State("B")
    val cState = State("C")
    aState.addNextState(bState)
    bState.addNextState(cState)
    bState.addNextState(aState)
    val stateMap = StateMap(aState)
    println(stateMap.checkNext("A", "B"))
    println(stateMap.checkNext("B", "A"))
    println(stateMap.checkNext("B", "C"))
    println(stateMap.checkNext("A", "C"))
    println(stateMap.checkPrevious("B", "A"))
    println(stateMap.checkPrevious("C", "B"))
    println(stateMap.checkPrevious("C", "A"))
}