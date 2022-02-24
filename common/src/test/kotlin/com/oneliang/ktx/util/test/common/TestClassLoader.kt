package com.oneliang.ktx.util.test.common

import com.oneliang.ktx.Constants
import java.io.File

class TestClassLoader : ClassLoader() {

    override fun findClass(name: String?): Class<*> {
        val classFullFilename = File("D:/Dandelion/java/githubWorkspace/frame-kotlin/adjuster", name + Constants.Symbol.DOT + Constants.File.CLASS)
        val classByteArray = classFullFilename.readBytes()
        return this.defineClass(name, classByteArray, 0, classByteArray.size)
    }
}