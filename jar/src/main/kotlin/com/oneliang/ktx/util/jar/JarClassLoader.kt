package com.oneliang.ktx.util.jar

import java.net.URL
import java.net.URLClassLoader

/**
 * JarClassLoader
 */
class JarClassLoader(parentClassLoader: ClassLoader) : URLClassLoader(emptyArray(), parentClassLoader) {

    /**
     * add url,make the protected method to public method
     */
    public override fun addURL(url: URL) {
        super.addURL(url)
    }
}
