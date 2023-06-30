package com.oneliang.ktx.util.common

import com.oneliang.ktx.Constants
import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass

/**
 * kotlin class has no base class(like int, long, short and so on)
 */
object KotlinClassUtil {

    private val classTypeMap = mutableMapOf<KClass<*>, ClassType>()

    private val simpleClassMap = mutableMapOf<KClass<*>, KClass<*>>()
    private val baseArrayMap = mutableMapOf<KClass<*>, KClass<*>>()
    private val simpleArrayMap = mutableMapOf<KClass<*>, KClass<*>>()
    private val simpleClassNameMap = mutableMapOf<String, KClass<*>>()

    val DEFAULT_KOTLIN_CLASS_PROCESSOR: KotlinClassProcessor = DefaultKotlinClassProcessor()

    enum class ClassType {
        KOTLIN_STRING, KOTLIN_CHARACTER, KOTLIN_SHORT, KOTLIN_INTEGER, KOTLIN_LONG,
        KOTLIN_FLOAT, KOTLIN_DOUBLE, KOTLIN_BOOLEAN, KOTLIN_BYTE, JAVA_UTIL_DATE, JAVA_MATH_BIG_DECIMAL,

        //        CHAR, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN,
        KOTLIN_STRING_ARRAY,
        KOTLIN_CHAR_ARRAY, KOTLIN_SHORT_ARRAY, KOTLIN_INT_ARRAY, KOTLIN_LONG_ARRAY,
        KOTLIN_FLOAT_ARRAY, KOTLIN_DOUBLE_ARRAY, KOTLIN_BOOLEAN_ARRAY, KOTLIN_BYTE_ARRAY,
        CHAR_ARRAY, BYTE_ARRAY, SHORT_ARRAY, INT_ARRAY, LONG_ARRAY, FLOAT_ARRAY, DOUBLE_ARRAY, BOOLEAN_ARRAY
    }

    init {
        classTypeMap[String::class] = ClassType.KOTLIN_STRING
        classTypeMap[Char::class] = ClassType.KOTLIN_CHARACTER
        classTypeMap[Short::class] = ClassType.KOTLIN_SHORT
        classTypeMap[Int::class] = ClassType.KOTLIN_INTEGER
        classTypeMap[Long::class] = ClassType.KOTLIN_LONG
        classTypeMap[Float::class] = ClassType.KOTLIN_FLOAT
        classTypeMap[Double::class] = ClassType.KOTLIN_DOUBLE
        classTypeMap[Boolean::class] = ClassType.KOTLIN_BOOLEAN
        classTypeMap[Byte::class] = ClassType.KOTLIN_BYTE
        classTypeMap[Date::class] = ClassType.JAVA_UTIL_DATE
        classTypeMap[BigDecimal::class] = ClassType.JAVA_MATH_BIG_DECIMAL

        classTypeMap[Array<String>::class] = ClassType.KOTLIN_STRING_ARRAY
        classTypeMap[Array<Char>::class] = ClassType.KOTLIN_CHAR_ARRAY
        classTypeMap[Array<Short>::class] = ClassType.KOTLIN_SHORT_ARRAY
        classTypeMap[Array<Int>::class] = ClassType.KOTLIN_INT_ARRAY
        classTypeMap[Array<Long>::class] = ClassType.KOTLIN_LONG_ARRAY
        classTypeMap[Array<Float>::class] = ClassType.KOTLIN_FLOAT_ARRAY
        classTypeMap[Array<Double>::class] = ClassType.KOTLIN_DOUBLE_ARRAY
        classTypeMap[Array<Boolean>::class] = ClassType.KOTLIN_BOOLEAN_ARRAY
        classTypeMap[Array<Byte>::class] = ClassType.KOTLIN_BYTE_ARRAY
        classTypeMap[CharArray::class] = ClassType.CHAR_ARRAY
        classTypeMap[ByteArray::class] = ClassType.BYTE_ARRAY
        classTypeMap[ShortArray::class] = ClassType.SHORT_ARRAY
        classTypeMap[IntArray::class] = ClassType.INT_ARRAY
        classTypeMap[LongArray::class] = ClassType.LONG_ARRAY
        classTypeMap[FloatArray::class] = ClassType.FLOAT_ARRAY
        classTypeMap[DoubleArray::class] = ClassType.DOUBLE_ARRAY
        classTypeMap[BooleanArray::class] = ClassType.BOOLEAN_ARRAY

        simpleClassMap[String::class] = String::class
        simpleClassMap[Char::class] = Char::class
        simpleClassMap[Byte::class] = Byte::class
        simpleClassMap[Short::class] = Short::class
        simpleClassMap[Int::class] = Int::class
        simpleClassMap[Long::class] = Long::class
        simpleClassMap[Float::class] = Float::class
        simpleClassMap[Double::class] = Double::class
        simpleClassMap[Boolean::class] = Boolean::class

        baseArrayMap[CharArray::class] = CharArray::class
        baseArrayMap[ByteArray::class] = ByteArray::class
        baseArrayMap[ShortArray::class] = ShortArray::class
        baseArrayMap[IntArray::class] = IntArray::class
        baseArrayMap[LongArray::class] = LongArray::class
        baseArrayMap[FloatArray::class] = FloatArray::class
        baseArrayMap[DoubleArray::class] = DoubleArray::class
        baseArrayMap[BooleanArray::class] = BooleanArray::class

        simpleArrayMap[Array<String>::class] = Array<String>::class
        simpleArrayMap[Array<Char>::class] = Array<Char>::class
        simpleArrayMap[Array<Short>::class] = Array<Short>::class
        simpleArrayMap[Array<Int>::class] = Array<Int>::class
        simpleArrayMap[Array<Long>::class] = Array<Long>::class
        simpleArrayMap[Array<Float>::class] = Array<Float>::class
        simpleArrayMap[Array<Double>::class] = Array<Double>::class
        simpleArrayMap[Array<Boolean>::class] = Array<Boolean>::class
        simpleArrayMap[Array<Byte>::class] = Array<Byte>::class

        simpleClassNameMap[String::class.qualifiedName!!] = String::class
        simpleClassNameMap[Char::class.qualifiedName!!] = Char::class
        simpleClassNameMap[Byte::class.qualifiedName!!] = Byte::class
        simpleClassNameMap[Short::class.qualifiedName!!] = Short::class
        simpleClassNameMap[Int::class.qualifiedName!!] = Int::class
        simpleClassNameMap[Long::class.qualifiedName!!] = Long::class
        simpleClassNameMap[Float::class.qualifiedName!!] = Float::class
        simpleClassNameMap[Double::class.qualifiedName!!] = Double::class
        simpleClassNameMap[Boolean::class.qualifiedName!!] = Boolean::class
    }

