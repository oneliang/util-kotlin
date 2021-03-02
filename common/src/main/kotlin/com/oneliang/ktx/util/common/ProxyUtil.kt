package com.oneliang.ktx.util.common

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

object ProxyUtil {

    /**
     * Method: return the proxy interface of the interfaces of object
     * @param <T>
     * @param classLoader can not be null
     * @param instance can not be null
     * @param handler can not be null
     * @return proxy interface
    </T> */
    fun <T : Any> newProxyInstance(classLoader: ClassLoader, instance: T, handler: InvocationHandler): Any {
        val interfaces = ObjectUtil.getClassAllInterfaces(instance.javaClass)
        if (interfaces.isEmpty()) {
            error("Can not create proxy instance:%s, because no any interface in this instance, please check it.".format(instance))
        }
        return Proxy.newProxyInstance(classLoader, interfaces, handler)
    }
}
