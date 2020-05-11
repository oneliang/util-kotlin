package com.oneliang.ktx.util.resource

class StableResourceStatus<T : Any> {

    var resource: T? = null
    var lastNotInUseTime: Long = 0
}
