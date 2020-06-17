package com.oneliang.ktx.util.validator

import com.oneliang.ktx.Constants
import com.oneliang.ktx.util.common.KotlinClassUtil
import com.oneliang.ktx.util.common.ObjectUtil
import com.oneliang.ktx.util.common.matches
import java.lang.reflect.Field

object Validator {
    val DEFAULT_VALIDATE_PROCESSOR: ValidateProcessor = DefaultValidateProcessor()

    /**
     * validate
     * @param instance
     * @param validateProcessor
     * @return List<ViolateConstrain>
     * @throws Exception
    </ViolateConstrain> */
    @Throws(Exception::class)
    fun validate(instance: Any, validateProcessor: ValidateProcessor = DEFAULT_VALIDATE_PROCESSOR): List<ViolateConstrain> {
        val fields = instance.javaClass.declaredFields ?: return emptyList()
        val violateConstrainList = mutableListOf<ViolateConstrain>()
        for (field in fields) {
            val violateConstrain = validateProcessor.validateProcess(instance, field)
            if (violateConstrain != null) {
                violateConstrainList.add(violateConstrain)
            }
        }
        return violateConstrainList
    }

    interface ValidateProcessor {
        /**
         * validate processor
         * @param instance
         * @param field
         * @return ViolateConstrain
         * @throws Exception
         */
        @Throws(Exception::class)
        fun validateProcess(instance: Any, field: Field): ViolateConstrain?
    }

    class DefaultValidateProcessor : ValidateProcessor {
        /**
         * validate processor
         * @param instance
         * @param field
         * @return ViolateConstrain
         * @throws Exception
         */
        @Throws(Exception::class)
        override fun validateProcess(instance: Any, field: Field): ViolateConstrain? {
            var violateConstrain: ViolateConstrain? = null
            val fieldName = field.name
            if (field.isAnnotationPresent(Numeric::class.java)) {
                val numeric = field.getAnnotation(Numeric::class.java)
                val fieldValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName)
                if (fieldValue != null) {
                    val value: Long = KotlinClassUtil.changeType(Long::class, arrayOf(fieldValue.toString())) ?: 0L
                    val min = numeric.min
                    val max = numeric.max
                    when {
                        min > max -> {
                            throw Exception("min:$min is larger then max:$max")
                        }
                        value < min -> {
                            violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, $value must >= $min")
                        }
                        value > max -> {
                            violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, $value must <= $max")
                        }
                    }
                } else {
                    val nullable = numeric.nullable
                    if (!nullable) {
                        violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, can not be null")
                    }
                }
            } else if (field.isAnnotationPresent(Decimal::class.java)) {
                val decimal = field.getAnnotation(Decimal::class.java)
                val fieldValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName)
                if (fieldValue != null) {
                    val value: Double = KotlinClassUtil.changeType(Double::class, arrayOf(fieldValue.toString())) ?: 0.0
                    val min = decimal.min
                    val max = decimal.max
                    when {
                        min > max -> {
                            throw Exception("min:$min is larger then max:$max")
                        }
                        value < min -> {
                            violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, $value must >= $min")
                        }
                        value > max -> {
                            violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, $value must <= $max")
                        }
                    }
                } else {
                    val nullable = decimal.nullable
                    if (!nullable) {
                        violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, can not be null")
                    }
                }
            } else if (field.isAnnotationPresent(Length::class.java)) {
                val length = field.getAnnotation(Length::class.java)
                val fieldValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName)
                if (fieldValue != null) {
                    val value: String = KotlinClassUtil.changeType(String::class, arrayOf(fieldValue.toString())) ?: Constants.String.BLANK
                    val min = length.min
                    val max = length.max
                    when {
                        min > max -> {
                            throw Exception("min:$min is larger then max:$max")
                        }
                        value.length < min -> {
                            violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, $value must >= $min")
                        }
                        value.length > max -> {
                            violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, $value must <= $max")
                        }
                    }
                } else {
                    val nullable = length.nullable
                    if (!nullable) {
                        violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, can not be null")
                    }
                }
            } else if (field.isAnnotationPresent(Regex::class.java)) {
                val regex = field.getAnnotation(Regex::class.java)
                val fieldValue = ObjectUtil.getterOrIsMethodInvoke(instance, fieldName)
                if (fieldValue != null) {
                    val value: String = KotlinClassUtil.changeType(String::class, arrayOf(fieldValue.toString())) ?: Constants.String.BLANK
                    val regexArray = regex.value
                    for (regexValue in regexArray) {
                        if (!value.matches(regexValue)) {
                            violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, $value mush match regex value:$regexValue")
                            break
                        }
                    }
                } else {
                    val nullable = regex.nullable
                    if (!nullable) {
                        violateConstrain = ViolateConstrain(fieldName, "The field:($fieldName) is violate constrain, can not be null")
                    }
                }
            }
            return violateConstrain
        }
    }

    class ViolateConstrain(val fieldName: String, val result: String)
}