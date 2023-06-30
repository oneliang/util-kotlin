package com.oneliang.ktx.util.common

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class MappingObject {

    @MustBeDocumented
    @Target(AnnotationTarget.FIELD, AnnotationTarget.FILE)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Key(
        val value: String
    )
}
