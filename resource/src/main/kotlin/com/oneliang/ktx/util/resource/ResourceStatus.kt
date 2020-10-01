package com.oneliang.ktx.util.resource

class ResourceStatus<T : Any>(var resource: T) {

    @Volatile
    var inUse = false
    var lastNotInUseTime: Long = 0
}
