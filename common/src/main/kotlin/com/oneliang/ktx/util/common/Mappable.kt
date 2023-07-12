package com.oneliang.ktx.util.common

import java.lang.annotation.Inherited

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class Mappable {

    @MustBeDocumented
    @Target(AnnotationTarget.FIELD, AnnotationTarget.FILE)
    @Retention(AnnotationRetention.RUNTIME)
    @Inherited
    annotation class Key(
        val value: String
    )
}
