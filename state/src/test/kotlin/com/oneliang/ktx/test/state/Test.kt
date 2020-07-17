package com.oneliang.ktx.test.state

import com.oneliang.ktx.util.state.State
import com.oneliang.ktx.util.state.StateMap

fun main() {
    //A->B->C
    val startState = State("A").apply {
        this.addNextState(State("B").apply {
            this.addNextState(State("C"))
        })
    }
    val stateMap = StateMap(startState)
    println(stateMap.checkNext("A","B"))
    println(stateMap.checkNext("B","C"))
    println(stateMap.checkNext("A","C"))
    println(stateMap.checkPrevious("B","A"))
    println(stateMap.checkPrevious("C","B"))
    println(stateMap.checkPrevious("C","A"))
}