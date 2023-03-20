package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.logging.LoggerManager
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass

/**
 * reflect the object property and invoke the method
 *
 * @author Dandelion
 * @since 2008-04-??
 */
object ObjectUtil {

    private val logger = LoggerManager.getLogger(ObjectUtil::class)

    /**
     * field name to method name
     * @param methodPrefix
     * @param fieldName
     * @param ignoreFirstLetterCase
     * @return methodName
     */
    fun fieldNameToMethodName(methodPrefix: String, fieldName: String, ignoreFirstLetterCase: Boolean = false): String {
        return if (fieldName.isNotEmpty()) {
            if (ignoreFirstLetterCase) {
                methodPrefix + fieldName
            } else {
                methodPrefix + fieldName.substring(0, 1).uppercase() + fieldName.substring(1)
            }
        } else {
            methodPrefix
        }
    }

    /**
     * method name to field name
     * @param methodPrefix
     * @param methodName
     * @param ignoreFirstLetterCase
     * @return fieldName
     */
    fun methodNameToFieldName(methodPrefix: String, methodName: String, ignoreFirstLetterCase: Boolean = false): String {
        return if (methodName.length > methodPrefix.length) {
            val front = methodPrefix.length
            if (ignoreFirstLetterCase) {
                methodName.substring(front, front + 1) + methodName.substring(front + 1)
            } else {
                methodName.substring(front, front + 1).lowercase() + methodName.substring(front + 1)
            }
        } else {
            Constants.String.BLANK
        }
    }

    /**
     * get field with method name,which start with method prefix get or is,not
     * not include method getClass()
     * @param methodName
     * get or is method
     * @param ignoreFirstLetterCase
     * @return String
     */
    fun methodNameToFieldName(methodName: String, ignoreFirstLetterCase: Boolean = false): String {
        return if (methodName.startsWith(Constants.Method.PREFIX_GET) && methodName != Constants.Method.GET_CLASS) {
            methodNameToFieldName(Constants.Method.PREFIX_GET, methodName, ignoreFirstLetterCase)
        } else if (methodName.startsWith(Constants.Method.PREFIX_IS)) {
            methodNameToFieldName(Constants.Method.PREFIX_IS, methodName, ignoreFirstLetterCase)
        } else {
            Constants.String.BLANK
        }
    }

    /**
     * get field with method name,which start with method prefix set
     * @param methodName
     * set method
     * @param ignoreFirstLetterCase
     * @return String
     */
    fun setMethodNameToFieldName(methodName: String, ignoreFirstLetterCase: Boolean = false): String {
        return if (methodName.startsWith(Constants.Method.PREFIX_SET)) {
            methodNameToFieldName(Constants.Method.PREFIX_SET, methodName, ignoreFirstLetterCase)
        } else {
            Constants.String.BLANK
        }
    }

    /**
     * get class all interface set
     * @param <T>
     * @param clazz
     * @return Set<Class></Class>>
    </T> */
    private fun <T : Any> getClassAllInterfaceSet(clazz: Class<T>): Set<Class<*>> {
        return getClassAllSuperclassAndAllInterfaceSet(isIncludeSelfClass = false, isAllSuperclass = false, isAllInterface = true, clazz = clazz)
    }

    /**
     * get class all superclass and all interface list include self class.
     * @param <T>
     * @param isIncludeSelfClass
     * @param isAllSuperclass
     * @param isAllInterface
     * @param clazz
     * @return Set<Class></Class>>
    </T> */
    fun <T : Any> getClassAllSuperclassAndAllInterfaceSet(isIncludeSelfClass: Boolean, isAllSuperclass: Boolean, isAllInterface: Boolean, clazz: Class<T>): Set<Class<*>> {
        val mutableSet = mutableSetOf<Class<*>>()
        val queue = ConcurrentLinkedQueue<Class<*>>()
        queue.add(clazz)
        if (isIncludeSelfClass) {
            mutableSet.add(clazz)
        }
        while (!queue.isEmpty()) {
            val currentClass = queue.poll()
            val superclass = currentClass.superclass
            if (superclass != null) {
                queue.add(superclass)
                if (isAllSuperclass) {
                    if (!mutableSet.contains(superclass)) {
                        mutableSet.add(superclass)
                    }
                }
            }
            if (isAllInterface) {
                val interfaces = currentClass.interfaces
                if (interfaces == null || interfaces.isEmpty()) {
                    continue
                }
                for (interfaceClass in interfaces) {
                    queue.add(interfaceClass)
                    if (!mutableSet.contains(interfaceClass)) {
                        mutableSet.add(interfaceClass)
                    }
                }
            }
        }
        return mutableSet
    }

    /**
     * getClassAllInterfaces
     * @param <T>
     * @param clazz
     * @return Class[]
    </T> */
    fun <T : Any> getClassAllInterfaces(clazz: Class<T>): Array<Class<*>> {
        val set = getClassAllInterfaceSet(clazz)
        return set.toTypedArray()
    }

    /**
     * is interface implement
     * @param implement
     * @param interfaceClass
     * @return boolean
     */
    fun isInterfaceImplement(implement: Class<*>, interfaceClass: Class<*>): Boolean {
        val set = getClassAllInterfaceSet(implement)
        return set.contains(interfaceClass)
    }

