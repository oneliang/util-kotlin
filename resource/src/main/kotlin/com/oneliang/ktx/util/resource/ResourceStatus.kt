package com.oneliang.ktx.util.resource

class ResourceStatus<T : Any>(var resource: T) {

    /**
     * @return the inUse
     */
    /**
     * @param inUse the inUse to set
     */
    @Volatile
    var isInUse = false
    /**
     * @return the lastNotInUseTime
     */
    /**
     * @param lastNotInUseTime the lastNotInUseTime to set
     */
    var lastNotInUseTime: Long = 0
}