    /**
     * get class with class name
     * @param className
     * @return Type
     * @throws Exception class not found
     */
    fun getClass(classLoader: ClassLoader, className: String): KClass<*>? {
        return if (simpleClassNameMap.containsKey(className)) {
            simpleClassNameMap[className]
        } else {
            classLoader.loadClass(className).kotlin
        }
    }

    /**
     * getClassType,for manual judge use
     * @param kClass
     * @return ClassType
     */
    fun getClassType(kClass: KClass<*>): ClassType? {
        return classTypeMap[kClass]
    }

    /**
     * simple class or not
     * include Boolean Short Integer Long Float Double Byte String
     * @param kClass
     * @return boolean
     */
    fun isSimpleClass(kClass: KClass<*>): Boolean {
        return simpleClassMap.containsKey(kClass)
    }

    /**
     * simple class or not
     * include Boolean Short Integer Long Float Double Byte String
     * @param className
     * @return boolean
     */
    fun isSimpleClass(className: String): Boolean {
        return simpleClassNameMap.containsKey(className)
    }

    /**
     * basic array or not
     * @param kClass
     * @return boolean
     */
    fun isBaseArray(kClass: KClass<*>): Boolean {
        return baseArrayMap.containsKey(kClass)
    }

    /**
     * simple array or not
     * @param kClass
     * @return boolean
     */
    fun isSimpleArray(kClass: KClass<*>): Boolean {
        return simpleArrayMap.containsKey(kClass)
    }

    /**
     * change type width class processor
     * @param <T>
     * @param kClass
     * @param values
     * @param fieldName is null if not exist
     * @param classProcessor
     * @return Object
    </T> */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> changeType(kClass: KClass<T>, values: Array<String>, fieldName: String = Constants.String.BLANK, classProcessor: KotlinClassProcessor = DEFAULT_KOTLIN_CLASS_PROCESSOR): T? {
        return classProcessor.changeClassProcess(kClass, values, fieldName, null) as T?
    }

    /**
     * change type width class processor
     * @param <T>
     * @param kClass
     * @param values
     * @param fieldName is null if not exist
     * @param classProcessor
     * @param specialParameter for special use
     * @return Object
    </T> */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any, SP> changeType(kClass: KClass<T>, values: Array<String>, fieldName: String = Constants.String.BLANK, classProcessor: KotlinClassProcessor = DEFAULT_KOTLIN_CLASS_PROCESSOR, specialParameter: SP?): T? {
        return classProcessor.changeClassProcess(kClass, values, fieldName, specialParameter) as T?
    }

    interface KotlinClassProcessor {

        /**
         * change class process
         * @param kClass
         * @param values
         * @param fieldName is empty if not exist
         * @param specialParameter for special use
         * @return Object
         */
        fun <T : Any, SP> changeClassProcess(kClass: KClass<T>, values: Array<String>, fieldName: String, specialParameter: SP? = null): Any?
    }
}