    /**
     * just objectClass is it the inheritance or interface implement of clazz
     * @param objectClass
     * @param clazz
     * @return boolean
     */
    fun isInheritanceOrInterfaceImplement(objectClass: Class<*>, clazz: Class<*>): Boolean {
        val set = getClassAllSuperclassAndAllInterfaceSet(isIncludeSelfClass = true, isAllSuperclass = true, isAllInterface = true, clazz = objectClass)
        return set.contains(clazz)
    }

    /**
     * judge the object is it the entity of class or interface
     * @param instance
     * @param clazz
     * @return boolean
     */
    fun isEntity(instance: Any, clazz: Class<*>): Boolean {
        return isInheritanceOrInterfaceImplement(instance.javaClass, clazz)
    }

    /**
     * judge the object is it the entity of class or interface
     * @param instance
     * @param kClass
     * @return boolean
     */
    fun isEntity(instance: Any, kClass: KClass<*>): Boolean {
        return isEntity(instance, kClass.java)
    }

    /**
     * invoke getter or is method for field
     * @param instance
     * @param fieldName
     * @param ignoreFirstLetterCase
     * @return Object
     */
    fun getterOrIsMethodInvoke(instance: Any, fieldName: String, ignoreFirstLetterCase: Boolean = false): Any? {
        val value: Any?
        var methodName = fieldNameToMethodName(Constants.Method.PREFIX_GET, fieldName, ignoreFirstLetterCase)
        var method: Method
        val instanceClass = instance.javaClass
        if ((instanceClass.modifiers and Modifier.PUBLIC) != Modifier.PUBLIC || instanceClass == Class::class.java) {
            return null
        }
        try {
            method = instance.javaClass.getMethod(methodName)
        } catch (e: Exception) {
            methodName = fieldNameToMethodName(Constants.Method.PREFIX_IS, fieldName, ignoreFirstLetterCase)
            try {
                method = instance.javaClass.getMethod(methodName)
            } catch (ex: Exception) {
                throw ObjectUtilException("No getter or is method for field:$fieldName", ex)
            }
        }

        try {
            value = method.invoke(instance)
        } catch (e: Exception) {
            throw ObjectUtilException("Invoke method exception, instance:${instance}, method:$methodName, field:$fieldName", e)
        }
        return value
    }

    /**
     * read object
     * @param inputStream
     * @return Object
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : java.io.Serializable> readObject(inputStream: InputStream): T? {
        var value: T? = null
        var objectInputStream: ObjectInputStream? = null
        try {
            objectInputStream = ObjectInputStream(inputStream)
            value = objectInputStream.readObject() as T?
        } catch (e: Exception) {
            logger.warning("Read exception:" + e.message)
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close()
                } catch (e: Exception) {
                    logger.warning("Read close exception:" + e.message)
                }
            }
        }
        return value
    }

    /**
     * write object
     * @param outputStream
     * @param instance
     */
    fun <T : java.io.Serializable> writeObject(outputStream: OutputStream, instance: T) {
        var objectOutputStream: ObjectOutputStream? = null
        try {
            objectOutputStream = ObjectOutputStream(outputStream)
            objectOutputStream.writeObject(instance)
            objectOutputStream.flush()
        } catch (e: Exception) {
            logger.warning("Write exception:" + e.message)
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close()
                } catch (e: Exception) {
                    logger.warning("Write close exception:" + e.message)
                }

            }
        }
    }


    /**
     * new instance
     * @param clazz
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> newInstance(clazz: Class<*>, parameterTypes: Array<Class<*>>, parameterValues: Array<Any>): T {
        val value: T
        try {
            value = clazz.getConstructor(*parameterTypes).newInstance(*parameterValues) as T
        } catch (e: Exception) {
            throw ObjectUtilException(e)
        }
        return value
    }

    /**
     * method invoke
     * @param instance
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    fun <T : Any> methodInvoke(instance: Any, methodName: String, parameterTypes: Array<Class<*>>, parameterValues: Array<Any>): T? {
        return methodInvoke(instance.javaClass, instance, methodName, parameterTypes, parameterValues)
    }

    /**
     * method invoke
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    fun <T : Any> methodInvoke(clazz: Class<*>, methodName: String, parameterTypes: Array<Class<*>>, parameterValues: Array<Any>): T? {
        return methodInvoke(clazz, null, methodName, parameterTypes, parameterValues)
    }

    /**
     * method invoke
     * @param clazz
     * @param instance
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> methodInvoke(clazz: Class<*>, instance: Any?, methodName: String, parameterTypes: Array<Class<*>>, parameterValues: Array<Any>): T? {
        val value: T?
        try {
            value = clazz.getMethod(methodName, *parameterTypes).invoke(instance, *parameterValues) as T
        } catch (e: Exception) {
            throw ObjectUtilException(e)
        }
        return value
    }

    class ObjectUtilException : RuntimeException {
        constructor(message: String, cause: Throwable) : super(message, cause)
        constructor(cause: Throwable) : super(cause)
        constructor(message: String) : super(message)
    }
}