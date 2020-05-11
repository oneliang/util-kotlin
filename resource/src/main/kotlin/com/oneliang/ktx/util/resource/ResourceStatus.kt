package com.oneliang.ktx.util.resource

class ResourceStatus<T : Any> {

    /**
     * @return the resource
     */
    /**
     * @param resource the resource to set
     */
    var resource: T? = null
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
