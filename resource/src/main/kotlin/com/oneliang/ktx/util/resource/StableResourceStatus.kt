package com.oneliang.ktx.util.resource

class StableResourceStatus<T : Any>(var resource: T) {

    var lastNotInUseTime: Long = 0
}
