package com.oneliang.ktx.util.jar

import com.oneliang.ktx.Constants
import com.oneliang.ktx.exception.FileLoadException
import java.io.FileInputStream
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import kotlin.reflect.KClass

object JarUtil {

    private val jarClassCacheMap = ConcurrentHashMap<String, List<KClass<*>>>()

    /**
     * extract from jar file
     * @param jarFileRealPath
     * @return List<JarEntry>
     * @throws FileLoadException
    </JarEntry> */
    @Throws(FileLoadException::class)
    fun extractFromJarFile(jarFileRealPath: String): List<JarEntry> {
        val jarEntryList = mutableListOf<JarEntry>()
        JarInputStream(FileInputStream(jarFileRealPath)).use { jarInputStream ->
            var jarEntry: JarEntry? = jarInputStream.nextJarEntry
            while (jarEntry != null) {
                jarEntryList.add(jarEntry)
                jarEntry = jarInputStream.nextJarEntry
            }
        }
        return jarEntryList
    }

    /**
     * search class list
     * @param jarClassLoader
     * @param jarFileRealPath
     * @param searchPackageName
     * @param annotationClass
     * @return List<Class></Class>>
     * @throws FileLoadException
     */
    @Throws(FileLoadException::class)
    fun searchClassList(jarClassLoader: JarClassLoader, jarFileRealPath: String, searchPackageName: String, annotationClass: KClass<out Annotation>): List<KClass<*>> {
        val classList = mutableListOf<KClass<*>>()
        val allClassList = extractClassFromJarFile(jarClassLoader, jarFileRealPath, searchPackageName)
        for (clazz in allClassList) {
            if (clazz.java.isAnnotationPresent(annotationClass.java)) {
                classList.add(clazz)
            }
        }
        return classList
    }

    /**
     * extract class from jar file
     * @param jarClassLoader
     * @param jarFileRealPath
     * @return List<Class></Class>>
     * @throws FileLoadException
     */
    @Throws(FileLoadException::class)
    fun extractClassFromJarFile(jarClassLoader: JarClassLoader, jarFileRealPath: String, packageName: String = Constants.String.BLANK): List<KClass<*>> {
        val jarClassCacheKey = generateJarClassCacheKey(jarClassLoader, jarFileRealPath, packageName)
        if (this.jarClassCacheMap.containsKey(jarClassCacheKey)) {
            val classList = jarClassCacheMap[jarClassCacheKey]
            if (classList != null) {
                return classList
            }
        }
        val classList = mutableListOf<KClass<*>>()
        if (jarFileRealPath.isBlank()) {
            return classList
        }
        jarClassLoader.addURL(URL(Constants.Protocol.FILE + jarFileRealPath))
        JarInputStream(FileInputStream(jarFileRealPath)).use { jarInputStream ->
            var jarEntry: JarEntry? = jarInputStream.nextJarEntry
            while (jarEntry != null) {
                var entryName = jarEntry.name
                if (entryName.endsWith(Constants.Symbol.DOT + Constants.File.CLASS)) {
                    entryName = entryName.substring(0, entryName.length - (Constants.Symbol.DOT + Constants.File.CLASS).length)
                    val className = entryName.replace(Constants.Symbol.SLASH_LEFT, Constants.Symbol.DOT)
                    var sign = false
                    if (packageName.isBlank()) {
                        sign = true
                    } else {
                        if (className.startsWith(packageName)) {
                            sign = true
                        }
                    }
                    if (sign) {
                        val clazz = jarClassLoader.loadClass(className)
                        classList.add(clazz.kotlin)
                    }
                }
                jarEntry = jarInputStream.nextJarEntry
            }
        }
        this.jarClassCacheMap[jarClassCacheKey] = classList
        return classList
    }

    private fun generateJarClassCacheKey(jarClassLoader: JarClassLoader, jarFileRealPath: String, packageName: String): String {
        return jarClassLoader.toString() + Constants.Symbol.COMMA + jarFileRealPath + Constants.Symbol.COMMA + packageName
    }
}