package com.oneliang.ktx.util.resource

fun <T : Any> ResourcePool<T>.useResource(block: (resource: T) -> Unit, destroy: Boolean) {
    val resource = this.resource
    try {
        block(resource)
    } finally {
        this.releaseResource(resource, destroy)
    }
}


fun <T : Any> ResourcePool<T>.useStableResource(block: (resource: T) -> Unit, destroy: Boolean) {
    val resource = this.stableResource
    try {
        block(resource)
    } finally {
        this.releaseStableResource(resource, destroy)
    }
}